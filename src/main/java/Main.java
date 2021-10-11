import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println(
                    "<input_directory>" +
                    " <output_directory>" +
                    " <web_root_dir>" +
                    " <source_sub_url>" +
                    System.lineSeparator() + "\t<input_directory>: directory containing all the java source files"+
                    System.lineSeparator() + "\t<output_directory>: the directory to write all the source files to"+
                    System.lineSeparator() + "\t<web_root_dir>: the path that the html files will appear on the website" +
                    System.lineSeparator() + "\t                for instance I write the files to /doc/jdk8/ but it appears at https://host/OdinCodeBrowser/" +
                    System.lineSeparator() + "\t<source_sub_url>: the sub path that the source files the the project will appear" +
                    System.lineSeparator() + "\t                  for instance the root of the website is at https://host/OdinCodeBrowser/ but the project is under https://host/OdinCodeBrowser/jdk8"
            );
            System.exit(-1);
        }
        int i = 0;
        Director director = new Director(
                args[i++],
                args[i++],
                args[i++],
                args[i++]
        );
        director.processFiles();
    }

}
