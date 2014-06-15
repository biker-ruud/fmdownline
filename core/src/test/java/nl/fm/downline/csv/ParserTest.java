package nl.fm.downline.csv;

import nl.fm.downline.common.Retour;
import nl.fm.downline.common.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;

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
        Retour<FmGroupMember, String> parseResult = parser.parse(csv);

        Assert.assertTrue(parseResult.isSuccess());
        FmGroupMember rootMember = parseResult.getValue();
        Assert.assertEquals(0, rootMember.getLine());
        Assert.assertEquals("XN500252/0", rootMember.getNumber());
        Assert.assertEquals("de Visser Angelique", rootMember.getName());
        Assert.assertEquals("Jan de Vriesstraat 9 2496RB Den Haag   0031653516933 angelique.devisser@gmail.com", rootMember.getAddress());
        Assert.assertEquals(314.90, rootMember.getPersonalPoints(), 0.01);
        Assert.assertEquals(515.60, rootMember.getGroupPoints(), 0.01);
        Assert.assertEquals(3, rootMember.getLevel());
        Assert.assertEquals(3.87, rootMember.getEarnings(), 0.01);
        Assert.assertEquals(2014, rootMember.getEntryDate().get(Calendar.YEAR));
        Assert.assertEquals(Calendar.MARCH, rootMember.getEntryDate().get(Calendar.MONTH));
        Assert.assertEquals(10, rootMember.getEntryDate().get(Calendar.DATE));
        Assert.assertEquals(0, rootMember.getInactiveMonths());

        Assert.assertEquals(3, rootMember.getDownline().size());
        FmGroupMember firstDownline = rootMember.getDownline().get(0);
        Assert.assertEquals("XN532486/0", firstDownline.getNumber());
        Assert.assertEquals("PIENEMAN Jacqueline", firstDownline.getName());
        Assert.assertEquals("LUSTHOFSTRAAT 36 3135CX VLAARDINGEN   0031624766534 Jasama67@hotmail.com", firstDownline.getAddress());
        Assert.assertEquals(0.0, firstDownline.getPersonalPoints(), 0.01);
        Assert.assertEquals(0.0, firstDownline.getGroupPoints(), 0.01);
        Assert.assertEquals(0, firstDownline.getLevel());
        Assert.assertEquals(0.0, firstDownline.getEarnings(), 0.01);
        Assert.assertEquals(2014, firstDownline.getEntryDate().get(Calendar.YEAR));
        Assert.assertEquals(Calendar.MAY, firstDownline.getEntryDate().get(Calendar.MONTH));
        Assert.assertEquals(16, firstDownline.getEntryDate().get(Calendar.DATE));
        Assert.assertEquals(1, firstDownline.getInactiveMonths());
        Assert.assertNotNull(firstDownline.getUpline());
        Assert.assertEquals(rootMember.getNumber(), firstDownline.getUpline().getNumber());

        FmGroupMember secondDownline = rootMember.getDownline().get(1);
        Assert.assertEquals("XN519771/0", secondDownline.getNumber());
        Assert.assertEquals("van Riet Alain", secondDownline.getName());
        Assert.assertEquals("kikkerveen 341 3205XC Spijkenisse 0031653821471  0031653821471 alain.v.r@hotmail.com", secondDownline.getAddress());
        Assert.assertEquals(200.7, secondDownline.getPersonalPoints(), 0.01);
        Assert.assertEquals(200.7, secondDownline.getGroupPoints(), 0.01);
        Assert.assertEquals(0, secondDownline.getLevel());
        Assert.assertEquals(0.0, secondDownline.getEarnings(), 0.01);
        Assert.assertEquals(2014, secondDownline.getEntryDate().get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JUNE, secondDownline.getEntryDate().get(Calendar.MONTH));
        Assert.assertEquals(3, secondDownline.getEntryDate().get(Calendar.DATE));
        Assert.assertEquals(0, secondDownline.getInactiveMonths());
        Assert.assertNotNull(secondDownline.getUpline());
        Assert.assertEquals(rootMember.getNumber(), secondDownline.getUpline().getNumber());

        FmGroupMember thirdDownline = rootMember.getDownline().get(2);
        Assert.assertEquals("XN509518/0", thirdDownline.getNumber());
        Assert.assertEquals("Pijpers Patty", thirdDownline.getName());
        Assert.assertEquals("vederkruidvaart 11 2724VM zoetermeer 0031637655660  0031637655660 pattypijpers@yahoo.com", thirdDownline.getAddress());
        Assert.assertEquals(0.0, thirdDownline.getPersonalPoints(), 0.01);
        Assert.assertEquals(0.0, thirdDownline.getGroupPoints(), 0.01);
        Assert.assertEquals(0, thirdDownline.getLevel());
        Assert.assertEquals(0.0, thirdDownline.getEarnings(), 0.01);
        Assert.assertEquals(2014, thirdDownline.getEntryDate().get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JUNE, thirdDownline.getEntryDate().get(Calendar.MONTH));
        Assert.assertEquals(10, thirdDownline.getEntryDate().get(Calendar.DATE));
        Assert.assertEquals(1, thirdDownline.getInactiveMonths());
        Assert.assertNotNull(thirdDownline.getUpline());
        Assert.assertEquals(rootMember.getNumber(), thirdDownline.getUpline().getNumber());
    }
}
