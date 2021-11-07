package rendering;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;

/**
 * Creates an html file listing out all the class files in the repository.
 */
public class IndexHtmlRenderer {

    public void render(
            String outputFile,
            String webPathToCssFile,
            SortedMap<String,String> javaFileToHtmlFile
    ) throws IOException {
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
                        webPathToCssFile
                )
        );

        for (Map.Entry<String, String> entry : javaFileToHtmlFile.entrySet()) {
            sb.append(String.format(
                    """
                    <div><a href="%s">%s</a></div>
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

}
