package rendering.source;

import java.util.HashMap;
import java.util.Map;

class RenderingQueue {

    public final Map<Integer, RenderQueueLine> renderingQueue = new HashMap<>();

    public void add(int line, int col, ContentRecord contentRecord) {
        RenderQueueLine renderQueueLine = renderingQueue.get(line);
        if (renderQueueLine == null) {
            renderQueueLine = new RenderQueueLine();
            renderingQueue.put(line, renderQueueLine);
        }
        renderQueueLine.add(col, contentRecord);
    }
}
