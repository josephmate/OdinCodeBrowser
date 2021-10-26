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
            SortedMap<String,String> javaFileToHtmlFile
    ) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(
                """
                <html>
                <body>
                """
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
