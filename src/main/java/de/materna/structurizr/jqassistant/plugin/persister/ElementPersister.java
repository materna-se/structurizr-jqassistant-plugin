package de.materna.structurizr.jqassistant.plugin.persister;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.xo.api.Query;
import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import de.materna.structurizr.jqassistant.plugin.model.ElementDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to persist a single {@link Element} from a Structurizr {@link Workspace#getModel()}.
 * Currently supported are:
 *
 * <ul>
 *     <li>{@link Component}</li>
 *     <li>{@link Container}</li>
 *     <li>{@link SoftwareSystem}</li>
 *     <li>{@link Person}</li>
 * </ul>
 *
 * @author Stephan Pirnbaum
 */
@RequiredArgsConstructor
@Slf4j
public class ElementPersister {

    private static final String elementCreationTemplate = "CREATE (n%s) RETURN n";

    private final Store store;

    public ElementDescriptor persist(Element element) {
        String stringRepresentation = buildStringRepresentation(element);
        if (stringRepresentation != null) {
            String query = String.format(elementCreationTemplate, stringRepresentation);
            List<Query.Result.CompositeRowObject> resultList;
            try (Query.Result<Query.Result.CompositeRowObject> result = this.store.executeQuery(query)) {
                resultList = IteratorUtils.toList(result.iterator());
            }
            if (resultList.size() == 1) {
                return resultList.get(0).get("n", ElementDescriptor.class);
            }
        }
        return null;
    }

    private String buildStringRepresentation(Element element) {
        if (element instanceof Component) {
            return String.format("%s{%s%s%s%s%s}",
                    buildLabelString(element, "Component"),
                    buildAliasString(element),
                    buildNameString(element),
                    buildDescriptionString(element),
                    buildTechnologiesString(((Component) element).getTechnology()),
                    buildPropertiesString(element));
        } else if (element instanceof Container) {
            return String.format("%s{%s%s%s%s%s}",
                    buildLabelString(element, "Container"),
                    buildAliasString(element),
                    buildNameString(element),
                    buildDescriptionString(element),
                    buildTechnologiesString(((Container) element).getTechnology()),
                    buildPropertiesString(element));
        } else if (element instanceof SoftwareSystem) {
            return String.format("%s{%s%s%s%s}",
                    buildLabelString(element, "System"),
                    buildAliasString(element),
                    buildNameString(element),
                    buildDescriptionString(element),
                    buildPropertiesString(element));
        } else if (element instanceof Person) {
            return String.format("%s{%s%s%s%s}",
                    buildLabelString(element, "Person"),
                    buildAliasString(element),
                    buildNameString(element),
                    buildDescriptionString(element),
                    buildPropertiesString(element));
        } else {
            log.warn("Element of type {} currently not supported.", element.getClass().getSimpleName());
            return null;
        }
    }

    final String buildAliasString(Element element) {
        if (MapUtils.isNotEmpty(element.getProperties())) {
            if (element.getProperties().containsKey("structurizr.dsl.identifier")) {
                return "alias: \"" + element.getProperties().get("structurizr.dsl.identifier") + "\"";
            }
        }
        return "";
    }

    final String buildNameString(Element element) {
        return ", name: \"" + element.getName() + "\"";
    }

    final String buildLabelString(Element element, String typeLabel) {
        Set<String> labels = new LinkedHashSet<>();
        labels.add("Structurizr");
        labels.add(typeLabel);
        labels.addAll(element.getTagsAsSet());
        return ":" + String.join(":", labels).replaceAll(" ", "_");
    }

    private String buildDescriptionString(Element element) {
        if (StringUtils.isNotEmpty(element.getDescription())) {
            return ", description: \"" + element.getDescription() + "\"";
        } else {
            return "";
        }
    }

    private String buildTechnologiesString(String technology) {
        if (StringUtils.isNotEmpty(technology)) {
            return ", technologies: [" +
                    Arrays.stream(technology.split(","))
                            .map(s -> "\"" + s + "\"")
                            .collect(Collectors.joining(", "))
                    + "]";

        } else {
            return "";
        }
    }

    private String buildPropertiesString(Element element) {
        if (MapUtils.isNotEmpty(element.getProperties())) {
            String properties = element.getProperties().entrySet()
                    .stream()
                    .filter(e -> !Objects.equals(e.getKey(), "structurizr.dsl.identifier"))
                    .map(e -> e.getKey().replaceAll(" ", "") + ": \"" + e.getValue() + "\"")
                    .collect(Collectors.joining(", "));
            if (StringUtils.isEmpty(properties)) {
                return "";
            } else {
                return  ", " + properties;
            }
        } else {
            return "";
        }
    }

}
