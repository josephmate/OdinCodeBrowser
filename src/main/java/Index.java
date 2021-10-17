import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Index {

    private final Map<String, FilePosition> classIndex = new HashMap<>();
    private final Map<String, Map<String, FilePosition>> methodIndex = new HashMap<>();
    private final Map<String, Map<String, FilePosition>> privateMethodIndex = new HashMap<>();

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

    public void addMethod(
            String fullyQualifiedName,
            String methodName,
            String fileUrl,
            int lineNumber
    ) {
        FilePosition filePosition = new FilePosition(fileUrl, lineNumber);
        Map<String, FilePosition> methodSubMap = methodIndex.get(fullyQualifiedName);
        if (methodSubMap == null) {
            methodSubMap = new HashMap<>();
            methodIndex.put(fullyQualifiedName, methodSubMap);
        }
        methodSubMap.put(methodName, filePosition);
    }
    public void addPrivateMethod(
            String fullyQualifiedName,
            String methodName,
            String fileUrl,
            int lineNumber
    ) {
        FilePosition filePosition = new FilePosition(fileUrl, lineNumber);
        Map<String, FilePosition> methodSubMap = privateMethodIndex.get(fullyQualifiedName);
        if (methodSubMap == null) {
            methodSubMap = new HashMap<>();
            privateMethodIndex.put(fullyQualifiedName, methodSubMap);
        }
        methodSubMap.put(methodName, filePosition);
    }

    public FilePosition get(String fullyQualifiedName) {
        return classIndex.get(fullyQualifiedName);
    }

    public FilePosition getMethod(
            String fullyQualifiedName,
            String methodName
    ) {
        Map<String, FilePosition> methodSubMap = methodIndex.get(fullyQualifiedName);
        if (methodSubMap == null) {
            return null;
        }
        return methodSubMap.get(methodName);
    }

    public FilePosition getPrivateMethod(
            String fullyQualifiedName,
            String methodName
    ) {
        Map<String, FilePosition> methodSubMap = privateMethodIndex.get(fullyQualifiedName);
        if (methodSubMap == null) {
            return null;
        }
        return methodSubMap.get(methodName);
    }

    public Map<String, FilePosition> getClassIndex() {
        return classIndex;
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
    public void visit(MethodDeclaration methodDeclaration, Void arg) {
        String methodName = methodDeclaration.getName().asString();
        if (methodDeclaration.getParentNode().isPresent()
                && methodDeclaration.getParentNode().get() instanceof ClassOrInterfaceDeclaration
        )  {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration)methodDeclaration.getParentNode().get();
            if (classOrInterfaceDeclaration.getFullyQualifiedName().isPresent()) {
                if (methodDeclaration.isPrivate()) {
                    index.addPrivateMethod(
                            classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                            methodName,
                            fileUrl,
                            methodDeclaration.getRange().get().begin.line
                    );
                } else {
                    index.addMethod(
                            classOrInterfaceDeclaration.getFullyQualifiedName().get(),
                            methodName,
                            fileUrl,
                            methodDeclaration.getRange().get().begin.line
                    );
                }
            }
        }
        super.visit(methodDeclaration, arg);
    }
}