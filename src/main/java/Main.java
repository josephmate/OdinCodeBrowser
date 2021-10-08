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
                """
                <html>
                    <head>
                        <link rel="stylesheet" type="text/css" href="/css/styles.css"/>
                    </head>
                <body>
                <pre class="code">    
                """
                + StringEscapeUtils.escapeHtml4(code)
                + """
                </pre>
                </body>
                </html>
                """
        );
    }
}
