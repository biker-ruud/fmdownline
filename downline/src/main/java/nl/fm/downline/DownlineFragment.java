package nl.fm.downline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import nl.fm.downline.common.Utils;
import nl.fm.downline.csv.FmGroupMember;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Ruud de Jong
 */
public class DownlineFragment extends RoboFragment implements RefreshListener {

    private static final String LOG_TAG = "DownlineFragment";

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

    @InjectView(R.id.textMemberEarnings)
    private TextView memberEarnings;

    @InjectView(R.id.textLatestUpdate)
    private TextView latestUpdateText;

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
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this BundleType contains the data it most
     *                           recently supplied in onSaveInstanceState(BundleType). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        DownlineApp.getInstance().registerListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(LOG_TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_downline, container, false);
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

        if (properSettings()) {
            refresh();
        }
    }

    private boolean properSettings() {
        return (DownlineApp.getUsername().length() > 0 && DownlineApp.getPassword().length() > 0);
    }

    private void initControls() {
        Context ctx = getActivity().getApplicationContext();
        membersList.addHeaderView(new View(ctx));
        membersList.addFooterView(new View(ctx));
        fmGroupMemberAdapter = new FmGroupMemberAdapter<>(ctx, R.layout.list_item_card);
        membersList.setAdapter(fmGroupMemberAdapter);

        membersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object chosenItem = parent.getItemAtPosition(position);
                if (chosenItem instanceof FmGroupMember) {
                    FmGroupMember chosenMember = (FmGroupMember) chosenItem;
                    Log.i(LOG_TAG, "Chosen member " + chosenMember.getName());
                    memberSelectionListener.chosenMember(chosenMember);
                }
            }
        });
    }

    private void updateLatestUpdate() {
        long latestUpdate = DownlineApp.getLatestUpdate();
        String latestUpdatePretext = getString(R.string.latestUpdate);
        if (latestUpdate == 0) {
            latestUpdateText.setText(latestUpdatePretext + " " + getString(R.string.latestUpdateNever) + ".");
        } else {
            Date latestUpdateDate = new Date();
            latestUpdateDate.setTime(latestUpdate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            latestUpdateText.setText(latestUpdatePretext + " " + sdf.format(latestUpdateDate));
        }
    }

    private void render(FmGroupMember fmGroupMember) {
        if (fmGroupMember == null) {
            return;
        }
        Log.i(LOG_TAG, "fmGroupMember.getName() " + fmGroupMember.getName());
        memberName.setText(fmGroupMember.getName());

        Log.i(LOG_TAG, "fmGroupMember.getLevel() " + fmGroupMember.getLevel());
        memberLevel.setText(String.valueOf(fmGroupMember.getLevel()));

        String personalPoints = Utils.formatGetal(fmGroupMember.getPersonalPoints());
        String groupPoints = Utils.formatGetal(fmGroupMember.getGroupPoints());
        String combinedPoints = personalPoints + " / " + groupPoints;
        Log.i(LOG_TAG, "fmGroupMember.getPersonalPoints() " + personalPoints);
        Log.i(LOG_TAG, "fmGroupMember.getGroupPoints() " + groupPoints);
        Log.i(LOG_TAG, "fmGroupMember combined points() " + combinedPoints);
        memberPoints.setText(combinedPoints);

        DownlineApp.getInstance().setLevelProgress(levelProgress, fmGroupMember);

        Log.i(LOG_TAG, "fmGroupMember.getEarnings() " + Utils.formatGetal(fmGroupMember.getEarnings()));
        memberEarnings.setText("€ " + Utils.formatGetal(fmGroupMember.getEarnings()));

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


    @Override
    public void refresh() {
        updateLatestUpdate();
        FmGroupMember fmDownlineTree = DownlineApp.getInstance().getFmDownline();
        render(fmDownlineTree);
    }
}
