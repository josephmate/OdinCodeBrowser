import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private final String outputDirectory;
    private final String inputDirectory;

    private Main(
            String inputDirectory,
            String outputDirectory
    ) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("<input_directory> <output_directory>");
            System.exit(-1);
        }
        Main main = new Main(args[0], args[1]);

        try (Stream<Path> stream = Files.walk(Paths.get(main.inputDirectory))){
            List<Path> files = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
            for (Path path : files) {
                main.processFile(path);
            }
        }
    }

    private void processFile(Path path) throws IOException {
        System.out.println("Processing " + path);
        final String destinationStr = path.toString()
                .substring(0, path.toString().length()-5)
                .replace(inputDirectory, outputDirectory)
                + ".html";
        String code = Files.readString(path);
        Path destinationPath = Paths.get(destinationStr);
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
     *         <td><a href="#" data-linenum="1"></a></td>
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
                <td><a class="linenum-cell" data-linenum="%s" href="#"></a></td>
                <td><pre>%s</pre></td>
                </tr>
                """,
                lineNumber,
                StringEscapeUtils.escapeHtml4(line)
        );
    }
}
