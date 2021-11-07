package rendering.source;

import com.github.javaparser.Range;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;

/**
 * Applies syntax highlighting to the source by recording the changes that are need
 * in the rendering queue.
 */
public class ApplySyntaxHighlightingVisitor  extends VoidVisitorAdapter<Void> {


    private final RenderingQueue renderingQueue;

    public ApplySyntaxHighlightingVisitor(
            RenderingQueue renderingQueue
    ) {
        this.renderingQueue = renderingQueue;
    }

    private void handleComment(Optional<Range> range) {
        int startLineNum = range.get().begin.line;
        int startCol = range.get().begin.column;
        int endLineNum = range.get().end.line;
        int endCol = range.get().end.column;

        renderingQueue.add(
                startLineNum,
                startCol,
                new ContentRecord(
                        "<span class='comment'>",
                        PositionType.BEFORE
                )
        );
        renderingQueue.add(
                endLineNum,
                endCol,
                new ContentRecord(
                        "</span>",
                        PositionType.AFTER
                )
        );
        if (startLineNum != endLineNum) {
            renderingQueue.endOfLine(startLineNum, "<span class='comment'>");
            for (int i = startLineNum + 1; i < endLineNum; i++) {
                renderingQueue.beginningOfLine(i, "<span class='comment'>");
                renderingQueue.endOfLine(i, "<span class='comment'>");
            }
            renderingQueue.endOfLine(endLineNum, "<span class='comment'>");
        }
    }

    @Override
    public void visit(LineComment comment, Void arg) {
        handleComment(comment.getRange());
        super.visit(comment, arg);
    }

    @Override
    public void visit(JavadocComment comment, Void arg) {
        handleComment(comment.getRange());
        super.visit(comment, arg);
    }

    @Override
    public void visit(BlockComment comment, Void arg) {
        handleComment(comment.getRange());
        super.visit(comment, arg);
    }
}
