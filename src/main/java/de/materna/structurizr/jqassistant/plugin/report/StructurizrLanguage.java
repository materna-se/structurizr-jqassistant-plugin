package de.materna.structurizr.jqassistant.plugin.report;

import com.buschmais.jqassistant.core.report.api.SourceProvider;
import com.buschmais.jqassistant.core.report.api.model.Language;
import com.buschmais.jqassistant.core.report.api.model.LanguageElement;
import de.materna.structurizr.jqassistant.plugin.model.ComponentDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.ContainerDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.ElementDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.PersonDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.SystemDescriptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the language elements for exports of "Structurizr" graphs.
 *
 * @author Stephan Pirnbaum
 */
@Language
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StructurizrLanguage {

    StructurizrLanguageElement value();

    enum StructurizrLanguageElement implements LanguageElement {

        Component {
            @Override
            public SourceProvider<ComponentDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        },

        Container {
            @Override
            public SourceProvider<ContainerDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        },

        Person {
            @Override
            public SourceProvider<PersonDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        },

        System {
            @Override
            public SourceProvider<SystemDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        };

        @Override
        public String getLanguage() {
            return "Structurizr";
        }
    }
}
