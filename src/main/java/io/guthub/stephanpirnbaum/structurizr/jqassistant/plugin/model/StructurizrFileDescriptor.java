package io.guthub.stephanpirnbaum.structurizr.jqassistant.plugin.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Descriptor representing a .dsl file containing a Structurizr {@link com.structurizr.Workspace}.
 *
 * @author Stephan Pirnbaum
 */
public interface StructurizrFileDescriptor extends StructurizrDescriptor, FileDescriptor {

    @Relation("CONTAINS")
    WorkspaceDescriptor getWorkspace();

    void setWorkspace(WorkspaceDescriptor workspace);

}
