import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;

public class SimpleExperimentMain {
  public static void main(String [] args) throws Exception {
    System.out.println("Hello World!");
    CompilationUnit compilationUnit = StaticJavaParser.parse(new File(args[0]));
    VoidVisitorAdapter visitor = new VoidVisitorAdapter<Void>() {
      /**
       * Some Javadoc
       *
       * @param md
       * @param arg
       */
      @Override
      public void visit(MethodDeclaration md, Void arg) {
        super.visit(md, arg);
        System.out.println("Method Name " + md.getName()
                + " Return Type: " + md.getTypeAsString()
                + " Parameters: " + md.getParameters()
                + " Range: " + md.getRange());
      }
    };
    visitor.visit(compilationUnit, null);
  }
}
