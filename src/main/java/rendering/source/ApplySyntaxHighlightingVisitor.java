package rendering.source;

import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import indexing.Index;

import java.util.Map;

public class ApplySyntaxHighlightingVisitor  extends VoidVisitorAdapter<Void> {


    private final RenderingQueue renderingQueue;

    public ApplySyntaxHighlightingVisitor(
            RenderingQueue renderingQueue
    ) {
        this.renderingQueue = renderingQueue;
    }
}
