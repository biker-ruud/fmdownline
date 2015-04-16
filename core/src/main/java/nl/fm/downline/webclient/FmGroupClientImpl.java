package nl.fm.downline.webclient;

import nl.fm.downline.common.Retour;
import nl.fm.downline.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * @author Ruud de Jong
 */
public final class FmGroupClientImpl implements FmGroupClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmGroupClientImpl.class);
    private static final String HOST = "nl.fmworld.com";
    private static final String LOGIN = "/nl/login/";
    private static final String MIJN_OVERZICHT = "/nl/distributors-zone/mijn-structuur-53/nieuwe-stamboom-34/";
    private static final String CSV_DOWNLOAD_LINK_START = "http://mlm.fmgroup.pl";
    private String username;
    private String password;

    @Override
    public Retour<String, String> start(String username, String password) {
        this.username = username;
        this.password = password;

        HttpsSession session = null;
        try {
            session = new HttpsSession(new URL(HttpsSession.UNSECURE_PROTOCOL + HOST));
            InputStream inputStream = session.connect(new URL(HttpsSession.UNSECURE_PROTOCOL + HOST + LOGIN));
            if (inputStream != null) {
                if (!login(session, inputStream)) {
                    // Login gefaald.
                    return Retour.createFaultRetour("login gefaald");
                }
                String csvDownloadLink = vindCsvDownloadLink(session);
                if (csvDownloadLink == null) {
                    return Retour.createFaultRetour("CSV niet gevonden");
                }
                String csv = downloadCsv(csvDownloadLink);
                return Retour.createSuccessRetour(csv);
            }
        } catch (MalformedURLException e) {
            LOGGER.error("URL invalid: ", e);
        } catch (ClassCastException e) {
            LOGGER.error("Class cast: ", e);
        } catch (SocketTimeoutException e) {
            LOGGER.warn("Timeout: ", e);
        } catch (IOException e) {
            LOGGER.error("IO error: ", e);
        } catch (URISyntaxException e) {
            LOGGER.error("URL invalid: ", e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
        return Retour.createFaultRetour("niet gevonden");
    }

    private boolean login(HttpsSession session, InputStream inputStream) throws IOException, URISyntaxException {
        LOGGER.info("***************************************");
        LOGGER.info("**        INLOGGEN                   **");
        LOGGER.info("***************************************");
        String body = Utils.toString(inputStream);
        LOGGER.debug("Response body size: " + body.length() + " bytes.");
        //LOGGER.trace("Body: " + body);
        inputStream.close();
        List<FormParserUtil.Form> formlist = FormParserUtil.parseForm(body);

        if (formlist == null || formlist.isEmpty()) {
            LOGGER.info("Kan het inlogscherm niet vinden");
            LOGGER.info(body);
            return false;
        }
        for (FormParserUtil.Form form : formlist) {
            LOGGER.info("Form:");
            LOGGER.info("Form action: " + form.action);
            LOGGER.info("Form inputs: " + form.inputList.size());
            FormParserUtil.Input loginInput = FormParserUtil.getLoginInput(form);
            FormParserUtil.Input passwordInput = FormParserUtil.getPasswordInput(form);
            if (loginInput != null && passwordInput != null) {
                loginInput.value = username;
                passwordInput.value = password;
                return FormParserUtil.postForm(session, form);
            }
        }
        LOGGER.info("Kan login en password velden NIET vinden op het login scherm.");
        return false;
    }

    private String vindCsvDownloadLink(HttpsSession session) throws IOException {
        LOGGER.info("***************************************");
        LOGGER.info("**        CSV VINDEN                 **");
        LOGGER.info("***************************************");
        InputStream inputStream = session.get(new URL(HttpsSession.UNSECURE_PROTOCOL + HOST + MIJN_OVERZICHT));
        String body = Utils.toString(inputStream);
        LOGGER.info("Response body size: " + body.length() + " bytes.");
        LOGGER.trace("Body: " + body);
        inputStream.close();

        String partialDownloadLink = Utils.substringBetween(body, CSV_DOWNLOAD_LINK_START, "\"");
        if (partialDownloadLink != null) {
            String downloadLink = CSV_DOWNLOAD_LINK_START + partialDownloadLink;
            LOGGER.info("Gevonden CSV downloadlink: " + downloadLink);
            return downloadLink;
        } else {
            return null;
        }
    }

    private String downloadCsv(String csvDownloadLink) {
        LOGGER.info("***************************************");
        LOGGER.info("**        CSV DOWNLOADEN             **");
        LOGGER.info("***************************************");
        HttpsSession session = null;
        try {
            session = new HttpsSession(new URL(csvDownloadLink));
            InputStream inputStream = session.connect(new URL(csvDownloadLink));
            String body = Utils.toString(inputStream);
            LOGGER.info("Response body size: " + body.length() + " bytes.");
            LOGGER.info("Body: " + body);
            inputStream.close();
            return body;
        } catch (MalformedURLException e) {
            LOGGER.error("URL invalid: ", e);
        } catch (IOException e) {
            LOGGER.error("IO error: ", e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
        return null;
    }

}
