package de.materna.structurizr.jqassistant.plugin.persister;

import com.buschmais.jqassistant.core.store.api.Store;
import com.structurizr.Workspace;
import com.structurizr.model.Relationship;
import de.materna.structurizr.jqassistant.plugin.model.ElementDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to persist a single {@link Relationship} from a Structurizr {@link Workspace#getModel()}.
 *
 * @author Stephan Pirnbaum
 */
@RequiredArgsConstructor
@Slf4j
public class RelationshipPersister {

    private static final String relCreationTemplate =
            "MATCH (s:Structurizr:Element), (t:Structurizr:Element) " +
            "WHERE id(s) = %d AND id(t) = %d " +
            "MERGE (s)-[d%s]->(t) " +
            "RETURN d";

    private final Store store;

    public void persist(ElementDescriptor from, ElementDescriptor to, Relationship relationship) {
        String query = String.format(relCreationTemplate,
                (Long) from.getId(),
                (Long) to.getId(),
                buildStringRepresentation(from.getAlias(), to.getAlias(), relationship));
        this.store.executeQuery(query);
    }

    private String buildStringRepresentation(String sourceAlias, String targetAlias, Relationship relationship) {
        String label;
        Set<String> tags = relationship.getTagsAsSet();
        // default name of Relationships
        tags.remove("Relationship");
        if (tags.size() > 1) {
            label = tags.stream().findFirst().get();
            log.warn("Relation between {} and {} has more then one tag. Using {}", sourceAlias, targetAlias, label);
        } else if (tags.size() == 1) {
            label = tags.stream().findFirst().get();
        } else {
            label = "DEPENDS_ON";
            log.warn("Relation between {} and {} has no tags. Using default {}", sourceAlias, targetAlias, label);
        }

        return String.format(":%s{%s%s%s}",
                label.replaceAll(" ", "_"),
                buildDescriptionString(relationship),
                buildTechnologiesString(relationship),
                buildPropertiesString(relationship));
    }


    private String buildDescriptionString(Relationship relationship) {
        if (StringUtils.isEmpty(relationship.getDescription())) {
            return "";
        } else {
            return "description: \"" + relationship.getDescription() + "\"";
        }
    }

    private String buildPropertiesString(Relationship relationship) {
        if (MapUtils.isEmpty(relationship.getProperties())) {
            return "";
        } else {
            return ", " + relationship.getProperties().entrySet()
                    .stream()
                    .map(e -> e.getKey() + ": \"" + e.getValue() + "\"")
                    .collect(Collectors.joining(", "));
        }
    }

    private String buildTechnologiesString(Relationship relationship) {
        if (StringUtils.isEmpty(relationship.getTechnology())) {
            return "";
        } else {
            return ", technologies: [" +
                    Arrays.stream(relationship.getTechnology().split(","))
                            .map(s -> "\"" + s + "\"")
                            .collect(Collectors.joining(", "))
                    + "]";
        }
    }
}