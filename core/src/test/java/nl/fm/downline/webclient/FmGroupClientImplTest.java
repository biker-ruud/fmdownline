package nl.fm.downline.webclient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Ruud de Jong
 */
public class FmGroupClientImplTest {

    @Before
    public void setLogging() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    @Test
    public void connectTest() throws IOException {
        Properties authenticationProps = new Properties();
        authenticationProps.load(this.getClass().getResourceAsStream("/FmGroupClientImplTest.properties"));
        String username = authenticationProps.getProperty("username");
        String password = authenticationProps.getProperty("password");

        FmGroupClientImpl client = new FmGroupClientImpl();
        String response = client.start(username, password);
        Assert.assertNotNull(response);
    }

    @Test
    public void ongeldigeLogin() throws IOException {
        FmGroupClientImpl haring = new FmGroupClientImpl();
        String response = haring.start("dummy", "dummy");
        Assert.assertNull(response);
    }
}
