import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
    String inputDirectory,
    String outputDirectory,
    String webRootDir,
    String sourceSubUrl
) {
    public void processFiles() throws IOException {
        Index index = new Index();
        Renderer render = new Renderer(index, webRootDir);

        try (Stream<Path> stream = Files.walk(Paths.get(inputDirectory))){
            List<Path> files = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
            for (Path path: files) {
                try {
                    indexFile(index, path);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing " + path, e);
                }
            }

            for (Path path : files) {
                processFile(render, path);
            }

            SortedMap<String, String> fileTreeData = new TreeMap<>();
            for (Path path : files) {
                fileTreeData.put(
                        path.toString().replace(inputDirectory, ""),
                        getFileUrl(path)
                );
            }
            new IndexFileRenderer().render(
                    outputDirectory + "/index.html",
                    fileTreeData
            );
        }
    }

    private String getFileUrl(Path javaSourceFile) {
        return webRootDir + "/" + sourceSubUrl + "/"
                + (
                javaSourceFile.toString()
                        .substring(0, javaSourceFile.toString().length()-5)
                        .replace(inputDirectory, "")
                        + ".html"
        );
    }

    private void indexFile(Index index, Path path) throws IOException {
        System.out.println("Indexing " + path);
        final String fileUrl = getFileUrl(path);
        index.indexFile(path, fileUrl);
    }

    private void processFile(Renderer render, Path path) throws IOException {
        System.out.println("Rendering " + path);
        final String destinationStr = path.toString()
                .substring(0, path.toString().length()-5)
                .replace(inputDirectory, outputDirectory)
                + ".html";
        render.renderFile(path, destinationStr);
    }

}
