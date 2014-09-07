package nl.fm.downline;

import nl.fm.downline.csv.FmGroupMember;

/**
 * @author Ruud de Jong
 */
public interface MemberSelectionListener {

    void chosenMember(FmGroupMember member);

    FmGroupMember getChosenMember();
}
