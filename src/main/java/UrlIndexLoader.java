import com.fasterxml.jackson.databind.ObjectMapper;

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

            for (Map.Entry<String, Map<String, Index.FilePosition>> entry : externalIndex.variableIndex.entrySet()) {
                String fullyQualifiedClassName = entry.getKey();
                for (Map.Entry<String, Index.FilePosition> entry2: entry.getValue().entrySet()) {
                    String variableName = entry2.getKey();
                    Index.FilePosition filePosition = entry2.getValue();
                    allExternalIndexes.addVariable(
                            fullyQualifiedClassName,
                            variableName,
                            StringUtil.mergeStrings(prefixUrl, filePosition.fileName()),
                            filePosition.lineNumber()
                    );
                }
            }

            for (Map.Entry<String, Map<String, Index.FilePosition>> entry : externalIndex.methodIndex.entrySet()) {
                String fullyQualifiedClassName = entry.getKey();
                for (Map.Entry<String, Index.FilePosition> entry2: entry.getValue().entrySet()) {
                    String methodName = entry2.getKey();
                    Index.FilePosition filePosition = entry2.getValue();
                    allExternalIndexes.addMethod(
                            fullyQualifiedClassName,
                            methodName,
                            StringUtil.mergeStrings(prefixUrl, filePosition.fileName()),
                            filePosition.lineNumber()
                    );
                }
            }
            // purposely skip privateMethodIndex since I don't expect dependants to use
            // privates in the dependency
        }

        return allExternalIndexes;
    }

}
