import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;

public class SimpleExperimentMain {
  public static void main(String [] args) throws Exception {
    JavaParser javaParser = new JavaParser(new ParserConfiguration().setLanguageLevel(
            ParserConfiguration.LanguageLevel.JAVA_16));

    CompilationUnit compilationUnit = javaParser.parse(new File(args[0])).getResult().get();
    VoidVisitorAdapter visitor = new Visitor();
    visitor.visit(compilationUnit, null);
  }
}

class Visitor extends VoidVisitorAdapter<Void> {
  int tabCount = 0;

  private void print(String message) {
    for (int i = 0; i < tabCount; i++) {
      System.out.print("\t");
    }
    System.out.println(message);
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration d, Void arg) {
    print("start class " + d.getFullyQualifiedName());
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end class " + d.getFullyQualifiedName());
  }

  @Override
  public void visit(MethodDeclaration n, Void arg) {
    print("start method " + n.getName());
    tabCount++;

    // don't call super since we copied super's code and changed the order to have the parameters first
    n.getParameters().forEach(p -> p.accept(this, arg));
    n.getBody().ifPresent(l -> l.accept(this, arg));
    n.getType().accept(this, arg);
    n.getModifiers().forEach(p -> p.accept(this, arg));
    n.getName().accept(this, arg);
    n.getParameters().forEach(p -> p.accept(this, arg));
    n.getReceiverParameter().ifPresent(l -> l.accept(this, arg));
    n.getThrownExceptions().forEach(p -> p.accept(this, arg));
    n.getTypeParameters().forEach(p -> p.accept(this, arg));
    n.getAnnotations().forEach(p -> p.accept(this, arg));
    n.getComment().ifPresent(l -> l.accept(this, arg));

    tabCount--;
    print("end method " + n.getName());
  }

  @Override
  public void visit(WhileStmt d, Void arg) {
    print("start while " );
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end while ");
  }

  @Override
  public void visit(ForStmt d, Void arg) {
    print("start for ");
    tabCount++;

    // don't call super since I don't like the order they use
    // instead we do initialization first so we can get the variables before the body:
    d.getInitialization().forEach(p -> p.accept(this, arg));
    d.getBody().accept(this, arg);
    d.getCompare().ifPresent(l -> l.accept(this, arg));
    d.getUpdate().forEach(p -> p.accept(this, arg));
    d.getComment().ifPresent(l -> l.accept(this, arg));

    tabCount--;
    print("end for ");
  }

  @Override
  public void visit(IfStmt d, Void arg) {
    print("start if ");
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end if ");
  }

  @Override
  public void visit(Parameter d, Void arg) {
    print("start param " + d);
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end param " + d);
  }

  @Override
  public void visit(VariableDeclarationExpr d, Void arg) {
    print("start variable " + d);
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end variable " + d);
  }
}
