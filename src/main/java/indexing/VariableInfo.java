package indexing;

/***
 * @param filePosition position this varible appears in the file
 * @param type the Fully Qualified Class Name of the type
 */
public record VariableInfo(
        String type,
        Index.FilePosition filePosition
) {
}
