import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;

public class ExperimentalMain {
  public static void main(String [] args) throws Exception {
    JavaParser javaParser = new JavaParser(new ParserConfiguration().setLanguageLevel(
            ParserConfiguration.LanguageLevel.JAVA_16));

    CompilationUnit compilationUnit = javaParser.parse(new File(args[0])).getResult().get();
    VoidVisitorAdapter visitor = new Visitor();
    visitor.visit(compilationUnit, null);

    "some test with str"
            .substring(0,2)
            .substring(0,1);

    (new Builder("hello"))
            .blah1(123)
            .someField
            .blah2()
            .arrayField[1]
            .blah3();
  }
}

enum Test {
  HELLO,
  WORLD
}

class Builder {

  Builder(String s) {

  }

  Test enumField = Test.HELLO;

  Builder multiA = this, multiB = this;

  int primitiveField = 10;

  int[] primitiveArrayField = new int[]{1, 2, 3};

  int primitiveArrayFieldWeird[] = new int[]{1, 2, 3};

  Builder someField = this;

  Builder[] arrayField = new Builder[]{
          this,
          this
  };

  Builder blah1(int someArg) {
    return this;
  }
  Builder blah2() {
    return this;
  }
  Builder blah3() {
    return this;
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
    print("start MethodDeclaration " + n.getName());
    tabCount++;
    print("MethodDeclaration.getTypeAsString() " + n.getTypeAsString());
    n.getParameters().forEach(p -> print("param: " + p.getTypeAsString()));

    // don't call super since we copied super's code and changed the order to have the parameters first
    n.getParameters().forEach(p -> p.accept(this, arg));
    n.getBody().ifPresent(l -> l.accept(this, arg));
    n.getType().accept(this, arg);
    n.getModifiers().forEach(p -> p.accept(this, arg));
    n.getName().accept(this, arg);
    n.getReceiverParameter().ifPresent(l -> l.accept(this, arg));
    n.getThrownExceptions().forEach(p -> p.accept(this, arg));
    n.getTypeParameters().forEach(p -> p.accept(this, arg));
    n.getAnnotations().forEach(p -> p.accept(this, arg));
    n.getComment().ifPresent(l -> l.accept(this, arg));

    tabCount--;
    print("end MethodDeclaration " + n.getName());
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
    print("start param " + d + " " + d.getRange());
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end param " + d + " " + d.getRange());
  }

  @Override
  public void visit(VariableDeclarationExpr d, Void arg) {
    print("start variable " + d);
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end variable " + d);
  }


  @Override
  public void visit(ClassOrInterfaceType d, Void arg) {
    print("start classType " + d);
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end classType " + d);
  }

  @Override
  public void visit(NameExpr d, Void arg) {
    print("start nameExpr " + d);
    tabCount++;
    super.visit(d, arg);
    tabCount--;
    print("end nameExpr " + d);
  }

  @Override
  public void visit(MethodCallExpr n, Void arg) {
    print("start MethodCallExpr " + n);
    tabCount++;
    print("# args: " + n.getArguments().size());
    for (int i = 0; i < n.getArguments().size(); i++) {
      Expression expression =  n.getArguments().get(i);
      print("arg[" + i + "]= (# nodes=" + expression.getChildNodes().size() + ")" + expression);
      tabCount++;
      if (expression.getChildNodes().size() == 1) {
        print("type: " + expression.getChildNodes().get(0).getClass().getSimpleName());
      }
      tabCount--;
    }
    print("===========================");
    super.visit(n, arg);
    tabCount--;
    print("end MethodCallExpr " + n);
  }

  @Override
  public void visit(ObjectCreationExpr oce, Void arg) {
    print("start ObjectCreationExpr " + oce);
    tabCount++;
    super.visit(oce, arg);
    tabCount--;
    print("end ObjectCreationExpr " + oce);
  }
  @Override
  public void visit(ArrayAccessExpr oce, Void arg) {
    print("start ArrayAccessExpr " + oce);
    tabCount++;
    super.visit(oce, arg);
    tabCount--;
    print("end ArrayAccessExpr " + oce);
  }

  public void visit(FieldDeclaration fieldDeclaration, Void arg) {
    print("start FieldDeclaration " + fieldDeclaration);
    tabCount++;
    super.visit(fieldDeclaration, arg);
    tabCount--;
    print("end FieldDeclaration " + fieldDeclaration);
  }

  public void visit(VariableDeclarator vd, Void arg) {
    print("start VariableDeclarator " + vd);
    tabCount++;
    print("start VariableDeclarator.getType().asString() " + vd.getType().asString());
    super.visit(vd, arg);
    tabCount--;
    print("end VariableDeclarator " + vd);
  }

}