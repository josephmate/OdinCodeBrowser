import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("<input_directory> <output_directory>");
            System.exit(-1);
        }
        Director director = new Director(
                args[0],
                args[1]
        );
        director.processFiles();
    }

}
