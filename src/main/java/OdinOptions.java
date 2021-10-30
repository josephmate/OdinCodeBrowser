import org.kohsuke.args4j.Option;

import java.util.List;

public class OdinOptions {

    @Option(name="--inputSourceDirectory",
        required = true,
        usage= """
               The directory contain the source code. For instance,
               if you had a maven project, it would probably be
               <project_root>/src/main/java
               """)
    public String inputSourceDirectory;

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
            required = true,
            usage= """
               Url of the index.json of the dependencies of this repository.
               """)
    public List<String> urlsToDependantIndexJsons;
}
