package rendering.source;

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
import indexing.Index;
import indexing.MethodInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

import java.util.*;

/**
 * Applies the Index to the source code by recording the changes that are need
 * in the rendering queue.
 */
public class ApplyIndexVisitor extends VoidVisitorAdapter<Void> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final RenderingQueue renderingQueue;
    private final Index index;
    private final Map<String, String> imports;
    private final ScopeTracker scopeTracker = new ScopeTracker();
    private int tabbing = 0;

    public ApplyIndexVisitor(
            Index index,
            Map<String, String> imports,
            RenderingQueue renderingQueue
    ) {
        this.index = index;
        this.imports = imports;
        this.renderingQueue = renderingQueue;
    }

    private void addLink(
            SimpleName simpleName,
            Index.FilePosition filePosition,
            String cssClass
    ) {
        if (filePosition != null) {
            int lineNum = simpleName.getRange().get().begin.line;
            int startCol = simpleName.getRange().get().begin.column;
            int endCol = simpleName.getRange().get().end.column;
            renderingQueue.add(lineNum, startCol, new ContentRecord(
                    String.format(
                            """
                                    <a class="%s" href="%s#linenum%d">""",
                            cssClass,
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
        debug(() -> "START ClassOrInterfaceDeclaration" + classOrInterfaceDeclaration);
        tabbing++;
        scopeTracker.startScope();
        // need to set the class name before visiting all the nodes
        String previousClassName = currentClassName;
        if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
            currentClassName = classOrInterfaceDeclaration.getFullyQualifiedName().get();
        }
        super.visit(classOrInterfaceDeclaration, arg);
        currentClassName = previousClassName;
        scopeTracker.endScope();
        tabbing--;
        debug(() -> "END   ClassOrInterfaceDeclaration" + classOrInterfaceDeclaration);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg) {
        debug(() -> "START MethodDeclaration" + methodDeclaration);
        tabbing++;
        scopeTracker.startScope();
        // don't call super since we copied super's code and changed the order to have the parameters first
        methodDeclaration.getParameters().forEach(p -> p.accept(this, arg));
        methodDeclaration.getBody().ifPresent(l -> l.accept(this, arg));
        methodDeclaration.getType().accept(this, arg);
        methodDeclaration.getModifiers().forEach(p -> p.accept(this, arg));
        methodDeclaration.getName().accept(this, arg);
        methodDeclaration.getReceiverParameter().ifPresent(l -> l.accept(this, arg));
        methodDeclaration.getThrownExceptions().forEach(p -> p.accept(this, arg));
        methodDeclaration.getTypeParameters().forEach(p -> p.accept(this, arg));
        methodDeclaration.getAnnotations().forEach(p -> p.accept(this, arg));
        methodDeclaration.getComment().ifPresent(l -> l.accept(this, arg));
        scopeTracker.endScope();
        tabbing--;
        debug(() -> "END   MethodDeclaration" + methodDeclaration);
    }

    @Override
    public void visit(WhileStmt whileStmt, Void arg) {
        debug(() -> "START WhileStmt" + whileStmt);
        tabbing++;
        scopeTracker.startScope();
        super.visit(whileStmt, arg);
        scopeTracker.endScope();
        tabbing--;
        debug(() -> "END   WhileStmt" + whileStmt);
    }

    @Override
    public void visit(ForStmt forStmt, Void arg) {
        debug(() -> "START ForStmt" + forStmt);
        tabbing++;
        scopeTracker.startScope();
        // don't call super since we copied super's code and changed the order to have the parameters first
        forStmt.getInitialization().forEach(p -> p.accept(this, arg));
        forStmt.getBody().accept(this, arg);
        forStmt.getCompare().ifPresent(l -> l.accept(this, arg));
        forStmt.getUpdate().forEach(p -> p.accept(this, arg));
        forStmt.getComment().ifPresent(l -> l.accept(this, arg));
        scopeTracker.endScope();
        tabbing--;
        debug(() -> "END   ForStmt" + forStmt);
    }

    @Override
    public void visit(IfStmt ifStmt, Void arg) {
        debug(() -> "START IfStmt" + ifStmt);
        tabbing++;
        scopeTracker.startScope();
        super.visit(ifStmt, arg);
        scopeTracker.endScope();
        tabbing--;
        debug(() -> "END   IfStmt" + ifStmt);
    }

    @Override
    public void visit(Parameter parameter, Void arg) {
        debug(() -> "START Parameter" + parameter);
        tabbing++;
        scopeTracker.addVariable(
                parameter.getNameAsString(),
                parameter.getType().asString(),
                parameter.getRange().get().begin.line
        );
        super.visit(parameter, arg);
        tabbing--;
        debug(() -> "END   Parameter" + parameter);
    }

    @Override
    public void visit(VariableDeclarator variableDeclarator, Void arg) {
        debug(() -> "START VariableDeclarator" + variableDeclarator);
        tabbing++;
        scopeTracker.addVariable(
                variableDeclarator.getNameAsString(),
                variableDeclarator.getType().asString(),
                variableDeclarator.getRange().get().begin.line
        );
        super.visit(variableDeclarator, arg);
        tabbing--;
        debug(() -> "END   VariableDeclarator" + variableDeclarator);
    }

    @Override
    public void visit(ClassOrInterfaceType classOrInterfaceType, Void arg) {
        debug(() -> "START ClassOrInterfaceType" + classOrInterfaceType);
        tabbing++;
        SimpleName simpleName = classOrInterfaceType.getName();
        String className = simpleName.asString();
        if (imports.containsKey(className)) {
            String fullyQualifiedName = imports.get(className);
            Index.FilePosition filePosition = index.getClass(fullyQualifiedName);
            addLink(simpleName, filePosition, "type");
        }
        super.visit(classOrInterfaceType, arg);
        tabbing--;
        debug(() -> "END   ClassOrInterfaceType" + classOrInterfaceType);
    }

    public void visit(MethodCallExpr methodCallExpr, Void arg) {
        debug(() -> "START MethodCallExpr" + methodCallExpr);
        tabbing++;
        SimpleName methodSimpleName = methodCallExpr.getName();
        if (methodCallExpr.getScope().isPresent()) {
            Expression scope = methodCallExpr.getScope().get();
            if (scope instanceof StringLiteralExpr) {
                handleClassMethod("java.lang.String", methodSimpleName, false);
            } else if (scope instanceof ClassExpr) {
                handleClassMethod("java.lang.Class", methodSimpleName, false);
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
                handleClassMethod(fullyQualifiedClassName, methodSimpleName, false);
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
                handleClassMethod(currentClassName, methodSimpleName, true);
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
                searchForMethodInClassAndSuperClasses(currentClassName, methodSimpleName, true);
            } else {
                LOGGER.warn("Unrecognized expression: {}", methodCallExpr);
            }
        } else {
            // method call within the same class
            handleClassMethod(currentClassName, methodSimpleName, true);
        }
        super.visit(methodCallExpr, arg);
        tabbing--;
        debug(() -> "END   MethodCallExpr" + methodCallExpr);
    }

    private boolean handleClassMethod(
            String fullyQualifiedClassName,
            SimpleName methodSimpleName,
            boolean includePrivates
    ) {
        if (fullyQualifiedClassName != null) {
            Index.FilePosition filePosition = null;
            if (includePrivates) {
                List<MethodInfo> overloads = index.getPrivateMethodOverloads(
                        fullyQualifiedClassName,
                        methodSimpleName.asString()
                );
                if (overloads != null) {
                    MethodInfo lastMethodInfo = overloads.get(overloads.size() - 1);
                    filePosition = lastMethodInfo.filePosition();
                }
            }
            addLink(methodSimpleName, filePosition, "type");
            if (filePosition != null) {
                return true;
            }

            // try class and super classes
            return searchForMethodInClassAndSuperClasses(
                    fullyQualifiedClassName,
                    methodSimpleName,
                    false
            );
        }
        return false;
    }

    private boolean searchForMethodInClassAndSuperClasses(
            String fullyQualifiedClassName,
            SimpleName methodSimpleName,
            boolean skipCurrentClass
    ) {
        Set<String> visited = new HashSet<>();
        Queue<String> bfsQueue = new ArrayDeque<>();
        if (skipCurrentClass) {
            visited.add(fullyQualifiedClassName);
            bfsQueue.addAll(index.getSuperClasses(fullyQualifiedClassName));
        } else {
            bfsQueue.add(fullyQualifiedClassName);
        }

        while (!bfsQueue.isEmpty()) {
            String currentFullyQualifiedClassName = bfsQueue.poll();
            visited.add(currentFullyQualifiedClassName);

            List<MethodInfo> overloads = index.getMethodOverloads(
                    currentFullyQualifiedClassName,
                    methodSimpleName.asString()
            );
            Index.FilePosition filePosition = null;
            if (overloads != null) {
                MethodInfo lastMethodInfo = overloads.get(overloads.size() - 1);
                filePosition = lastMethodInfo.filePosition();
            }
            addLink(methodSimpleName, filePosition, "type");
            if (filePosition != null) {
                return true;
            }

            // add super classes to search
            for (String fullyQualifiedSuperClassName : index.getSuperClasses(currentFullyQualifiedClassName)) {
                if (!visited.contains(fullyQualifiedSuperClassName)) {
                    bfsQueue.add(fullyQualifiedSuperClassName);
                }
            }
        }

        // exhausted all super classes
        return false;
    }

    public void visit(NameExpr nameExpr, Void arg) {
        debug(() -> "START NameExpr" + nameExpr);
        tabbing++;
        // trying variable index
        final Integer localVariableLineNum = scopeTracker.getVariableLine(nameExpr.getNameAsString());
        if (localVariableLineNum != null) {
            addLink(nameExpr.getName(), new Index.FilePosition(
                    "",
                    localVariableLineNum
                ),
        "variable"
            );
        }
        tabbing--;
        debug(() -> "END   NameExpr" + nameExpr);
    }

    private String generateTabs() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tabbing; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private void debug(Supplier<?> msg) {
        LOGGER.debug("{}{}",
                () -> generateTabs(),
                msg);
    }
}
