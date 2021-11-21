package indexing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import options.OdinOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SuperClassIndexer {

    private final Index completeIndex;
    private final OdinOptions odinOptions;

    public SuperClassIndexer(
            Index completeIndex,
            OdinOptions odinOptions
    ) {
        this.completeIndex = completeIndex;
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

        ImportVisitor importVisitor = new ImportVisitor(completeIndex);
        importVisitor.visit(compilationUnit, null);

        SuperClassIndexVisitor indexVisitor = new SuperClassIndexVisitor(localIndex, importVisitor.imports);
        indexVisitor.visit(compilationUnit, null);
    }
}
