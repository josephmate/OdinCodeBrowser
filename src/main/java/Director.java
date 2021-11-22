import indexing.Index;
import indexing.Indexer;
import indexing.SuperClassIndexer;
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
        Index completeIndex = new UrlIndexLoader().load(odinOptions.urlsToDependantIndexJsons);
        // don't use private methods from dependencies
        completeIndex.privateMethodIndex.clear();

        try (Stream<Path> stream = Files.walk(Paths.get(odinOptions.inputSourceDirectory))){
            List<Path> files = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            Index localIndex = new Index();
            new Indexer(odinOptions).indexFiles(files, localIndex);
            // need complete index for super class since we need all the imports
            // this index will be missing the super classes which is fine. we don't
            // need the dependencies super class mapping to calculate the super class
            // mapping.
            completeIndex.addAll(localIndex);
            new SuperClassIndexer(completeIndex, odinOptions).indexFiles(files, localIndex);
            completeIndex.addAll(localIndex);

            new SourceHtmlRenderer(completeIndex, odinOptions).proccessFiles(files);
            new IndexHtmlRenderer(odinOptions).render(files);
            new IndexJsonRenderer().render(
                    odinOptions.outputDirectory + "/index.json",
                    // only export an index for the sources belonging to this project
                    localIndex
            );
        }
    }

}
