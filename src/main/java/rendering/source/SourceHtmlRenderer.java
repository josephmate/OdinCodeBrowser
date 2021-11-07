package rendering.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import indexing.Index;
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
    String webPathToCssFile,
    ParserConfiguration.LanguageLevel languageLevel,
    String header
){

    public SourceHtmlRenderer(Index index,
                              String webPathToCssFile,
                              ParserConfiguration.LanguageLevel languageLevel) {
        this(index, webPathToCssFile, languageLevel,
            String.format(
                    """
                    <html>
                        <head>
                            <link rel="stylesheet" type="text/css" href="%s"/>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        </head>
                    <body>
                    <table>
                    """,
                    webPathToCssFile
            )
        );
    }

    public void renderFile(
            Path inputFile,
            String outputFile
    ) throws IOException {
        JavaParser javaParser = new JavaParser(new ParserConfiguration().setLanguageLevel(
                languageLevel));
        CompilationUnit compilationUnit = javaParser.parse(inputFile).getResult().get();
        ImportVisitor importVisitor = new ImportVisitor(index);
        importVisitor.visit(compilationUnit, null);

        RenderingQueue renderingQueue = new RenderingQueue();
        ApplyIndexVisitor applyIndexVisitor = new ApplyIndexVisitor(
                index,
                importVisitor.imports,
                renderingQueue
        );
        applyIndexVisitor.visit(compilationUnit, null);
        ApplySyntaxHighlightingVisitor syntaxHighlightingVisitor = new ApplySyntaxHighlightingVisitor(
                renderingQueue
        );
        syntaxHighlightingVisitor.visit(compilationUnit, null);

        Path destinationPath = Paths.get(outputFile);
        destinationPath.toFile().getParentFile().mkdirs();
        String code = Files.readString(inputFile);
        Files.writeString(
                destinationPath,
                codeToHtml(code, renderingQueue)
        );
    }

    private static final String FOOTER =
            """
            </table>
            </body>
            </html>
            """;

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

        sb.append(FOOTER);
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

