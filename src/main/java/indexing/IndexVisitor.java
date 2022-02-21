package indexing;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.stream.Collectors;

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
            List<String> argTypes = methodDeclaration.getParameters().stream()
                    .map(Parameter::getTypeAsString)
                    .collect(Collectors.toList());
            String returnType = methodDeclaration.getTypeAsString();
            MethodInfo methodInfo = new MethodInfo(
                argTypes,
                returnType,
                new Index.FilePosition(
                    fileUrl,
                    methodDeclaration.getRange().get().begin.line
                )
            );

            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
            if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
                if (methodDeclaration.isPrivate()) {
                    index.addPrivateMethod(
                            classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                            methodName,
                            methodInfo
                    );
                } else {
                    index.addMethod(
                            classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                            methodName,
                            methodInfo
                    );
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
                        index.addVariable(
                            classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                            vd.getNameAsString(),
                            new VariableInfo(
                                vd.getTypeAsString(),
                                new Index.FilePosition(
                                        fileUrl,
                                        fieldDeclaration.getRange().get().begin.line
                                )
                            )
                        );
                    }
                }
            }
        }
        super.visit(fieldDeclaration, arg);
    }
}
