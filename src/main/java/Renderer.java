import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public record Renderer (
    Index index,
    String webRootUrl,
    String header
){

    public Renderer(Index index,
             String webRootUrl) {
        this(index, webRootUrl,
            String.format(
                    """
                    <html>
                        <head>
                            <link rel="stylesheet" type="text/css" href="%s"/>
                        </head>
                    <body>
                    <table>
                    """,
                    webRootUrl + "/css/styles.css"
            )
        );
    }

    public void renderFile(
            Path inputFile,
            String outputFile
    ) throws IOException {
        CompilationUnit compilationUnit = StaticJavaParser.parse(inputFile);
        ImportVisitor importVisitor = new ImportVisitor(index);
        importVisitor.visit(compilationUnit, null);

        RenderingQueueVisitor renderingQueueVisitor = new RenderingQueueVisitor(
                index,
                importVisitor.imports
        );
        renderingQueueVisitor.visit(compilationUnit, null);

        Path destinationPath = Paths.get(outputFile);
        destinationPath.toFile().getParentFile().mkdirs();
        String code = Files.readString(inputFile);
        Files.writeString(
                destinationPath,
                codeToHtml(code, renderingQueueVisitor.renderingQueue)
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

            renderLine(sb, line, renderingQueue.renderingQueue.getOrDefault(
                    lineNumber,
                    new RenderQueueLine()
            ));

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

enum PositionType {
    BEFORE,
    AFTER
}

record ContentRecord (
    String content,
    PositionType positionType
) {

}

class RenderQueueLine {

    final Map<Integer, List<ContentRecord>> renderQueueLine = new HashMap<>();

    public void add(int col, ContentRecord contentRecord) {
        List<ContentRecord> content = renderQueueLine.get(col);
        if (content == null) {
            content = new ArrayList<>();
            renderQueueLine.put(col, content);
        }
        content.add(contentRecord);
    }

}

class RenderingQueue {

    final Map<Integer, RenderQueueLine> renderingQueue = new HashMap<>();

    public void add(int line, int col, ContentRecord contentRecord) {
        RenderQueueLine renderQueueLine = renderingQueue.get(line);
        if (renderQueueLine == null) {
            renderQueueLine = new RenderQueueLine();
            renderingQueue.put(line, renderQueueLine);
        }
        renderQueueLine.add(col, contentRecord);
    }
}

class ImportVisitor extends VoidVisitorAdapter<Void> {

    final Map<String, String> imports = new HashMap<>();
    private final Index index;

    public ImportVisitor(Index index) {
        this.index = index;
        for (String fullyQualifiedName : index.getClassIndex().keySet()) {
            if (fullyQualifiedName.startsWith("java.lang.")) {
                imports.put(getLastToken(fullyQualifiedName), fullyQualifiedName);
            }
        }
    }

    private boolean isInPackage(
            String fullyQualifiedName,
            String packageName
    ) {
        return fullyQualifiedName.startsWith(packageName)
                && fullyQualifiedName.lastIndexOf('.') <= packageName.length();
    }

    @Override
    public void visit(PackageDeclaration packageDeclaration, Void arg) {
        String packageName = packageDeclaration.getName().asString();
        for (String fullyQualifiedName : index.getClassIndex().keySet()) {
            if (isInPackage(fullyQualifiedName, packageName)) {
                imports.put(getLastToken(fullyQualifiedName), fullyQualifiedName);
            }
        }
        super.visit(packageDeclaration, arg);
    }

    @Override
    public void visit(ImportDeclaration importDeclaration, Void arg) {
        if (!importDeclaration.isAsterisk() && !importDeclaration.isStatic()) {
            String importName = importDeclaration.getNameAsString();
            imports.put(getLastToken(importName), importName);
        } else if (importDeclaration.isAsterisk() && !importDeclaration.isStatic()) {
            String importName = importDeclaration.getNameAsString();
            for (String fullyQualifiedName : index.getClassIndex().keySet()) {
                if (isInPackage(fullyQualifiedName, importName)) {
                    imports.put(getLastToken(fullyQualifiedName), fullyQualifiedName);
                }
            }
        }
        super.visit(importDeclaration, arg);
    }

    private static String getLastToken(String importName) {
        String [] tokens = importName.split("\\.");
        if (tokens.length <= 0) {
            return "";
        }
        return tokens[tokens.length-1];
    }
}

class RenderingQueueVisitor extends VoidVisitorAdapter<Void> {

    RenderingQueue renderingQueue = new RenderingQueue();
    private final Index index;
    private final Map<String, String> imports;

    public RenderingQueueVisitor(
            Index index,
            Map<String, String> imports
    ) {
        this.index = index;
        this.imports = imports;
    }

    private void addLink(SimpleName simpleName, Index.FilePosition filePosition) {
        if (filePosition != null) {
            int lineNum = simpleName.getRange().get().begin.line;
            int startCol = simpleName.getRange().get().begin.column;
            int endCol = simpleName.getRange().get().end.column;
            renderingQueue.add(lineNum, startCol, new ContentRecord(
                    String.format(
                            """
                            <a href="%s#linenum%d">""",
                            filePosition.fileName(),
                            filePosition.lineNumber()
                    ),
                    PositionType.BEFORE
            ));
            renderingQueue.add(lineNum, endCol, new ContentRecord(
                    "</a>",
                    PositionType.AFTER
            ));
        }
    }

    @Override
    public void visit(ClassOrInterfaceType classOrInterfaceType, Void arg) {
        SimpleName simpleName = classOrInterfaceType.getName();
        String className = simpleName.asString();
        if (imports.containsKey(className)) {
            String fullyQualifiedName = imports.get(className);
            Index.FilePosition filePosition = index.get(fullyQualifiedName);
            addLink(simpleName, filePosition);
        }

        super.visit(classOrInterfaceType, arg);
    }

    public void visit(MethodCallExpr methodCallExpr , Void arg) {
        SimpleName simpleName = methodCallExpr.getName();
        methodCallExpr.getNameAsString();
        if (methodCallExpr.getScope().isPresent()) {
            if (methodCallExpr.getScope().get() instanceof StringLiteralExpr) {
                Index.FilePosition filePosition = index.getMethod(
                        "java.lang.String",
                        simpleName.asString()
                );
                addLink(simpleName, filePosition);
            } else {
                methodCallExpr.getNameAsString();
            }
        } else {
            // TODO: probably method call within the same class
        }
    }
}