package indexing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import options.OdinOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class Indexer {

    private final OdinOptions odinOptions;

    public Indexer(
            OdinOptions odinOptions
    ) {
        this.odinOptions = odinOptions;
    }

    public void indexFiles(
            Collection<Path> paths,
            Index index
    ) {
        for (Path path: paths) {
            try {
                indexFile(index, path);
            } catch (Exception e) {
                throw new RuntimeException("Error processing " + path, e);
            }
        }
    }

    private String getFileUrl(Path javaSourceFile) {
        return odinOptions.webPathToSourceHtmlFiles
                + (
                javaSourceFile.toString()
                        .substring(0, javaSourceFile.toString().length()-5)
                        .replace(odinOptions.inputSourceDirectory, "")
                        + ".html"
        );
    }

    private void indexFile(Index index, Path path) throws IOException {
        System.out.println("Indexing " + path);
        final String fileUrl = getFileUrl(path);
        indexFile(
                index,
                path,
                fileUrl,
                odinOptions.languageLevel
        );
    }

    private void indexFile(
            Index index,
            Path inputFile,
            String fileUrl,
            ParserConfiguration.LanguageLevel languageLevel
    ) throws IOException {
        JavaParser javaParser = new JavaParser(new ParserConfiguration().setLanguageLevel(
                languageLevel));
        CompilationUnit compilationUnit = javaParser.parse(inputFile).getResult().get();
        IndexVisitor indexVisitor = new IndexVisitor(index, fileUrl);
        indexVisitor.visit(compilationUnit, null);
    }
}
