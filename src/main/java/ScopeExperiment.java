public class ScopeExperiment {
    public String field;
    public static String FIELD;

    public void method(String param) {
        int variable = 0;
        for (int another = 0; another < 10; another++) {
            if (another == 10) {
                int somethingElse = 1;
                // Cannot redefine variable:
                // int variable = 3;
                while (true) {
                    int oneMoreVar = 5;
                }
            }
            if (another == 5) {
                int somethingElse = 2;
                // Cannot change the type
                // String somethingElse = 2;
            }
        }
    }
}
