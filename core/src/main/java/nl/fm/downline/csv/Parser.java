package nl.fm.downline.csv;

import nl.fm.downline.common.Retour;
import nl.fm.downline.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Ruud de Jong
 */
public final class Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);
    private static final String HEADER = "Lijn;Nummer;Sponsor;Distributeur;Adres;Persoonlijke punten;Groepspunten;Level;Verdiensten;entry_date;inactive_months;";
    private static final int HEADER_COUNT = Utils.split(HEADER, ";").length;
    private static final String NO_EARNINGS = "--";

    public Retour<FmGroupMember, String> parse(String csv) {
        if (csv == null || csv.length() < HEADER.length()) {
            LOGGER.warn("CSV invalid.");
            return Retour.createFaultRetour("CSV invalid.");
        }
        BufferedReader reader = null;
        FmGroupMember rootMember = null;
        try {
            reader = new BufferedReader(new StringReader(csv));
            // Last update line
            reader.readLine();
            String headerLine = reader.readLine();
            if (HEADER.equals(headerLine)) {
                // process content
                FmGroupMember currentMember = null;
                String memberLine = reader.readLine();
                while (memberLine != null) {
                    FmGroupMember readMember  = readMember(memberLine);
                    if (readMember == null) {
                        return Retour.createFaultRetour("Could not read CSV.");
                    }
                    if (currentMember == null) {
                        rootMember = readMember;
                    } else {
                        FmGroupMember parent = findParent(currentMember, readMember);
                        parent.addDownlineMember(readMember);
                        readMember.setUpline(parent);
                    }
                    currentMember = readMember;
                    memberLine = reader.readLine();
                }
            } else {
                LOGGER.warn("CSV invalid, header not found");
                return Retour.createFaultRetour("CSV invalid, header not found");
            }
        } catch (IOException e) {
            LOGGER.warn("CSV invalid.");
            return Retour.createFaultRetour("CSV invalid.");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Fail silently
                }
            }
        }
        if (rootMember == null) {
            return Retour.createFaultRetour("No members found");
        } else {
            return Retour.createSuccessRetour(rootMember);
        }
    }

    private FmGroupMember readMember(String memberLine) {
        String[] memberElements = Utils.split(memberLine, ";");
        if (memberElements.length != HEADER_COUNT || memberElements.length < 11) {
            LOGGER.warn("Member line invalid: " + memberLine);
            return null;
        }
        FmGroupMember member = new FmGroupMember();
        try {
            int line = Integer.parseInt(memberElements[0]);
            member.setLine(line);
        } catch (NumberFormatException e) {
            LOGGER.warn("'Lijn' is not an integer: " + memberElements[0]);
            return null;
        }
        member.setNumber(memberElements[1]);
        member.setName(memberElements[3]);
        LOGGER.info("Reading: " + member.getName());
        member.setAddress(memberElements[4]);
        try {
            member.setPersonalPoints(Utils.parseGetal(memberElements[5]));
        } catch (ParseException e) {
            LOGGER.warn("'Persoonlijke punten' is not a number: " + memberElements[5]);
            return null;
        }
        try {
            member.setGroupPoints(Utils.parseGetal(memberElements[6]));
        } catch (ParseException e) {
            LOGGER.warn("'Groepspunten' is not a number: " + memberElements[6]);
            return null;
        }
        try {
            int level = Integer.parseInt(memberElements[7]);
            member.setLevel(level);
        } catch (NumberFormatException e) {
            LOGGER.warn("'Level' is not an integer: " + memberElements[7]);
            return null;
        }
        try {
            if (!NO_EARNINGS.equals(memberElements[8])) {
                member.setEarnings(Utils.parseGetal(memberElements[8]));
            }
        } catch (ParseException e) {
            LOGGER.warn("'Verdiensten' is not a number: " + memberElements[8]);
            return null;
        }
        try {
            Calendar entryDate = Calendar.getInstance();
            entryDate.setTime(new SimpleDateFormat(FmGroupMember.DATE_FORMAT).parse(memberElements[9]));
            member.setEntryDate(entryDate);
        } catch (ParseException e) {
            LOGGER.warn("'entry_date' is not a date: " + memberElements[9]);
            return null;
        }
        try {
            int inactiveMonths = Integer.parseInt(memberElements[10]);
            member.setInactiveMonths(inactiveMonths);
        } catch (NumberFormatException e) {
            LOGGER.warn("'inactive_months' is not an integer: " + memberElements[10]);
            return null;
        }
        return member;
    }

    private FmGroupMember findParent(FmGroupMember last, FmGroupMember child) {
        LOGGER.info("Finding parent of " + child.getName());
        FmGroupMember current = last;
        boolean isParent = isParent(current, child);
        LOGGER.info("Evaluating parent " + current.getName());
        while (!isParent) {
            current = current.getUpline();
            isParent = isParent(current, child);
        }
        LOGGER.info("Found parent " + current.getName());
        return current;
    }

    private boolean isParent(FmGroupMember current, FmGroupMember child) {
        return (current.getLine()+1 == child.getLine());
    }
}
