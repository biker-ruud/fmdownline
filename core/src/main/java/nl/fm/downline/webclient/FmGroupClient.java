package nl.fm.downline.webclient;

import nl.fm.downline.common.Retour;

/**
 * @author Ruud de Jong
 */
public interface FmGroupClient {

    /**
     * Bezoek de website en vind de gegevens.
     *
     * @param username de username.
     * @param password het wachtwoord.
     * @return de gegevens of een foutmelding.
     */
    Retour<String, String> start(String username, String password);

}
