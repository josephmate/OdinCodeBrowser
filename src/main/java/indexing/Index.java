package indexing;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class Index {

    /**
     * Fully Qualified Class Name -> File Position
     */
    public final Map<String, FilePosition> classIndex = new HashMap<>();

    /**
     * Fully Qualified Class Name -> method name -> File Position
     */
    public final Map<String, Map<String, List<MethodInfo>>> methodIndex = new HashMap<>();

    /**
     * Fully Qualified Class Name -> method name -> File Position
     */
    @JsonIgnore
    public final Map<String, Map<String, List<MethodInfo>>> privateMethodIndex = new HashMap<>();

    /**
     * Fully Qualified Class Name -> variable name -> File Position
     */
    public final Map<String, Map<String, VariableInfo>> variableIndex = new HashMap<>();

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
            MethodInfo methodInfo
    ) {
        Map<String, List<MethodInfo>> methodSubMap = methodIndex.get(fullyQualifiedName);
        if (methodSubMap == null) {
            methodSubMap = new HashMap<>();
            methodIndex.put(fullyQualifiedName, methodSubMap);
        }

        List<MethodInfo> overloads = methodSubMap.get(methodName);
        if (overloads == null) {
            overloads = new ArrayList<>();
            methodSubMap.put(methodName, overloads);
        }
        overloads.add(methodInfo);
    }

    public void addPrivateMethod(
            String fullyQualifiedName,
            String methodName,
            MethodInfo methodInfo
    ) {
        Map<String, List<MethodInfo>> methodSubMap = privateMethodIndex.get(fullyQualifiedName);
        if (methodSubMap == null) {
            methodSubMap = new HashMap<>();
            privateMethodIndex.put(fullyQualifiedName, methodSubMap);
        }

        List<MethodInfo> overloads = methodSubMap.get(methodName);
        if (overloads == null) {
            overloads = new ArrayList<>();
            methodSubMap.put(methodName, overloads);
        }
        overloads.add(methodInfo);
    }

    public void addVariable(
            String fullyQualifiedName,
            String variableName,
            VariableInfo variableInfo
    ) {
        Map<String, VariableInfo> variableSubMap = variableIndex.get(fullyQualifiedName);
        if (variableSubMap == null) {
            variableSubMap = new HashMap<>();
            variableIndex.put(fullyQualifiedName, variableSubMap);
        }
        variableSubMap.put(variableName, variableInfo);
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

    public MethodInfo getMethod(
            String fullyQualifiedName,
            String methodName,
            List<String> argumentTypes
    ) {
        Map<String, List<MethodInfo>> methodSubMap = methodIndex.get(fullyQualifiedName);
        if (methodSubMap == null) {
            return null;
        }

        List<MethodInfo> overloads = methodSubMap.get(methodName);
        if (overloads == null) {
            return null;
        }

        for (MethodInfo methodInfo : overloads) {
            if (methodInfo.argumentTypes().equals(argumentTypes)) {
                return methodInfo;
            }
        }

        return null;
    }

    public MethodInfo getPrivateMethod(
            String fullyQualifiedName,
            String methodName,
            List<String> argumentTypes
    ) {
        Map<String, List<MethodInfo>> methodSubMap = privateMethodIndex.get(fullyQualifiedName);
        if (methodSubMap == null) {
            return null;
        }

        List<MethodInfo> overloads = methodSubMap.get(methodName);
        if (overloads == null) {
            return null;
        }

        for (MethodInfo methodInfo : overloads) {
            if (methodInfo.argumentTypes().equals(argumentTypes)) {
                return methodInfo;
            }
        }

        return null;
    }

    public List<String> getSuperClasses(String fullyQualifiedClassName) {
        List<String> result = superClassMap.get(fullyQualifiedClassName);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public Map<String, FilePosition> getClassIndex() {
        return classIndex;
    }

    public Map<String, Map<String, List<MethodInfo>>> getMethodIndex() {
        return methodIndex;
    }

    public Map<String, Map<String, VariableInfo>> getVariableIndex() {
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
        for (Map.Entry<String, Map<String, VariableInfo>> entry : otherIndex.variableIndex.entrySet()) {
            String fullyQualifiedClassName = entry.getKey();
            for (Map.Entry<String, VariableInfo> entry2: entry.getValue().entrySet()) {
                String variableName = entry2.getKey();
                VariableInfo variableInfo = entry2.getValue();
                this.addVariable(
                        fullyQualifiedClassName,
                        variableName,
                        variableInfo
                );
            }
        }
        for (Map.Entry<String, Map<String, List<MethodInfo>>> entry : otherIndex.methodIndex.entrySet()) {
            String fullyQualifiedClassName = entry.getKey();
            for (Map.Entry<String, List<MethodInfo>> entry2: entry.getValue().entrySet()) {
                String methodName = entry2.getKey();
                List<MethodInfo> overloads = entry2.getValue();
                for (MethodInfo overload : overloads) {
                    this.addMethod(
                            fullyQualifiedClassName,
                            methodName,
                            overload
                    );
                }
            }
        }
        for (Map.Entry<String, Map<String, List<MethodInfo>>> entry : otherIndex.privateMethodIndex.entrySet()) {
            String fullyQualifiedClassName = entry.getKey();
            for (Map.Entry<String, List<MethodInfo>> entry2: entry.getValue().entrySet()) {
                String methodName = entry2.getKey();
                List<MethodInfo> overloads = entry2.getValue();
                for (MethodInfo overload : overloads) {
                    this.addMethod(
                            fullyQualifiedClassName,
                            methodName,
                            overload
                    );
                }
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

