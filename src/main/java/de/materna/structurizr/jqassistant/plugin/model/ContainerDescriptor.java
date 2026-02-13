package de.materna.structurizr.jqassistant.plugin.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import de.materna.structurizr.jqassistant.plugin.report.StructurizrLanguage;

/**
 * Descriptor for a {@link com.structurizr.model.Container}.
 *
 * @author Stephan Pirnbaum
 */
@StructurizrLanguage(StructurizrLanguage.StructurizrLanguageElement.Container)
@Label("Container")
public interface ContainerDescriptor extends ElementDescriptor {

}
