package nl.fm.downline.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ruud de Jong
 */
public final class UtilsTest {

    @Test
    public void testJoin() {
        String[] array = new String[]{"a", "bb", "ccc"};
        String separator = "--";
        String result = Utils.join(array, separator);
        Assert.assertNotNull(result);
        Assert.assertEquals("a--bb--ccc", result);
    }

    @Test
    public void testFormatGetal() {
        float getal = 314.23001f;

        String result = Utils.formatGetal(getal);

        Assert.assertNotNull(result);
        Assert.assertEquals("314,23", result);
    }

}
