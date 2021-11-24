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
            String inputDirectory,
            Collection<Path> paths,
            Index index
    ) {
        for (Path path: paths) {
            try {
                indexFile(inputDirectory, index, path);
            } catch (Exception e) {
                throw new RuntimeException("Error processing " + path, e);
            }
        }
    }

    private String getFileUrl(
            String inputSourceDirectory,
            Path javaSourceFile
    ) {
        return odinOptions.webPathToSourceHtmlFiles
                + (
                javaSourceFile.toString()
                        .substring(0, javaSourceFile.toString().length()-5)
                        .replace(inputSourceDirectory, "")
                        + ".html"
        );
    }

    private void indexFile(
            String inputDirectory,
            Index index,
            Path path
    ) throws IOException {
        System.out.println("Indexing " + path);
        final String fileUrl = getFileUrl(inputDirectory, path);
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
