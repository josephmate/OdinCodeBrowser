package indexing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import options.OdinOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SuperClassIndexer {

    private final Index externalIndex;
    private final OdinOptions odinOptions;

    public SuperClassIndexer(
            Index externalIndex,
            OdinOptions odinOptions
    ) {
        this.externalIndex = externalIndex;
        this.odinOptions = odinOptions;
    }


    public void indexFiles(
            List<Path> files,
            Index localIndex) {
        for (Path file: files) {
            try {
                indexFile(file, localIndex);
            } catch (Exception e) {
                throw new RuntimeException("Error processing " + file, e);
            }
        }
    }

    private void indexFile(
            Path file,
            Index localIndex
    ) throws IOException {
        JavaParser javaParser = new JavaParser(new ParserConfiguration().setLanguageLevel(
                odinOptions.languageLevel));
        CompilationUnit compilationUnit = javaParser.parse(file).getResult().get();
        SuperClassIndexVisitor indexVisitor = new SuperClassIndexVisitor(localIndex, TODO);
        indexVisitor.visit(compilationUnit, null);
    }
}
