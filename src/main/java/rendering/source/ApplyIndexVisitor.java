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

import java.util.*;

/**
 * Applies the Index to the source code by recording the changes that are need
 * in the rendering queue.
 */
public class ApplyIndexVisitor extends VoidVisitorAdapter<Void> {

    private final RenderingQueue renderingQueue;
    private final Index index;
    private final Map<String, String> imports;
    private final ScopeTracker scopeTracker = new ScopeTracker();

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
            Index.FilePosition filePosition = index.getClass(fullyQualifiedName);
            addLink(simpleName, filePosition, "type");
        }
        super.visit(classOrInterfaceType, arg);
    }

    public void visit(MethodCallExpr methodCallExpr, Void arg) {
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
                System.out.println("Unrecognized expression: " + methodCallExpr);
            }
        } else {
            // method call within the same class
            handleClassMethod(currentClassName, methodSimpleName, true);
        }
        super.visit(methodCallExpr, arg);
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
    }

}
