package rendering;

import com.fasterxml.jackson.databind.ObjectMapper;
import indexing.Index;

import java.io.File;
import java.io.IOException;

/**
 * Creates a json file with the index. Allows other source repositories to point to this repository.
 */
public class IndexJsonRenderer {

    public void render(
            String outputFile,
            Index index
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(outputFile), index);
    }

}
