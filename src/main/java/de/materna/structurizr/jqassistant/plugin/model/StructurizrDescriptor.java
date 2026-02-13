package de.materna.structurizr.jqassistant.plugin.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Base descriptor for all types resulting from a Structurizr {@link com.structurizr.Workspace}.
 *
 * @author Stephan Pirnbaum
 */
@Label("Structurizr")
public interface StructurizrDescriptor extends Descriptor {
}
