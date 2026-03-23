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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        return path.toLowerCase().endsWith(".dsl");
    }

    @Override
    public StructurizrDescriptor scan(FileResource fileResource, String path, Scope scope, Scanner scanner) throws IOException {
        StructurizrFileDescriptor structurizrFileDescriptor = getScannerContext().getStore().addDescriptorType(getScannerContext().getCurrentDescriptor(), StructurizrFileDescriptor.class);
        try (InputStream is = fileResource.createStream()) {
            String fileContent = IOUtils.toString(is);
            StructurizrDslParser parser = new StructurizrDslParser();
            parser.parse(fileContent, resolveWorkspace(fileResource, path));
            Workspace workspace = parser.getWorkspace();
            WorkspacePersister persister = new WorkspacePersister(getScannerContext().getStore());
            WorkspaceDescriptor workspaceDescriptor = persister.persist(workspace);
            structurizrFileDescriptor.setWorkspace(workspaceDescriptor);
            return structurizrFileDescriptor;
        } catch (StructurizrDslParserException e) {
            throw new StructurizrWorkspaceEvaluationException("Unable to parse Structurizr workspace", e);
        } catch (URISyntaxException e) {
            throw new StructurizrWorkspaceEvaluationException("Unable to resolve workspace files in jQAssistant Plugin Jar", e);
        }
    }

    private File resolveWorkspace(FileResource fileResource, String path) throws URISyntaxException, IOException {
        if ("jar".equals(new URI(path).getScheme())) {
            /*
             * Workspace is part of a jQAssistant Plug-In
             * Includes can't be resolved by Structurizr as they are in the jar file
             * so extract all the DSLs into a temp folder
             */
            Path tempDir = Files.createTempDirectory("structurizr-dsl-");
            URI jarURI = resolveJarURI(new URI(path));

            try (JarFile jar = new JarFile(Path.of(jarURI).toFile())) {
                jar
                        .stream()
                        .filter(f -> f.getName().endsWith(".dsl") || f.getName().endsWith(".json")) // included DSLs and Themes
                        .forEach(entry -> extract(jar, entry, tempDir));
            }
            return null;
        } else {
            return fileResource.getFile();
        }
    }

    private URI resolveJarURI(URI jarURI) {
        // split jar:file:/...jar!/<entry>
        String ssp = jarURI.getSchemeSpecificPart(); // file:/...jar!/workspace.dsl
        int bang = ssp.indexOf("!/");
        if (bang < 0) throw new IllegalArgumentException("Invalid jar URI: " + jarURI);

        return URI.create(ssp.substring(0, bang));      // /.../artifact.jar
    }

    private void extract(JarFile jar, JarEntry entry, Path targetDir) {
        try {
            Path target = targetDir.resolve(entry.getName());

            Files.createDirectories(target.getParent());
            try (InputStream in = jar.getInputStream(entry)) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Fehler beim Entpacken von " + entry.getName(), ex);
        }
    }
}
