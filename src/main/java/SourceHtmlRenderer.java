import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
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

public record SourceHtmlRenderer(
    Index index,
    String webPathToCssFile,
    String header
){

    public SourceHtmlRenderer(Index index,
                              String webPathToCssFile) {
        this(index, webPathToCssFile,
            String.format(
                    """
                    <html>
                        <head>
                            <link rel="stylesheet" type="text/css" href="%s"/>
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
                ParserConfiguration.LanguageLevel.JAVA_16));
        CompilationUnit compilationUnit = javaParser.parse(inputFile).getResult().get();
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

class RenderingQueueVisitor extends VoidVisitorAdapter<Void> {

    RenderingQueue renderingQueue = new RenderingQueue();
    private final Index index;
    private final Map<String, String> imports;
    private final ScopeTracker scopeTracker = new ScopeTracker();

    public RenderingQueueVisitor(
            Index index,
            Map<String, String> imports
    ) {
        this.index = index;
        this.imports = imports;
    }

    private void addLink(
            SimpleName simpleName,
            Index.FilePosition filePosition
    ) {
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

    private String currentClassName = null;

    public void visit(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Void arg) {
        scopeTracker.startScope();
        // need to set the class name before visiting all the nodes
        String previousClassName = currentClassName;
        if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
            currentClassName = classOrInterfaceDeclaration.getFullyQualifiedName().get();
        }
        super.visit(classOrInterfaceDeclaration, arg);
        currentClassName = previousClassName;
        scopeTracker.endScope();
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        scopeTracker.startScope();
        // don't call super since we copied super's code and changed the order to have the parameters first
        n.getParameters().forEach(p -> p.accept(this, arg));
        n.getBody().ifPresent(l -> l.accept(this, arg));
        n.getType().accept(this, arg);
        n.getModifiers().forEach(p -> p.accept(this, arg));
        n.getName().accept(this, arg);
        n.getReceiverParameter().ifPresent(l -> l.accept(this, arg));
        n.getThrownExceptions().forEach(p -> p.accept(this, arg));
        n.getTypeParameters().forEach(p -> p.accept(this, arg));
        n.getAnnotations().forEach(p -> p.accept(this, arg));
        n.getComment().ifPresent(l -> l.accept(this, arg));
        scopeTracker.endScope();

    }

    @Override
    public void visit(WhileStmt d, Void arg) {
        scopeTracker.startScope();
        super.visit(d, arg);
        scopeTracker.endScope();
    }

    @Override
    public void visit(ForStmt n, Void arg) {
        scopeTracker.startScope();
        // don't call super since we copied super's code and changed the order to have the parameters first
        n.getInitialization().forEach(p -> p.accept(this, arg));
        n.getBody().accept(this, arg);
        n.getCompare().ifPresent(l -> l.accept(this, arg));
        n.getUpdate().forEach(p -> p.accept(this, arg));
        n.getComment().ifPresent(l -> l.accept(this, arg));
        scopeTracker.endScope();
    }

    @Override
    public void visit(IfStmt d, Void arg) {
        scopeTracker.startScope();
        super.visit(d, arg);
        scopeTracker.endScope();
    }

    @Override
    public void visit(Parameter d, Void arg) {
        scopeTracker.addVariable(
                d.getNameAsString(),
                d.getType().asString(),
                d.getRange().get().begin.line
        );
        super.visit(d, arg);
    }

    @Override
    public void visit(VariableDeclarator d, Void arg) {
        scopeTracker.addVariable(
                d.getNameAsString(),
                d.getType().asString(),
                d.getRange().get().begin.line
        );
        super.visit(d, arg);
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

    public void visit(MethodCallExpr methodCallExpr, Void arg) {
        SimpleName methodSimpleName = methodCallExpr.getName();
        if (methodCallExpr.getScope().isPresent()) {
            Expression scope = methodCallExpr.getScope().get();
            if (scope instanceof StringLiteralExpr) {
                handleClassName("java.lang.String", methodSimpleName, false);
            } else if (scope instanceof ClassExpr) {
                handleClassName("java.lang.Class", methodSimpleName, false);
            } else if (scope instanceof NameExpr) {
                NameExpr nameExpr = (NameExpr) scope;
                // example System.getProperty()
                // example variable.getProperty()
                // Need some way to distinguish between them
                // How about try variable index first, then Class index.

                // trying variable index
                final String typeFromVariable = scopeTracker.getVariableType(nameExpr.getNameAsString());
                final String fullyQualifiedClassName;
                if (typeFromVariable != null) {
                    fullyQualifiedClassName = imports.get(typeFromVariable);
                } else {
                    // trying Class index
                    fullyQualifiedClassName = imports.get(nameExpr.getName().asString());
                }
                handleClassName(fullyQualifiedClassName, methodSimpleName, false);
            } else if (scope instanceof MethodCallExpr) {
                // method call chaining
                // example indexMap.entrySet().iterator()
                //                     \          \___ should be Iterable
                //                      \___ should be Map
                methodCallExpr.getNameAsString();
            } else if (scope instanceof EnclosedExpr) {
                // (new FDBigInteger(r, pow5.offset)).leftShift(p2)
                methodCallExpr.getNameAsString();
            } else if (scope instanceof ThisExpr) {
                // this.size()
                handleClassName(currentClassName, methodSimpleName, true);
            } else if (scope instanceof FieldAccessExpr) {
                // this.data.clone()
                methodCallExpr.getNameAsString();
            } else if (scope instanceof ObjectCreationExpr) {
                // new BigInteger(magnitude).shiftLeft(offset * 32)
                methodCallExpr.getNameAsString();
            } else if (scope instanceof ArrayAccessExpr) {
                // queryArgs[i].startsWith("mode=")
                methodCallExpr.getNameAsString();
            } else if (scope instanceof SuperExpr) {
                // super.detach()
                methodCallExpr.getNameAsString();
            } else {
                System.out.println("Unrecognized expression: " + methodCallExpr);
            }
        } else {
            // method call within the same class
            handleClassName(currentClassName, methodSimpleName, true);
        }
        super.visit(methodCallExpr, arg);
    }

    private boolean handleClassName(
            String fullyQualifiedClassName,
            SimpleName methodSimpleName,
            boolean includePrivates
    ) {
        if (fullyQualifiedClassName != null) {
            Index.FilePosition filePosition = null;
            if (includePrivates) {
                filePosition = index.getPrivateMethod(
                        fullyQualifiedClassName,
                        methodSimpleName.asString()
                );
            }
            addLink(methodSimpleName, filePosition);

            if (filePosition != null) {
                return true;
            }
            filePosition = index.getMethod(
                    fullyQualifiedClassName,
                    methodSimpleName.asString()
            );
            addLink(methodSimpleName, filePosition);

            return filePosition != null;
        }
        return false;
    }

    public void visit(NameExpr nameExpr, Void arg) {
        // trying variable index
        final Integer localVariableLineNum = scopeTracker.getVariableLine(nameExpr.getNameAsString());
        if (localVariableLineNum != null) {
            addLink(nameExpr.getName(), new Index.FilePosition(
                    "",
                    localVariableLineNum
            ));
        }
    }

}