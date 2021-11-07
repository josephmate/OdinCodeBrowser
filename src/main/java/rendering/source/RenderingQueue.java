package rendering.source;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RenderingQueue {

    public final Map<Integer, RenderQueueLine> withinLineRenderingQueue = new HashMap<>();
    public final MultiValuedMap<Integer, String> beginningOfLineRenderingQueue = new ArrayListValuedHashMap<>();
    public final MultiValuedMap<Integer, String> endOfLineRenderingQueue = new ArrayListValuedHashMap<>();

    public void add(int line, int col, ContentRecord contentRecord) {
        RenderQueueLine renderQueueLine = withinLineRenderingQueue.get(line);
        if (renderQueueLine == null) {
            renderQueueLine = new RenderQueueLine();
            withinLineRenderingQueue.put(line, renderQueueLine);
        }
        renderQueueLine.add(col, contentRecord);
    }

    public void beginningOfLine(int line, String content) {
        beginningOfLineRenderingQueue.put(line, content);
    }
    public void endOfLine(int line, String content) {
        endOfLineRenderingQueue.put(line, content);
    }
}
