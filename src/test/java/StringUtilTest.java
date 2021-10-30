import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilTest {

    @Test
    public void test() {
        assertEquals("https://josephmate.github.io/OdinCodeBrowser/jdk8/java/util/List.html#linenum111",
                StringUtil.mergeStrings("https://josephmate.github.io/OdinCodeBrowser/jdk8",
                        "/OdinCodeBrowser/jdk8/java/util/List.html#linenum111"));
    }

    @Test
    public void test2() {
        assertEquals("https://josephmate.github.io/OdinCodeBrowser/jdk8/com/sun/crypto/provider/PBKDF2HmacSHA1Factory.html",
                StringUtil.mergeStrings("https://josephmate.github.io/OdinCodeBrowser/jdk8/",
                        "/OdinCodeBrowser/jdk8/com/sun/crypto/provider/PBKDF2HmacSHA1Factory.html"));
    }

}