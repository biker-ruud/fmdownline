package nl.fm.downline.csv;

import nl.fm.downline.common.Utils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Ruud de Jong
 */
public class ParserTest {

    @Before
    public void setLogging() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    @Test
    public void testParseCsv() throws IOException {
        String csv = Utils.toString(this.getClass().getResourceAsStream("/15500252_6_2014.csv"));
        Parser parser = new Parser();
        parser.parse(csv);
    }
}
