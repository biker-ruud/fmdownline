package nl.fm.downline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import nl.fm.downline.common.LevelRanges;
import nl.fm.downline.common.Utils;
import nl.fm.downline.csv.FmGroupMember;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Ruud de Jong
 */
public class Downline extends Activity {

    private static final String LOG_TAG = "Downline";

    private boolean firstTime = true;
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

        setContentView(R.layout.main);
        initControls();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");

        if (properSettings()) {
            updateLatestUpdate();
            FmGroupMember fmDownlineTree = app.getFmDownline();
            render(fmDownlineTree);
        } else if (this.firstTime) {
            gotoSettings();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.icon);
            builder.setTitle(getString(R.string.noSettings));
            builder.setMessage(getString(R.string.downlineHasNoSettings))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.gotoSettings), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            gotoSettings();
                        }
                    })
                    .setNegativeButton(getString(R.string.exitApp), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            closeApplication();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.firstTime = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG_TAG, "onCreateOptionsMenu");
        menu.add(Menu.NONE, R.id.menuSettings, 0, getString(R.string.settings));
        menu.add(Menu.NONE, R.id.menuAbout, 0, getString(R.string.about));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(LOG_TAG, "onOptionsItemSelected");
        String versionName = null;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }
        switch (item.getItemId()) {
            case R.id.menuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menuAbout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.icon);
                builder.setTitle(getString(R.string.aboutDownline));
                builder.setMessage("Downline " + versionName)
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.okButtonText), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                Log.i(LOG_TAG, "Wrong option");
                return super.onOptionsItemSelected(item);
        }
    }

    private void initControls() {
        Button refreshButton = (Button) findViewById(R.id.updateButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRefresh();
            }
        });

        ListView membersList = (ListView) findViewById(R.id.listMembers);
        fmGroupMemberAdapter = new FmGroupMemberAdapter<>(this, R.layout.groupmember);
        membersList.setAdapter(fmGroupMemberAdapter);
    }

    private void startRefresh() {
        String username = DownlineApp.getUsername();
        String password = DownlineApp.getPassword();
        DownloadTask asyncTask = new DownloadTask();
        asyncTask.setDownlineApp(app);
        asyncTask.execute(username, password);
    }

    private boolean properSettings() {
        return (DownlineApp.getUsername().length() > 0 && DownlineApp.getPassword().length() > 0);
    }

    private void closeApplication() {
        this.finish();
    }

    private void gotoSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void updateLatestUpdate() {
        long latestUpdate = DownlineApp.getLatestUpdate();
        TextView latestUpdateText = (TextView) findViewById(R.id.textLatestUpdate);
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
        TextView memberName = (TextView) findViewById(R.id.textMemberName);
        memberName.setText(fmGroupMember.getName());

        Log.i(LOG_TAG, "fmGroupMember.getLevel() " + fmGroupMember.getLevel());
        TextView memberLevel = (TextView) findViewById(R.id.textMemberLevel);
        memberLevel.setText(String.valueOf(fmGroupMember.getLevel()));

        Log.i(LOG_TAG, "fmGroupMember.getPersonalPoints() " + Utils.formatGetal(fmGroupMember.getPersonalPoints()));
        TextView memberPersonalPoints = (TextView) findViewById(R.id.textMemberPersonalPoints);
        memberPersonalPoints.setText(Utils.formatGetal(fmGroupMember.getPersonalPoints()));

        Log.i(LOG_TAG, "fmGroupMember.getGroupPoints() " + Utils.formatGetal(fmGroupMember.getGroupPoints()));
        TextView memberGroupPoints = (TextView) findViewById(R.id.textMemberGroupPoints);
        memberGroupPoints.setText(Utils.formatGetal(fmGroupMember.getGroupPoints()));

        ProgressBar levelProgress = (ProgressBar) findViewById(R.id.progressLevel);
        setLevelProgress(levelProgress, fmGroupMember);

        Log.i(LOG_TAG, "fmGroupMember.getEmailAdress() " + Utils.getEmailAddress(fmGroupMember.getAddress()));
        TextView memberEmailAddress = (TextView) findViewById(R.id.textMemberEmailAddress);
        memberEmailAddress.setText(Utils.getEmailAddress(fmGroupMember.getAddress()));

        Log.i(LOG_TAG, "fmGroupMember.getPhoneNumber() " + Utils.getPhoneNumber(fmGroupMember.getAddress()));
        TextView memberPhoneNumber = (TextView) findViewById(R.id.textMemberPhoneNumber);
        memberPhoneNumber.setText(Utils.getPhoneNumber(fmGroupMember.getAddress()));

        Collections.sort(fmGroupMember.getDownline(), new Comparator<FmGroupMember>() {
            @Override
            public int compare(FmGroupMember lhs, FmGroupMember rhs) {
                int lhsGroupPoints = (int)(lhs.getGroupPoints() * 100f);
                int rhsGroupPoints = (int)(rhs.getGroupPoints() * 100f);
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

    private void setLevelProgress(ProgressBar progressBar, FmGroupMember fmGroupMember) {
        LevelRanges.Range levelRange = LevelRanges.getRange(fmGroupMember.getLevel());
        if (levelRange != null) {
            progressBar.setMax((int) (levelRange.getMax() - levelRange.getMin()));
            progressBar.setProgress((int) (fmGroupMember.getGroupPoints() - levelRange.getMin()));
        }
    }

    private class FmGroupMemberAdapter<T extends FmGroupMember> extends ArrayAdapter<T> {

        public FmGroupMemberAdapter(Context context, int resourceId) {
            super(context, resourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(LOG_TAG, "FmGroupMemberAdapter.getView() " + position);
            FmGroupMemberViewHolder holder;
            View memberView;

            // Try and reuse old view first.
            if (convertView != null  && convertView.getTag() != null && convertView.getTag() instanceof FmGroupMemberViewHolder) {
                Log.i(LOG_TAG, "FmGroupMemberAdapter.getView(): re-using old view");
                holder = (FmGroupMemberViewHolder) convertView.getTag();
                memberView = convertView;
            } else {
                Log.i(LOG_TAG, "FmGroupMemberAdapter.getView(): inflating new view");
                LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                memberView = inflater.inflate(R.layout.groupmember, parent, false);
                holder = new FmGroupMemberViewHolder();
                holder.memberNameView = (TextView) memberView.findViewById(R.id.textMemberName);
                holder.memberLevelView = (TextView) memberView.findViewById(R.id.textMemberLevel);
                holder.memberPersonalPointsView = (TextView) memberView.findViewById(R.id.textMemberPersonalPoints);
                holder.memberGroupPointsiew = (TextView) memberView.findViewById(R.id.textMemberGroupPoints);
                holder.memberLevelProgress = (ProgressBar) memberView.findViewById(R.id.progressLevel);
                holder.memberEmailAddressView = (TextView) memberView.findViewById(R.id.textMemberEmailAddress);
                holder.memberPhoneNumberView = (TextView) memberView.findViewById(R.id.textMemberPhoneNumber);
            }
            FmGroupMember downlineMember = super.getItem(position);
            Log.i(LOG_TAG, "FmGroupMemberAdapter.getView(): member " + downlineMember.getName());
            holder.memberNameView.setText(downlineMember.getName());
            holder.memberLevelView.setText(String.valueOf(downlineMember.getLevel()));
            holder.memberPersonalPointsView.setText(Utils.formatGetal(downlineMember.getPersonalPoints()));
            holder.memberGroupPointsiew.setText(Utils.formatGetal(downlineMember.getGroupPoints()));
            setLevelProgress(holder.memberLevelProgress, downlineMember);
            holder.memberEmailAddressView.setText(Utils.getEmailAddress(downlineMember.getAddress()));
            holder.memberPhoneNumberView.setText(Utils.getPhoneNumber(downlineMember.getAddress()));
            memberView.setTag(holder);
            return memberView;
        }
    }

    private class FmGroupMemberViewHolder {
        TextView memberNameView;
        TextView memberLevelView;
        TextView memberPersonalPointsView;
        TextView memberGroupPointsiew;
        ProgressBar memberLevelProgress;
        TextView memberEmailAddressView;
        TextView memberPhoneNumberView;
    }
}
