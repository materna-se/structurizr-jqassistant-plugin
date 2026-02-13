package de.materna.structurizr.jqassistant.plugin.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import de.materna.structurizr.jqassistant.plugin.report.StructurizrLanguage;

/**
 * Descriptor for a {@link com.structurizr.model.Person}.
 *
 * @author Stephan Pirnbaum
 */
@StructurizrLanguage(StructurizrLanguage.StructurizrLanguageElement.Person)
@Label("Person")
public interface PersonDescriptor extends ElementDescriptor {

}
