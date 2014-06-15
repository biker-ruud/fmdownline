package nl.fm.downline.csv;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * @author Ruud de Jong
 */
public class FmGroupMemberTest {

    @Test
    public void testToStringEmptyMember() {
        FmGroupMember member = new FmGroupMember();
        Assert.assertNotNull(member.toString());
    }

    @Test
    public void testToString() {
        FmGroupMember member = new FmGroupMember();
        member.setLine(0);
        member.setNumber("XN500252/0");
        member.setName("de Visser Angelique");
        member.setAddress("Jan de Vriesstraat 9 2496RB Den Haag   0031653516933 angelique.devisser@gmail.com");
        member.setPersonalPoints(314.90f);
        member.setGroupPoints(515.60f);
        member.setLevel(3);
        member.setEarnings(3.87f);
        Calendar entryDate = Calendar.getInstance();
        entryDate.clear();
        entryDate.set(2014, Calendar.MARCH, 10);
        member.setEntryDate(entryDate);
        member.setInactiveMonths(0);

        Assert.assertNotNull(member.toString());
    }
}
