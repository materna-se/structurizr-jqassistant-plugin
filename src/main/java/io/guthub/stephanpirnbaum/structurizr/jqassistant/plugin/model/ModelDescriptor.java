package io.guthub.stephanpirnbaum.structurizr.jqassistant.plugin.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.structurizr.Workspace;

import java.util.List;

/**
 * Descriptor representing the {@link Workspace#getModel()}.
 *
 * @author Stephan Pirnbaum
 */
@Label("Model")
public interface ModelDescriptor extends StructurizrDescriptor {

    @Relation("HAS")
    List<ElementDescriptor> getElements();

}
