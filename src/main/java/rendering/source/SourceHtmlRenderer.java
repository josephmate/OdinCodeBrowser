package rendering.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import indexing.ImportVisitor;
import indexing.Index;
import options.OdinOptions;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public record SourceHtmlRenderer(
    Index index,
    OdinOptions odinOptions,
    String header,
    String footer
){

    public SourceHtmlRenderer(Index index,
                              OdinOptions odinOptions) {
        this(
            index,
            odinOptions,
            String.format(
                    """
                    <html>
                        <head>
                            <link rel="stylesheet" type="text/css" href="%s"/>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        </head>
                    <body>
                    <a class="index-link" href="%s">Back to index...</a>
                    <table>
                    """,
                odinOptions.webPathToCssFile,
                odinOptions.webPathToSourceHtmlFiles
                ),
            String.format(
                """
                </table>
                </body>
                <a class="index-link" href="%s">Back to index...</a>
                </html>
                """,
                odinOptions.webPathToSourceHtmlFiles
                )
        );
    }

    public void proccessFiles(
            String inputDirectory,
            Collection<Path> files
    ) throws IOException {
        for (Path path : files) {
            processFile(inputDirectory, path);
        }
    }

    private void processFile(
            String inputDirectory,
            Path path
    ) throws IOException {
        System.out.println("Rendering " + path);
        final String destinationStr = path.toString()
                .replace('\\', '/')
                .substring(0, path.toString().length()-5)
                .replace(inputDirectory, odinOptions.outputDirectory)
                + ".html";
        renderFile(path, destinationStr);
    }

    private void renderFile(
            Path inputFile,
            String outputFile
    ) throws IOException {
        JavaParser javaParser = new JavaParser(new ParserConfiguration().setLanguageLevel(
                odinOptions.languageLevel));
        // Get the parse tree from GitHub Java Parser, so we can navigate the parse tree
        // with visitors.
        CompilationUnit compilationUnit = javaParser.parse(inputFile).getResult().get();

        // Use a visitor to collect all the imports. The imports are used to detemine what
        // methods, classes, and static fields are in scope.
        ImportVisitor importVisitor = new ImportVisitor(index);
        importVisitor.visit(compilationUnit, null);

        // Accumulate the changes from source code to html in a rendering queue that we apply at the end.
        // This allows us to apply successive operations like a link, then a css style.
        RenderingQueue renderingQueue = new RenderingQueue();

        // Use the index with the parse tree to figure out what links need to be added to the source code.
        ApplyIndexVisitor applyIndexVisitor = new ApplyIndexVisitor(
                outputFile,
                index,
                importVisitor.imports,
                renderingQueue
        );
        applyIndexVisitor.visit(compilationUnit, null);

        // Use the parse tree to figure out what css styles need to be applied. This is separates
        // linking from styling since methods, classes, variables, other linkables will have
        // different colours.
        ApplySyntaxHighlightingVisitor syntaxHighlightingVisitor = new ApplySyntaxHighlightingVisitor(
                renderingQueue
        );
        syntaxHighlightingVisitor.visit(compilationUnit, null);

        // Apply the rendering queue to the code.
        Path destinationPath = Paths.get(outputFile);
        destinationPath.toFile().getParentFile().mkdirs();
        String code = Files.readString(inputFile);
        Files.writeString(
                destinationPath,
                codeToHtml(code, renderingQueue)
        );
    }

    private String codeToHtml(
            String code,
            RenderingQueue renderingQueue
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(header);

        int lineNumber = 1;
        Iterable<String> lines = () -> code.lines().iterator();
        for (String line : lines) {
            linePrefix(sb, lineNumber);

            Collection<String> beforeTasks = renderingQueue.beginningOfLineRenderingQueue.get(lineNumber);
            if (beforeTasks != null) {
                for (String content : beforeTasks) {
                    sb.append(content);
                }
            }

            renderLine(sb, line, renderingQueue.withinLineRenderingQueue.getOrDefault(
                    lineNumber,
                    new RenderQueueLine()
            ));

            Collection<String> afterTasksRaw = renderingQueue.beginningOfLineRenderingQueue.get(lineNumber);
            if (afterTasksRaw != null) {
                List<String> afterTasks = new ArrayList<>(afterTasksRaw);
                Collections.reverse(afterTasks);
                for (String content : afterTasks) {
                    sb.append(content);
                }
            }

            lineSuffix(sb);
            lineNumber++;
        }

        sb.append(footer);
        return sb.toString();
    }

    private void linePrefix(StringBuilder sb, int lineNumber) {
        sb.append(String.format(
                """
                <tr>
                <td><a id="linenum%d" class="linenum-cell" data-linenum="%d" href="#linenum%d"></a></td>
                <td><pre>""",
                lineNumber,
                lineNumber,
                lineNumber
        ));
    }

    private void lineSuffix(StringBuilder sb) {
        sb.append(String.format(
                """
                </pre></td>
                </tr>
                """
        ));
    }

    private void renderLine(
            StringBuilder sb,
            String line,
            @Nonnull RenderQueueLine renderQueueLine
    ) {
        int currentCol = 1;
        for (char c : line.toCharArray()) {
            List<String> beforeContentRecords = renderQueueLine.renderQueueLine.getOrDefault(currentCol, Collections.emptyList()).stream()
                    .filter(contentRecord -> contentRecord.positionType() == PositionType.BEFORE)
                    .map(ContentRecord::content)
                    .collect(Collectors.toList());
            for (String before : beforeContentRecords) {
                sb.append(before);
            }

            sb.append(StringEscapeUtils.escapeHtml4(String.valueOf(c)));

            List<String> afterContentRecords = renderQueueLine.renderQueueLine.getOrDefault(currentCol, Collections.emptyList()).stream()
                    .filter(contentRecord -> contentRecord.positionType() == PositionType.AFTER)
                    .map(ContentRecord::content)
                    // need to reverse so that the <a><div> </a></div>
                    // gets converted to <a><div> </div></a>
                    .collect(Collectors.toList());
            Collections.reverse(afterContentRecords);
            for (String after : afterContentRecords) {
                sb.append(after);
            }
            currentCol++;
        }
    }
}

