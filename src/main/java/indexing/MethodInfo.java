package indexing;

import java.util.List;

/***
 * @param argumentTypes list list of the Fully Qualified Class Name of the method parameters
 * @param returnType the Fully Qualified Class Name of the return type
 * @param filePosition position this varible appears in the file
 */
public record MethodInfo(
        List<String> argumentTypes,
        String returnType,
        Index.FilePosition filePosition
) {
}
