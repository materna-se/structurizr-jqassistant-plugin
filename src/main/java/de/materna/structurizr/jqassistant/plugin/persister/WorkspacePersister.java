package de.materna.structurizr.jqassistant.plugin.persister;

import com.buschmais.jqassistant.core.store.api.Store;
import com.structurizr.Workspace;
import com.structurizr.model.Element;
import com.structurizr.model.Model;
import com.structurizr.model.Relationship;
import de.materna.structurizr.jqassistant.plugin.model.ComponentDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.ContainerDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.ElementDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.ModelDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.PersonDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.SystemDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.WorkspaceDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class to persist a {@link Workspace}.
 *
 * @author Stephan Pirnbaum
 */
@RequiredArgsConstructor
@Slf4j
public class WorkspacePersister {

    private final Store store;

    public WorkspaceDescriptor persist(Workspace workspace) {
        WorkspaceDescriptor workspaceDescriptor = store.create(WorkspaceDescriptor.class);
        if (workspace.getModel() != null) {
            Model model = workspace.getModel();
            ModelDescriptor modelDescriptor = store.create(ModelDescriptor.class);
            workspaceDescriptor.setName(workspace.getName());
            workspaceDescriptor.setModel(modelDescriptor);
            Map<String, ElementDescriptor> elementIdMap = persistElements(modelDescriptor, model.getElements());
            persistRelations(elementIdMap, model.getRelationships());
        }
        return workspaceDescriptor;
    }

    private Map<String, ElementDescriptor> persistElements(ModelDescriptor modelDescriptor, Set<Element> elements) {
        Map<String, ElementDescriptor> elementIdMap = new HashMap<>();
        ElementPersister elementPersister = new ElementPersister(this.store);
        for (Element element : elements) {
            ElementDescriptor elementDescriptor = elementPersister.persist(element);
            if (elementDescriptor != null) {
                modelDescriptor.getElements().add(elementDescriptor);
                elementIdMap.put(element.getId(), elementDescriptor);
            }
        }

        for (Element element : elements) {
            if (element.getParent() != null) {
                ElementDescriptor parent = elementIdMap.get(element.getParent().getId());
                ElementDescriptor child = elementIdMap.get(element.getId());
                if (child instanceof ComponentDescriptor) {
                    parent.getContainedComponents().add((ComponentDescriptor) child);
                } else if (child instanceof ContainerDescriptor) {
                    parent.getContainedContainers().add((ContainerDescriptor) child);
                } else if (child instanceof SystemDescriptor) {
                    parent.getContainedSystems().add((SystemDescriptor) child);
                } else if (child instanceof PersonDescriptor) {
                    parent.getContainedPersons().add((PersonDescriptor) child);
                }
            }
        }
        return elementIdMap;
    }

    private void persistRelations(Map<String, ElementDescriptor> elementIdMap, Set<Relationship> relationships) {
        RelationshipPersister relationshipPersister = new RelationshipPersister(this.store);
        for (Relationship relationship : relationships) {
            ElementDescriptor from = elementIdMap.get(relationship.getSourceId());
            ElementDescriptor to = elementIdMap.get(relationship.getDestinationId());
            if (from != null && to != null) {
                relationshipPersister.persist(from, to, relationship);
            }
        }
    }


}
