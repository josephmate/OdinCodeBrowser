package indexing;

import com.fasterxml.jackson.databind.ObjectMapper;
import util.StringUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class UrlIndexLoader {

    public Index load(List<String> urls) throws IOException {
        Index allExternalIndexes = new Index();

        ObjectMapper objectMapper = new ObjectMapper();
        for (String url : urls) {
            String prefixUrl = url.substring(0, url.length() - "index.json".length());

            Index externalIndex = objectMapper.readValue(new URL(url), Index.class);

            for (Map.Entry<String, Index.FilePosition> entry : externalIndex.classIndex.entrySet()) {
                String fullyQualifiedClassName = entry.getKey();
                Index.FilePosition filePosition = entry.getValue();
                allExternalIndexes.addClass(
                        fullyQualifiedClassName,
                        StringUtil.mergeStrings(prefixUrl, filePosition.fileName()),
                        filePosition.lineNumber()
                );
            }

            for (Map.Entry<String, Map<String, VariableInfo>> entry : externalIndex.variableIndex.entrySet()) {
                String fullyQualifiedClassName = entry.getKey();
                for (Map.Entry<String, VariableInfo> entry2: entry.getValue().entrySet()) {
                    String variableName = entry2.getKey();
                    VariableInfo variableInfo = entry2.getValue();
                    allExternalIndexes.addVariable(
                        fullyQualifiedClassName,
                        variableName,
                        new VariableInfo(
                            variableInfo.type(),
                            new Index.FilePosition(
                                StringUtil.mergeStrings(prefixUrl, variableInfo.filePosition().fileName()),
                                    variableInfo.filePosition().lineNumber()
                            )
                        )
                    );
                }
            }

            for (Map.Entry<String, Map<String, List<MethodInfo>>> entry : externalIndex.methodIndex.entrySet()) {
                String fullyQualifiedClassName = entry.getKey();
                for (Map.Entry<String, List<MethodInfo>> entry2: entry.getValue().entrySet()) {
                    String methodName = entry2.getKey();
                    List<MethodInfo> overloads = entry2.getValue();
                    for(MethodInfo overload : overloads) {
                        allExternalIndexes.addMethod(
                            fullyQualifiedClassName,
                            methodName,
                            new MethodInfo(
                                overload.argumentTypes(),
                                overload.returnType(),
                                new Index.FilePosition(
                                    StringUtil.mergeStrings(prefixUrl, overload.filePosition().fileName()),
                                    overload.filePosition().lineNumber()
                                )
                            )
                        );
                    }
                }
            }

            // purposely skip privateMethodIndex since I don't expect dependants to use
            // privates in the dependency
        }

        return allExternalIndexes;
    }

}
