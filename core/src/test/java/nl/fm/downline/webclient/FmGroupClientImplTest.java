package nl.fm.downline.webclient;

import nl.fm.downline.common.Retour;
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
        Retour<String, String> response = client.start(username, password);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void ongeldigeLogin() throws IOException {
        FmGroupClientImpl client = new FmGroupClientImpl();
        Retour<String, String> response = client.start("dummy", "dummy");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isFault());
    }
}
