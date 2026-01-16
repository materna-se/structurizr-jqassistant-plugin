package io.guthub.stephanpirnbaum.structurizr.jqassistant.plugin;

/**
 * Exception to represent issues during processing a Structurizr workspace.
 *
 * @author Stephan Pirnbaum
 */
public class StructurizrWorkspaceEvaluationException extends RuntimeException {
    public StructurizrWorkspaceEvaluationException(String message) {
        super(message);
    }

    public StructurizrWorkspaceEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }


}
