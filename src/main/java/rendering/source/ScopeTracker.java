package rendering.source;

import java.util.*;

public class ScopeTracker {

    private final Map<String, Stack<String>> variableToShortTypes = new HashMap<>();
    private final Map<String, Stack<Integer>> variableToLines = new HashMap<>();
    private final Stack<List<String>> scopeToVariableNames = new Stack<>();

    public void startScope() {
        scopeToVariableNames.add(new ArrayList<>());
    }

    public void addVariable(
            String variableName,
            String variableType,
            int lineNumber) {
        if (scopeToVariableNames.isEmpty()) {
            return;
        }
        List<String> variableNames = scopeToVariableNames.peek();
        variableNames.add(variableName);

        Stack<String> types = variableToShortTypes.get(variableName);
        if (types == null) {
            types = new Stack<>();
            variableToShortTypes.put(variableName, types);
        }
        types.add(variableType);

        Stack<Integer> lines = variableToLines.get(variableName);
        if (lines == null) {
            lines = new Stack<>();
            variableToLines.put(variableName, lines);
        }
        lines.add(lineNumber);
    }

    /**
     * @param variableName the name of the variable to search in the scope for.
     * @return the short type that still needs to joined against the imports to figure out the full
     *         class name (fully qualified name).
     */
    public String getVariableShortType(String variableName) {
          Stack<String> types = variableToShortTypes.get(variableName);
          if (types == null) {
              return null;
          }
          if (types.isEmpty()) {
              return null;
          }
          return types.peek();
      }

    public Integer getVariableLine(String variableName) {
        Stack<Integer> lines = variableToLines.get(variableName);
        if (lines == null) {
            return null;
        }
        if (lines.isEmpty()) {
            return null;
        }
        return lines.peek();
    }

    public void endScope() {
        if (scopeToVariableNames.isEmpty()) {
            return;
        }

        List<String> variableNamesToRemove = scopeToVariableNames.pop();
        for (String variableNameToRemove : variableNamesToRemove) {
            Stack<String> types = variableToShortTypes.get(variableNameToRemove);
            if (types != null && !types.isEmpty()) {
                types.pop();
            }
            Stack<Integer> lines = variableToLines.get(variableNameToRemove);
            if (types != null && !types.isEmpty()) {
                types.pop();
            }
        }
    }
}
