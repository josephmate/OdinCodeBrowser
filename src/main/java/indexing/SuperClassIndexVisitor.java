package indexing;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Map;

public class SuperClassIndexVisitor extends VoidVisitorAdapter<Void> {

    private final Index index;
    private final Map<String, String> importIndex;

    public SuperClassIndexVisitor(
            Index index,
            Map<String, String> importIndex
    ) {
        this.index = index;
        this.importIndex = importIndex;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Void arg) {
        if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
            String fullyQualifiedClassName = classOrInterfaceDeclaration.getFullyQualifiedName().get();

            if (classOrInterfaceDeclaration.getExtendedTypes().size() > 1) {
                System.out.println(fullyQualifiedClassName + " has "
                    + classOrInterfaceDeclaration.getExtendedTypes()
                    + " extended types");
            }

            boolean found = false;
            for (ClassOrInterfaceType extension : classOrInterfaceDeclaration.getExtendedTypes()) {
                String superClassFullyQualifiedName = importIndex.get(extension.getNameAsString());
                if (superClassFullyQualifiedName != null) {
                    found = true;
                }
            }

            if (!found) {
                index.addSuperClass(fullyQualifiedClassName, "java.lang.Object");
            }
        }
        super.visit(classOrInterfaceDeclaration, arg);
    }

    /* Records and enums cannot extend so we only consider classes */
}