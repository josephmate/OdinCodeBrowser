import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record Renderer (
    Index index
){
    public void renderFile(
            Path inputFile,
            String outputFile
    ) throws IOException {
        String code = Files.readString(inputFile);
        Path destinationPath = Paths.get(outputFile);
        destinationPath.toFile().getParentFile().mkdirs();
        Files.writeString(
                destinationPath,
                codeToHtml(code)
        );
    }

    private static final String HEADER =
            """
            <html>
                <head>
                    <link rel="stylesheet" type="text/css" href="/OdinCodeBrowser/css/styles.css"/>
                </head>
            <body>
            <table>
            """;
    private static final String FOOTER =
            """
            </table>
            </body>
            </html>
            """;

    private static String codeToHtml(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER);

        int lineNumber = 1;
        Iterable<String> lines = () -> code.lines().iterator();
        for (String line : lines) {
            sb.append(codeLineToHtml(lineNumber, line));
            lineNumber++;
        }

        sb.append(FOOTER);
        return sb.toString();
    }

    /***
     * <pre>
     * <table>
     *     <tr>
     *         <td><a id="linenum1" href="#linenum1" data-linenum="1"></a></td>
     *         <td>First line</td>
     *     </tr>
     * </table>
     *     <tr>
     *         <td><a href="#" data-linenum="2"></a></td>
     *         <td>Second line</td>
     *     </tr>
     * </table>
     * </pre>
     * @param line
     * @return
     */
    private static String codeLineToHtml(int lineNumber, String line) {
        return String.format(
                """
                <tr>
                <td><a id="linenum%d" class="linenum-cell" data-linenum="%s" href="#linenum%d"></a></td>
                <td><pre>%s</pre></td>
                </tr>
                """,
                lineNumber,
                lineNumber,
                lineNumber,
                StringEscapeUtils.escapeHtml4(line)
        );
    }
}
