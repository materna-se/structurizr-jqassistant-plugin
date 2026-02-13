package de.materna.structurizr.jqassistant.plugin.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Base descriptor for all elements of the model of the Structurizr {@link com.structurizr.Workspace}.
 *
 * @author Stephan Pirnbaum
 */
@Label("Element")
public interface ElementDescriptor extends StructurizrDescriptor {

    String getAlias();

    void setAlias(String alias);

    String getName();

    void setName(String name);

    @Relation("CONTAINS")
    List<ComponentDescriptor> getContainedComponents();

    @Relation("CONTAINS")
    List<ContainerDescriptor> getContainedContainers();

    @Relation("CONTAINS")
    List<SystemDescriptor> getContainedSystems();

    @Relation("CONTAINS")
    List<PersonDescriptor> getContainedPersons();

}
