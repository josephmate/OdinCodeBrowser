package options;

import com.github.javaparser.ParserConfiguration;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OdinOptions {

    @Option(name="--inputSourceDirectory",
        required = true,
        usage= """
               The directory contain the source code. For instance,
               if you had a maven project, it would probably be
               <project_root>/src/main/java. You can specifiy multiple
               directories typing multiple --inputSourceDirectory parameters.
               You should specify the all the dependant directories before
               specifying the directory. For instance in jdk17 you type something
               like
               --inputSourceDirectory src/java.base/share/classes
               --inputSourceDirectory src/java.logging/share/classses
               --inputSourceDirectory src/java.xml/share/classses
               --inputSourceDirectory src/java.transactions.xa/share/classses
               --inputSourceDirectory src/java.sql/share/classses
               --inputSourceDirectory src/java.sql.rowset/share/classses
               """)
    public List<String> inputSourceDirectories;

    @Option(name="--outputDirectory",
            required = true,
            usage= """
               The directory where all the html files for the navigable source code will be written.
               Odin will also write the index.html and index.json directly in this directory.
               """)
    public String outputDirectory;

    @Option(name="--webPathToCssFile",
            required = true,
            usage= """
               The path to the css file. For instance, Odin hosts jdk8 at /OdinCodeBrowser/jdk8 but
               the style is in /OdinCodeBrowser/styles.css then this should be /OdinCodeBrowser/styles.css
               that way multiple repos can shares the same style.
               """)
    public String webPathToCssFile;

    @Option(name="--webPathToSourceHtmlFiles",
            required = true,
            usage= """
               The root path to the html versions of the source files generated using --inputSourceDirectory.
               For instance, OdinCodeBrowser hosts the jdk8 source in /OdinCodeBrowser/jdk8 so it uses
               --webPathToSourceHtmlFiles /OdinCodeBrowser/jdk8
               """)
    public String webPathToSourceHtmlFiles;

    @Option(name="--urlToDependantIndexJson",
            required = false,
            usage= """
               Url of the index.json of the dependencies of this repository.
               """)
    public List<String> urlsToDependantIndexJsons = new ArrayList<>();

    @Option(name="--languageLevel",
            required = false,
            usage= """
               Which version of the JDK the source code uses.
               """)
    public ParserConfiguration.LanguageLevel languageLevel = ParserConfiguration.LanguageLevel.JAVA_16;


    @Option(name="--multiRepoRoot",
            required = false,
            usage= """
               If this this website hold multiple repos under a root directory, provide this root directory
               so that the repositories can link back the the repository list.
               """)
    public String multiRepoRoot = null;
}
