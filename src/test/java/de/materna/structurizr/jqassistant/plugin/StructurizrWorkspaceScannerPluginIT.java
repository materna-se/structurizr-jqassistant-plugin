package de.materna.structurizr.jqassistant.plugin;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StructurizrWorkspaceScannerPluginIT extends AbstractPluginIT {

    private Workspace workspace;

    @BeforeEach
    public void setUp() throws StructurizrDslParserException {
        File testFile = new File(getClassesDirectory(this.getClass()), "workspace-full.dsl");
        getScanner().scan(testFile, "workspace-full.dsl", DefaultScope.NONE);

        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(testFile);
        this.workspace = parser.getWorkspace();
    }

    @Test
    public void workspaceFull() {
        String workspaceQuery = "MATCH (w:Structurizr:Workspace{name: '" + this.workspace.getName() + "'}) RETURN count(*) AS cnt";
        assertThat(query(workspaceQuery).getColumn("cnt").get(0)).isEqualTo(1L);

        for (Element element : this.workspace.getModel().getElements()) {
            String technology = null;
            if (element instanceof Component) {
                technology = ((Component) element).getTechnology();
            } else if (element instanceof Container) {
                technology = ((Container) element).getTechnology();
            }

            String elementQuery = String.format(
                    "MATCH (e:Structurizr:Element%s{alias: '%s', name: '%s'%s}) RETURN count(*) AS cnt",
                    element.getTagsAsSet().stream().map(s -> s.replace("Software System", "System")).map(s -> ":" + s).collect(Collectors.joining()),
                    element.getProperties().get("structurizr.dsl.identifier"),
                    element.getName(),
                    StringUtils.isNotBlank(technology) ? ", technologies: ['" + technology + "']" : ""
            );
            assertThat(query(elementQuery).getColumn("cnt").get(0)).isEqualTo(1L);
        }

        for (Element element : this.workspace.getModel().getElements()) {
            if (element.getParent() != null) {
                String containsQuery = String.format(
                        "MATCH (e1:Structurizr:Element{alias: '%s'})-[:CONTAINS]->(e2:Structurizr:Element{alias: '%s'}) RETURN count(*) AS cnt",
                        element.getParent().getProperties().get("structurizr.dsl.identifier"),
                        element.getProperties().get("structurizr.dsl.identifier")
                );

                assertThat(query(containsQuery).getColumn("cnt").get(0)).isEqualTo(1L);
            }
        }

        for (Relationship relationship : this.workspace.getModel().getRelationships()) {
            String sourceAlias = relationship.getSource().getProperties().get("structurizr.dsl.identifier");
            String tag = relationship.getTags().replace("Relationship", "").replace(",", "");
            String properties = relationship.getProperties().entrySet().stream().map(e -> e.getKey() + ": '" + e.getValue() + "'").collect(Collectors.joining(", "));
            String destinationAlias = relationship.getDestination().getProperties().get("structurizr.dsl.identifier");
            String relationshipQuery = String.format(
                    "MATCH (e1:Structurizr:Element{alias: '%s'})-[d:%s%s]->(e2:Structurizr:Element{alias: '%s'}) Return count(*) AS cnt",
                    sourceAlias,
                    StringUtils.isNotBlank(tag) ? tag : "DEFINES_DEPENDENCY",
                    StringUtils.isNotBlank(properties) ? "{" + properties + "}" : "",
                    destinationAlias
            );
            assertThat(query(relationshipQuery).getColumn("cnt").get(0)).isEqualTo(1L);
        }
    }

}
