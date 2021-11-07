package util;

public class StringUtil {

    private StringUtil() {

    }

    /**
     * ABCDE CDEFG
     * should return
     * ABCDEFG
     * @param startString
     * @param endString
     * @return
     */
    public static String mergeStrings(
            String startString,
            String endString
    ) {
        int max = 0;
        for (int i = 1; i <= endString.length(); i++) {
            String subStr = endString.substring(0, i);
            int posnFound = startString.lastIndexOf(subStr);
            if (posnFound >= 0
                    && startString.lastIndexOf(subStr) == startString.length() - subStr.length()) {
                max = i;
            }
        }
        return startString + endString.substring(max);
    }

}
