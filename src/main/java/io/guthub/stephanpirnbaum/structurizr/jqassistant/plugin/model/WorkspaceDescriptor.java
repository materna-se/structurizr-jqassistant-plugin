package io.guthub.stephanpirnbaum.structurizr.jqassistant.plugin.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Descriptor representing a Structurizr {@link com.structurizr.Workspace}.
 *
 * @author Stephan Pirnbaum
 */
@Label("Workspace")
public interface WorkspaceDescriptor extends StructurizrDescriptor {

    void setModel(ModelDescriptor model);

    @Relation("CONTAINS")
    ModelDescriptor getModel();

}
