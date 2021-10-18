public class VariableExperiment {
    public static final String HELLO = "Hello!";
    public String hello = "hello";

    public void method() {
        System.out.println(VariableExperiment.HELLO);
        VariableExperiment v = new VariableExperiment();
        System.out.println(v.hello);
    }

}
