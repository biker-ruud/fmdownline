package nl.fm.downline.csv;

import nl.fm.downline.common.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ruud de Jong
 */
public final class FmGroupMember {
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private int line;
    private String number;
    private String name;
    private String address;
    private float personalPoints;
    private float groupPoints;
    private int level;
    private float earnings;
    private Date entryDate;
    private int inactiveMonths;
    private List<FmGroupMember> memberList = new ArrayList<FmGroupMember>();

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getPersonalPoints() {
        return personalPoints;
    }

    public void setPersonalPoints(float personalPoints) {
        this.personalPoints = personalPoints;
    }

    public float getGroupPoints() {
        return groupPoints;
    }

    public void setGroupPoints(float groupPoints) {
        this.groupPoints = groupPoints;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getEarnings() {
        return earnings;
    }

    public void setEarnings(float earnings) {
        this.earnings = earnings;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public int getInactiveMonths() {
        return inactiveMonths;
    }

    public void setInactiveMonths(int inactiveMonths) {
        this.inactiveMonths = inactiveMonths;
    }

    public List<FmGroupMember> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<FmGroupMember> memberList) {
        this.memberList = memberList;
    }

    public void addMember(FmGroupMember member) {
        this.memberList.add(member);
    }

    @Override
    public String toString() {
        List<String> elements = new ArrayList<String>();
        elements.add(String.valueOf(line));
        elements.add(number);
        elements.add(name);
        elements.add(address);
        elements.add(String.valueOf(personalPoints));
        elements.add(String.valueOf(groupPoints));
        elements.add(String.valueOf(level));
        elements.add(String.valueOf(earnings));
        String formattedEntryDate = new SimpleDateFormat(DATE_FORMAT).format(entryDate);
        elements.add(formattedEntryDate);
        elements.add(String.valueOf(inactiveMonths));
        return Utils.join(elements.toArray(new String[elements.size()]), ";");
    }
}
