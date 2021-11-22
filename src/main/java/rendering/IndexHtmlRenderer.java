package rendering;

import options.OdinOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
            Collection<Path> paths
    ) throws IOException {
        SortedMap<String, String> javaFileToHtmlFile = new TreeMap<>();
        for (Path path : paths) {
            javaFileToHtmlFile.put(
                    path.toString().replace(odinOptions.inputSourceDirectory, ""),
                    getFileUrl(path)
            );
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

    private String getFileUrl(Path javaSourceFile) {
        return odinOptions.webPathToSourceHtmlFiles
                + (
                javaSourceFile.toString()
                        .substring(0, javaSourceFile.toString().length()-5)
                        .replace(odinOptions.inputSourceDirectory, "")
                        + ".html"
        );
    }
}
