public class VariableExperiment {
    public static final String HELLO = "Hello!";
    public String hello = "hello";

    public void method(String param) {
        System.out.println(VariableExperiment.HELLO);
        VariableExperiment v = new VariableExperiment();
        System.out.println(v.hello);
        System.out.println(this.hello);
        System.out.println(param);
    }

}
