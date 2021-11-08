package rendering.source;

import com.github.javaparser.Range;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
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

    private void highlightToken(
            Optional<Range> range,
            String cssClass
    ) {
        int startLineNum = range.get().begin.line;
        int startCol = range.get().begin.column;
        int endLineNum = range.get().end.line;
        int endCol = range.get().end.column;

        final String startTag = "<span class='" + cssClass+ "'>";
        final String endTag = "</span>";

        renderingQueue.add(
                startLineNum,
                startCol,
                new ContentRecord(
                        startTag,
                        PositionType.BEFORE
                )
        );
        renderingQueue.add(
                endLineNum,
                endCol,
                new ContentRecord(
                        endTag,
                        PositionType.AFTER
                )
        );
        if (startLineNum != endLineNum) {
            renderingQueue.endOfLine(startLineNum, endTag);
            for (int i = startLineNum + 1; i < endLineNum; i++) {
                renderingQueue.beginningOfLine(i, startTag);
                renderingQueue.endOfLine(i, endTag);
            }
            renderingQueue.endOfLine(endLineNum, endTag);
        }
    }

    @Override
    public void visit(LineComment comment, Void arg) {
        highlightToken(comment.getRange(), "comment");
        super.visit(comment, arg);
    }

    @Override
    public void visit(JavadocComment comment, Void arg) {
        highlightToken(comment.getRange(), "comment");
        super.visit(comment, arg);
    }

    @Override
    public void visit(BlockComment comment, Void arg) {
        highlightToken(comment.getRange(), "comment");
        super.visit(comment, arg);
    }

    @Override
    public void visit(StringLiteralExpr str, Void arg) {
        highlightToken(str.getRange(), "string");
        super.visit(str, arg);
    }

    @Override
    public void visit(TextBlockLiteralExpr str, Void arg) {
        highlightToken(str.getRange(), "string");
        super.visit(str, arg);
    }
}
