package nl.fm.downline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import nl.fm.downline.common.Utils;
import nl.fm.downline.csv.FmGroupMember;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Ruud de Jong
 */
public class Member extends Activity {
    private static final String LOG_TAG = "Member";

    private DownlineApp app;
    private FmGroupMemberAdapter<FmGroupMember> fmGroupMemberAdapter;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this BundleType contains the data it most
     *                           recently supplied in onSaveInstanceState(BundleType). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "onCreate");
        this.app = DownlineApp.getInstance();

        setContentView(R.layout.member);
        initControls();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");

        Intent intent = getIntent();
        String memberNumber = intent.getStringExtra(DownlineApp.INTENT_NAME_MEMBER_NUMBER);
        Log.i(LOG_TAG, "Member.onResume(): " + memberNumber);
        refresh(memberNumber);
    }

    private void initControls() {
        ListView membersList = (ListView) findViewById(R.id.listMembers);
        fmGroupMemberAdapter = new FmGroupMemberAdapter<>(this, R.layout.listmember);
        membersList.setAdapter(fmGroupMemberAdapter);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void refresh(String memberNumber) {
        FmGroupMember fmDownlineTree = app.getFmDownline();
        FmGroupMember chosenMember = findMember(fmDownlineTree, memberNumber);
        render(chosenMember);
    }

    private FmGroupMember findMember(FmGroupMember fmDownlineTree, String memberNumber) {
        if (fmDownlineTree.getNumber().equals(memberNumber)) {
            return fmDownlineTree;
        }
        for (FmGroupMember downlineMember : fmDownlineTree.getDownline()) {
            FmGroupMember foundMember = findMember(downlineMember, memberNumber);
            if (foundMember != null) {
                return foundMember;
            }
        }
        return null;
    }

    private void render(FmGroupMember fmGroupMember) {
        if (fmGroupMember == null) {
            return;
        }
        Log.i(LOG_TAG, "fmGroupMember.getName() " + fmGroupMember.getName());
        TextView memberName = (TextView) findViewById(R.id.textMemberName);
        memberName.setText(fmGroupMember.getName());

        Log.i(LOG_TAG, "fmGroupMember.getLine() " + fmGroupMember.getLine());
        TextView memberLine = (TextView) findViewById(R.id.textMemberLine);
        memberLine.setText(String.valueOf(fmGroupMember.getLine()));

        Log.i(LOG_TAG, "fmGroupMember.getLevel() " + fmGroupMember.getLevel());
        TextView memberLevel = (TextView) findViewById(R.id.textMemberLevel);
        memberLevel.setText(String.valueOf(fmGroupMember.getLevel()));

        String personalPoints = Utils.formatGetal(fmGroupMember.getPersonalPoints());
        String groupPoints = Utils.formatGetal(fmGroupMember.getGroupPoints());
        String combinedPoints = personalPoints + " / " + groupPoints;
        Log.i(LOG_TAG, "fmGroupMember.getPersonalPoints() " + personalPoints);
        Log.i(LOG_TAG, "fmGroupMember.getGroupPoints() " + groupPoints);
        Log.i(LOG_TAG, "fmGroupMember combined points() " + combinedPoints);
        TextView memberPoints = (TextView) findViewById(R.id.textMemberPoints);
        memberPoints.setText(combinedPoints);

        ProgressBar levelProgress = (ProgressBar) findViewById(R.id.progressLevel);
        app.setLevelProgress(levelProgress, fmGroupMember);

        Log.i(LOG_TAG, "fmGroupMember.getEmailAdress() " + Utils.getEmailAddress(fmGroupMember.getAddress()));
        TextView memberEmailAddress = (TextView) findViewById(R.id.textMemberEmailAddress);
        memberEmailAddress.setText(Utils.getEmailAddress(fmGroupMember.getAddress()));

        Log.i(LOG_TAG, "fmGroupMember.getPhoneNumber() " + Utils.getPhoneNumber(fmGroupMember.getAddress()));
        TextView memberPhoneNumber = (TextView) findViewById(R.id.textMemberPhoneNumber);
        memberPhoneNumber.setText(Utils.getPhoneNumber(fmGroupMember.getAddress()));

        Collections.sort(fmGroupMember.getDownline(), new Comparator<FmGroupMember>() {
            @Override
            public int compare(FmGroupMember lhs, FmGroupMember rhs) {
                int lhsGroupPoints = (int) (lhs.getGroupPoints() * 100f);
                int rhsGroupPoints = (int) (rhs.getGroupPoints() * 100f);
                return rhsGroupPoints - lhsGroupPoints;
            }
        });
        renderDownlineMembers(fmGroupMember.getDownline());
    }

    private void renderDownlineMembers(List<FmGroupMember> memberList) {
        Log.i(LOG_TAG, "renderDownlineMembers() adding " + memberList.size() + " members to list.");
        fmGroupMemberAdapter.clear();
        fmGroupMemberAdapter.addAll(memberList);
    }

}
