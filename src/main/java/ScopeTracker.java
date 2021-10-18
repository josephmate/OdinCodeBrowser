import java.util.*;

public class ScopeTracker {

    private final Map<String, Stack<String>> variableToTypes = new HashMap<>();
    private final Stack<List<String>> scopeToVariableNames = new Stack<>();

    public void startScope() {
        scopeToVariableNames.add(new ArrayList<>());
    }

    public void addVariable(
            String variableName,
            String variableType) {
        if (scopeToVariableNames.isEmpty()) {
            return;
        }
        List<String> variableNames = scopeToVariableNames.peek();
        variableNames.add(variableName);

        Stack<String> types = variableToTypes.get(variableName);
        if (types == null) {
            types = new Stack<>();
            variableToTypes.put(variableName, types);
        }
        types.add(variableType);
    }

    public String getVariableType(String variableName) {
        Stack<String> types = variableToTypes.get(variableName);
        if (types == null) {
            return null;
        }
        if (types.isEmpty()) {
            return null;
        }
        return types.peek();
    }

    public void endScope() {
        if (scopeToVariableNames.isEmpty()) {
            return;
        }

        List<String> variableNamesToRemove = scopeToVariableNames.pop();
        for (String variableNameToRemove : variableNamesToRemove) {
            Stack<String> types = variableToTypes.get(variableNameToRemove);
            if (types != null && !types.isEmpty()) {
                types.pop();
            }
        }
    }
}
