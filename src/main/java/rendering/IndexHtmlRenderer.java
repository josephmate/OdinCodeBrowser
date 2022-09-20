package rendering;

import options.OdinOptions;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Creates an html file listing out all the class files in the repository.
 */
public class IndexHtmlRenderer {

    private final OdinOptions odinOptions;

    public IndexHtmlRenderer(
            OdinOptions odinOptions
    ) {
        this.odinOptions = odinOptions;
    }

    public void render(
            Collection<Pair<String, List<Path>>> processingOrder
    ) throws IOException {
        SortedMap<String, String> javaFileToHtmlFile = new TreeMap<>();
        for(Pair<String, List<Path>> currentlyProcessing : processingOrder) {
            for (Path path : currentlyProcessing.getRight()) {
                javaFileToHtmlFile.put(
                        path.toString()
                            .replace('\\', '/')
                            .replace(currentlyProcessing.getLeft(), ""),
                        getFileUrl(currentlyProcessing.getLeft(), path)
                );
            }
        }
        final String outputFile = odinOptions.outputDirectory + "/index.html";

        StringBuilder sb = new StringBuilder();
        sb.append(
                String.format(
                        """
                        <html>
                            <head>
                                <link rel="stylesheet" type="text/css" href="%s"/>
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            </head>
                        <body>
                        """,
                        odinOptions.webPathToCssFile
                )
        );

        if (odinOptions.multiRepoRoot != null) {
            sb.append(String.format(
                    """
                    <div>
                        <a class="index-link" href="%s">Back to list of repositories...</a>
                    </div>
                    """,
                    odinOptions.multiRepoRoot
            ));
        }

        for (Map.Entry<String, String> entry : javaFileToHtmlFile.entrySet()) {
            sb.append(String.format(
                    """
                    <div><a class="index-link" href="%s">%s</a></div>
                    """,
                    entry.getValue(),
                    entry.getKey()
            ));
        }

        sb.append(
                """
                </body>
                </html>
                """
        );
        Files.writeString(Paths.get(outputFile), sb.toString());
    }

    private String getFileUrl(
            String inputSourceDirectory,
            Path javaSourceFile
    ) {
        return odinOptions.webPathToSourceHtmlFiles
                + (
                javaSourceFile.toString()
                        .replace('\\', '/')
                        .substring(0, javaSourceFile.toString().length()-5)
                        .replace(inputSourceDirectory, "")
                        + ".html"
        );
    }
}
