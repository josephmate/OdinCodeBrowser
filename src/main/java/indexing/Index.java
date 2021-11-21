package indexing;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public final Map<String, List<String>> superClassMap = new HashMap<>();

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
        List<String> superClasses = superClasses = superClassMap.get(subClassFullyQualifiedName);
        if (superClasses == null) {
            superClasses = new ArrayList<>();
            superClassMap.put(subClassFullyQualifiedName, superClasses);
        }
        superClasses.add(superClassFullyQualifiedName);
    }

    public FilePosition getClass(String fullyQualifiedName) {
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

    public Map<String, List<String>> getSuperClassMap() {
        return superClassMap;
    }

    public void addAll(Index otherIndex) {
        for (Map.Entry<String, Index.FilePosition> entry : otherIndex.classIndex.entrySet()) {
            String fullyQualifiedClassName = entry.getKey();
            Index.FilePosition filePosition = entry.getValue();
            this.addClass(
                    fullyQualifiedClassName,
                    filePosition.fileName(),
                    filePosition.lineNumber()
            );
        }
        for (Map.Entry<String, Map<String, Index.FilePosition>> entry : otherIndex.variableIndex.entrySet()) {
            String fullyQualifiedClassName = entry.getKey();
            for (Map.Entry<String, Index.FilePosition> entry2: entry.getValue().entrySet()) {
                String variableName = entry2.getKey();
                Index.FilePosition filePosition = entry2.getValue();
                this.addVariable(
                        fullyQualifiedClassName,
                        variableName,
                        filePosition.fileName(),
                        filePosition.lineNumber()
                );
            }
        }
        for (Map.Entry<String, Map<String, Index.FilePosition>> entry : otherIndex.methodIndex.entrySet()) {
            String fullyQualifiedClassName = entry.getKey();
            for (Map.Entry<String, Index.FilePosition> entry2: entry.getValue().entrySet()) {
                String methodName = entry2.getKey();
                Index.FilePosition filePosition = entry2.getValue();
                this.addMethod(
                        fullyQualifiedClassName,
                        methodName,
                        filePosition.fileName(),
                        filePosition.lineNumber()
                );
            }
        }
        for (Map.Entry<String, Map<String, Index.FilePosition>> entry : otherIndex.privateMethodIndex.entrySet()) {
            String fullyQualifiedClassName = entry.getKey();
            for (Map.Entry<String, Index.FilePosition> entry2: entry.getValue().entrySet()) {
                String methodName = entry2.getKey();
                Index.FilePosition filePosition = entry2.getValue();
                this.addPrivateMethod(
                        fullyQualifiedClassName,
                        methodName,
                        filePosition.fileName(),
                        filePosition.lineNumber()
                );
            }
        }
        for (Map.Entry<String, List<String>> entry : otherIndex.superClassMap.entrySet()) {
            String fullyQualifiedClassName = entry.getKey();
            for (String superClass : entry.getValue()) {
                this.addSuperClass(
                        fullyQualifiedClassName,
                        superClass
                );
            }
        }

        this.superClassMap.putAll(otherIndex.getSuperClassMap());
    }

    public record FilePosition (
        String fileName,
        int lineNumber) { }
}

