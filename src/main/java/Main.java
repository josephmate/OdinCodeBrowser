import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;

public class Main {



    public static void main(String[] args) throws IOException {
        OdinOptions bean = new OdinOptions();
        CmdLineParser parser = new CmdLineParser(bean);

        try {
            parser.parseArgument(args);
            Director director = new Director(bean);
            director.processFiles();
        } catch(CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar odin-browser.jar Main [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }
    }

}
