import indexing.Index;
import indexing.UrlIndexLoader;
import rendering.IndexHtmlRenderer;
import rendering.IndexJsonRenderer;
import rendering.source.SourceHtmlRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Figures out which files needs to be processed.
 * Two pass process:
 * <ol>
 *     <li>Provides those files to the Index to create the index.</li>
 *     <li>Gives the index to the render so it can create the html files.</li>
 * </ol>
 */
public record Director(
    OdinOptions odinOptions
) {
    public void processFiles() throws IOException {
        Index localIndex = new Index();

        try (Stream<Path> stream = Files.walk(Paths.get(odinOptions.inputSourceDirectory))){
            List<Path> files = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
            for (Path path: files) {
                try {
                    indexFile(localIndex, path);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing " + path, e);
                }
            }

            Index externalIndex = new UrlIndexLoader().load(odinOptions.urlsToDependantIndexJsons);
            for (Map.Entry<String, Index.FilePosition> entry : localIndex.classIndex.entrySet()) {
                String fullyQualifiedClassName = entry.getKey();
                Index.FilePosition filePosition = entry.getValue();
                externalIndex.addClass(
                        fullyQualifiedClassName,
                        filePosition.fileName(),
                        filePosition.lineNumber()
                );
            }
            for (Map.Entry<String, Map<String, Index.FilePosition>> entry : localIndex.variableIndex.entrySet()) {
                String fullyQualifiedClassName = entry.getKey();
                for (Map.Entry<String, Index.FilePosition> entry2: entry.getValue().entrySet()) {
                    String variableName = entry2.getKey();
                    Index.FilePosition filePosition = entry2.getValue();
                    externalIndex.addVariable(
                            fullyQualifiedClassName,
                            variableName,
                            filePosition.fileName(),
                            filePosition.lineNumber()
                    );
                }
            }
            for (Map.Entry<String, Map<String, Index.FilePosition>> entry : localIndex.methodIndex.entrySet()) {
                String fullyQualifiedClassName = entry.getKey();
                for (Map.Entry<String, Index.FilePosition> entry2: entry.getValue().entrySet()) {
                    String methodName = entry2.getKey();
                    Index.FilePosition filePosition = entry2.getValue();
                    externalIndex.addMethod(
                            fullyQualifiedClassName,
                            methodName,
                            filePosition.fileName(),
                            filePosition.lineNumber()
                    );
                }
            }
            // there should be no existing private index, so this should merge it properly
            externalIndex.privateMethodIndex.putAll(localIndex.privateMethodIndex);

            SourceHtmlRenderer render = new SourceHtmlRenderer(
                    externalIndex,
                    odinOptions.webPathToCssFile,
                    odinOptions.languageLevel
            );
            for (Path path : files) {
                processFile(render, path);
            }

            SortedMap<String, String> fileTreeData = new TreeMap<>();
            for (Path path : files) {
                fileTreeData.put(
                        path.toString().replace(odinOptions.inputSourceDirectory, ""),
                        getFileUrl(path)
                );
            }
            new IndexHtmlRenderer().render(
                    odinOptions.outputDirectory + "/index.html",
                    fileTreeData
            );

            new IndexJsonRenderer().render(
                    odinOptions.outputDirectory + "/index.json",
                    // only export an index for the sources belonging to this project
                    localIndex
            );
        }
    }

    private String getFileUrl(Path javaSourceFile) {
        return odinOptions.webPathToSourceHtmlFiles
                + (
                javaSourceFile.toString()
                        .substring(0, javaSourceFile.toString().length()-5)
                        .replace(odinOptions.inputSourceDirectory, "")
                        + ".html"
        );
    }

    private void indexFile(Index index, Path path) throws IOException {
        System.out.println("Indexing " + path);
        final String fileUrl = getFileUrl(path);
        index.indexFile(
                path,
                fileUrl,
                odinOptions.languageLevel
        );
    }

    private void processFile(SourceHtmlRenderer render, Path path) throws IOException {
        System.out.println("Rendering " + path);
        final String destinationStr = path.toString()
                .substring(0, path.toString().length()-5)
                .replace(odinOptions.inputSourceDirectory, odinOptions.outputDirectory)
                + ".html";
        render.renderFile(path, destinationStr);
    }

}
