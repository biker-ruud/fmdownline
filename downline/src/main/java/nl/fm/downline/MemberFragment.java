package nl.fm.downline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import nl.fm.downline.common.Utils;
import nl.fm.downline.csv.FmGroupMember;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Ruud de Jong
 */
public class MemberFragment extends RoboFragment {
    private static final String LOG_TAG = "MemberFragment";

    private DownlineApp app;
    private FmGroupMemberAdapter<FmGroupMember> fmGroupMemberAdapter;
    private MemberSelectionListener memberSelectionListener;

    @InjectView(R.id.listMembers)
    private ListView membersList;

    @InjectView(R.id.textMemberName)
    private TextView memberName;

    @InjectView(R.id.textMemberLevel)
    private TextView memberLevel;

    @InjectView(R.id.textMemberPoints)
    private TextView memberPoints;

    @InjectView(R.id.progressLevel)
    private ProgressBar levelProgress;

    @InjectView(R.id.textMemberLine)
    private TextView memberLine;

    @InjectView(R.id.textMemberEmailAddress)
    private TextView memberEmailAddress;

    @InjectView(R.id.textMemberPhoneNumber)
    private TextView memberPhoneNumber;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MemberSelectionListener) {
            memberSelectionListener = (MemberSelectionListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement MemberSelectionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        memberSelectionListener = null;
    }

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(LOG_TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControls();
    }


        @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");

        FmGroupMember chosenMember = memberSelectionListener.getChosenMember();
        String memberNumber = chosenMember.getNumber();
        Log.i(LOG_TAG, "Member.onResume(): " + memberNumber);
        refresh(memberNumber);
    }

    private void initControls() {
        Context ctx = getActivity().getApplicationContext();
        membersList.addHeaderView(new View(ctx));
        membersList.addFooterView(new View(ctx));
        fmGroupMemberAdapter = new FmGroupMemberAdapter<>(ctx, R.layout.list_item_card);
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
        memberName.setText(fmGroupMember.getName());

        Log.i(LOG_TAG, "fmGroupMember.getLine() " + fmGroupMember.getLine());
        memberLine.setText(String.valueOf(fmGroupMember.getLine()));

        Log.i(LOG_TAG, "fmGroupMember.getLevel() " + fmGroupMember.getLevel());
        memberLevel.setText(String.valueOf(fmGroupMember.getLevel()));

        String personalPoints = Utils.formatGetal(fmGroupMember.getPersonalPoints());
        String groupPoints = Utils.formatGetal(fmGroupMember.getGroupPoints());
        String combinedPoints = personalPoints + " / " + groupPoints;
        Log.i(LOG_TAG, "fmGroupMember.getPersonalPoints() " + personalPoints);
        Log.i(LOG_TAG, "fmGroupMember.getGroupPoints() " + groupPoints);
        Log.i(LOG_TAG, "fmGroupMember combined points() " + combinedPoints);
        memberPoints.setText(combinedPoints);

        app.setLevelProgress(levelProgress, fmGroupMember);

        Log.i(LOG_TAG, "fmGroupMember.getEmailAdress() " + Utils.getEmailAddress(fmGroupMember.getAddress()));
        memberEmailAddress.setText(Utils.getEmailAddress(fmGroupMember.getAddress()));

        Log.i(LOG_TAG, "fmGroupMember.getPhoneNumber() " + Utils.getPhoneNumber(fmGroupMember.getAddress()));
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
