package indexing;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import indexing.Index;

import java.util.HashMap;
import java.util.Map;

/**
 * Determines all imports we need to consider including:
 * <ul>
 *     <li>java.util.HashMap</li>
 *     <li>java.util.*</li>
 *     <li>java.lang.* (always included)</li>
 *     <li>current.package.* (always included)</li>
 * </ul>
 */
public class ImportVisitor extends VoidVisitorAdapter<Void> {

    /**
     * SimpleName (ex: Object) to Fully Qualified Name (ex: java.lang.Object)
     */
    public final Map<String, String> imports = new HashMap<>();
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