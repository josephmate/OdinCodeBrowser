package indexing;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class IndexVisitor extends VoidVisitorAdapter<Void> {

    private final Index index;
    private final String fileUrl;

    public IndexVisitor(
            Index index,
            String fileUrl
    ) {
        this.index = index;
        this.fileUrl = fileUrl;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Void arg) {
        if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
            index.addClass(
                    classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                    fileUrl,
                    classOrInterfaceDeclaration.getRange().get().begin.line
            );
        }
        super.visit(classOrInterfaceDeclaration, arg);
    }

    @Override
    public void visit(RecordDeclaration recordDeclaration, Void arg) {
        if (recordDeclaration.getFullyQualifiedName().isPresent()) {
            index.addClass(
                    recordDeclaration.getFullyQualifiedName().get(),
                    fileUrl,
                    recordDeclaration.getRange().get().begin.line
            );
        }
        super.visit(recordDeclaration, arg);
    }

    @Override
    public void visit(EnumDeclaration enumDeclaration, Void arg) {
        if (enumDeclaration.getFullyQualifiedName().isPresent()) {
            index.addClass(
                    enumDeclaration.getFullyQualifiedName().get(),
                    fileUrl,
                    enumDeclaration.getRange().get().begin.line
            );
        }
        super.visit(enumDeclaration, arg);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg) {
        String methodName = methodDeclaration.getName().asString();
        if (methodDeclaration.getParentNode().isPresent()
                && methodDeclaration.getParentNode().get() instanceof ClassOrInterfaceDeclaration
        ) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
            if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
                if (methodDeclaration.isPrivate()) {

                        /* TODO: method_chaining
                    index.addPrivateMethod(
                            classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                            methodName,
                            fileUrl,
                            methodDeclaration.getRange().get().begin.line
                    );
                         */
                } else {

                        /* TODO: method_chaining
                    index.addMethod(
                            classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                            methodName,
                            fileUrl,
                            methodDeclaration.getRange().get().begin.line
                    );
                         */
                }
            }
        }
        super.visit(methodDeclaration, arg);
    }

    public void visit(FieldDeclaration fieldDeclaration, Void arg) {
        if (fieldDeclaration.getParentNode().isPresent()
                && fieldDeclaration.getParentNode().get() instanceof ClassOrInterfaceDeclaration
        ) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) fieldDeclaration.getParentNode().get();
            if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
                if (!fieldDeclaration.isPrivate()) {
                    for (VariableDeclarator vd : fieldDeclaration.getVariables()) {
                        /* TODO: method_chaining
                        vd.getType().is

                        index.addVariable(
                                classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                                vd.getNameAsString(),
                                fileUrl,
                                fieldDeclaration.getRange().get().begin.line
                        );
                        */
                    }
                }
            }
        }
        super.visit(fieldDeclaration, arg);
    }
}
