package indexing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Index {

    /**
     * Fully Qualified Class Name -> File Position
     */
    public final Map<String, FilePosition> classIndex = new HashMap<>();

    /**
     * Fully Qualified Class Name -> method name -> File Position
     */
    public final Map<String, Map<String, FilePosition>> methodIndex = new HashMap<>();

    /**
     * Fully Qualified Class Name -> method name -> File Position
     */
    @JsonIgnore
    public final Map<String, Map<String, FilePosition>> privateMethodIndex = new HashMap<>();

    /**
     * Fully Qualified Class Name -> variable name -> File Position
     */
    public final Map<String, Map<String, FilePosition>> variableIndex = new HashMap<>();

    public final Map<String, String> superClasses = new HashMap<>();

    public void indexFile(
            Path inputFile,
            String fileUrl,
            ParserConfiguration.LanguageLevel languageLevel
    ) throws IOException {
        JavaParser javaParser = new JavaParser(new ParserConfiguration().setLanguageLevel(
                languageLevel));
        CompilationUnit compilationUnit = javaParser.parse(inputFile).getResult().get();
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

    public void addVariable(
            String fullyQualifiedName,
            String variableName,
            String fileUrl,
            int lineNumber
    ) {
        FilePosition filePosition = new FilePosition(fileUrl, lineNumber);
        Map<String, FilePosition> variableSubMap = variableIndex.get(fullyQualifiedName);
        if (variableSubMap == null) {
            variableSubMap = new HashMap<>();
            variableIndex.put(fullyQualifiedName, variableSubMap);
        }
        variableSubMap.put(variableName, filePosition);
    }

    public void addSuperClass(
        String subClassFullyQualifiedName,
        String superClassFullyQualifiedName
    ) {
        superClasses.put(subClassFullyQualifiedName, superClassFullyQualifiedName);
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

    public Map<String, Map<String, FilePosition>> getMethodIndex() {
        return methodIndex;
    }

    public Map<String, Map<String, FilePosition>> getVariableIndex() {
        return variableIndex;
    }

    public Map<String, String> getSuperClasses() {
        return superClasses;
    }

    public record FilePosition (
        String fileName,
        int lineNumber) { }
}

