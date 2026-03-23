package de.materna.structurizr.jqassistant.plugin;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import de.materna.structurizr.jqassistant.plugin.model.StructurizrDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.StructurizrFileDescriptor;
import de.materna.structurizr.jqassistant.plugin.model.WorkspaceDescriptor;
import de.materna.structurizr.jqassistant.plugin.persister.WorkspacePersister;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Scanner plug-in to enrich the graph based on a Structurizr DSL file.
 *
 * @author Stephan Pirnbaum
 */
@Requires(FileDescriptor.class)
@Slf4j
public class StructurizrWorkspaceScannerPlugin extends AbstractScannerPlugin<FileResource, StructurizrDescriptor> {

    @Override
    public boolean accepts(FileResource fileResource, String path, Scope scope) {
        if (!path.toLowerCase().endsWith(".dsl")) {
            return false;
        } else {
            try (InputStream is = fileResource.createStream()) {
                String fileContent = IOUtils.toString(is);
                StructurizrDslParser parser = new StructurizrDslParser();
                parser.parse(fileContent, fileResource.getFile());
                parser.getWorkspace();
                return true;
            } catch (Exception e) {
                log.error("Unable to read Structurizr workspace", e);
                return false;
            }
        }
    }

    @Override
    public StructurizrDescriptor scan(FileResource fileResource, String path, Scope scope, Scanner scanner) throws IOException {
        StructurizrFileDescriptor structurizrFileDescriptor = getScannerContext().getStore().addDescriptorType(getScannerContext().getCurrentDescriptor(), StructurizrFileDescriptor.class);

        try (InputStream is = fileResource.createStream()) {
            String fileContent = IOUtils.toString(is);
            StructurizrDslParser parser = new StructurizrDslParser();
            parser.parse(fileContent, fileResource.getFile());
            Workspace workspace = parser.getWorkspace();
            WorkspacePersister persister = new WorkspacePersister(getScannerContext().getStore());
            WorkspaceDescriptor workspaceDescriptor = persister.persist(workspace);
            structurizrFileDescriptor.setWorkspace(workspaceDescriptor);
            return structurizrFileDescriptor;
        } catch (StructurizrDslParserException e) {
            throw new StructurizrWorkspaceEvaluationException("Unable to parse Structurizr workspace", e);
        }
    }
}
