package de.materna.structurizr.jqassistant.plugin.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import de.materna.structurizr.jqassistant.plugin.report.StructurizrLanguage;

/**
 * Descriptor for a {@link com.structurizr.model.SoftwareSystem}.
 *
 * @author Stephan Pirnbaum
 */
@StructurizrLanguage(StructurizrLanguage.StructurizrLanguageElement.System)
@Label("System")
public interface SystemDescriptor extends ElementDescriptor {

}
