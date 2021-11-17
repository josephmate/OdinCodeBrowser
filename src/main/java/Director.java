import indexing.Index;
import indexing.Indexer;
import indexing.UrlIndexLoader;
import options.OdinOptions;
import rendering.IndexHtmlRenderer;
import rendering.IndexJsonRenderer;
import rendering.source.SourceHtmlRenderer;

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
    OdinOptions odinOptions
) {
    public void processFiles() throws IOException {
        Index index = new UrlIndexLoader().load(odinOptions.urlsToDependantIndexJsons);
        index.privateMethodIndex.clear();

        try (Stream<Path> stream = Files.walk(Paths.get(odinOptions.inputSourceDirectory))){
            List<Path> files = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            new Indexer(odinOptions).indexFiles(files, index);
            new SourceHtmlRenderer(index, odinOptions).proccessFiles(files);
            new IndexHtmlRenderer(odinOptions).render(files);
            new IndexJsonRenderer().render(
                    odinOptions.outputDirectory + "/index.json",
                    // only export an index for the sources belonging to this project
                    localIndex // TODO: figure this out
            );
        }
    }

}
