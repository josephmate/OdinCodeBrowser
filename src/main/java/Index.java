import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Index {

    private final Map<String, FilePosition> classIndex = new HashMap<>();

    public void indexFile(
            Path inputFile,
            String fileUrl
    ) throws IOException {
        CompilationUnit compilationUnit = StaticJavaParser.parse(inputFile);
        IndexVisitor indexVisitor = new IndexVisitor(this, fileUrl);
        indexVisitor.visit(compilationUnit, null);
    }

    public void addClass(
            String fullyQualifiedName,
            String fileUrl,
            int lineNumber) {
        classIndex.put(fullyQualifiedName, new FilePosition(fileUrl, lineNumber));
    }

    public record FilePosition (
        String fileName,
        int lineNumber) { }
}

class IndexVisitor extends VoidVisitorAdapter<Void> {

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
        super.visit(classOrInterfaceDeclaration, arg);
        if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
            index.addClass(
                    classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                    fileUrl,
                    classOrInterfaceDeclaration.getRange().get().begin.line
            );
        }
    }
}