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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import nl.fm.downline.common.LevelRanges;
import nl.fm.downline.common.Utils;
import nl.fm.downline.csv.FmGroupMember;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Ruud de Jong
 */
public class Downline extends Activity implements RefreshListener {

    private static final String LOG_TAG = "Downline";

    private boolean firstTime = true;
    private DownlineApp app;
    private FmGroupMemberAdapter<FmGroupMember> fmGroupMemberAdapter;
    private MenuItem refreshItem;

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
            refresh();
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        refreshItem = menu.findItem(R.id.menuRefresh);
        refreshItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRefresh();
            }
        });
        return true;
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
            case R.id.menuRefresh:
                // Menu refresh has it's own onclick handler
                return true;
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
        ListView membersList = (ListView) findViewById(R.id.listMembers);
        membersList.addHeaderView(new View(this));
        membersList.addFooterView(new View(this));
//        fmGroupMemberAdapter = new FmGroupMemberAdapter<>(this, R.layout.listmember);
        fmGroupMemberAdapter = new FmGroupMemberAdapter<>(this, R.layout.list_item_card);
        membersList.setAdapter(fmGroupMemberAdapter);

        membersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object chosenItem = parent.getItemAtPosition(position);
                if (chosenItem instanceof FmGroupMember) {
                    FmGroupMember chosenMember = (FmGroupMember) chosenItem;
                    Log.i(LOG_TAG, "Chosen member " + chosenMember.getName());
                    Intent intent = new Intent(view.getContext(), Member.class);
                    intent.putExtra(DownlineApp.INTENT_NAME_MEMBER_NUMBER, chosenMember.getNumber());
                    startActivity(intent);
                }
            }
        });
    }

    private void startRefresh() {
        /* Attach a rotating ImageView to the refresh item as an ActionView */
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshItem.getActionView().startAnimation(rotation);

        String username = DownlineApp.getUsername();
        String password = DownlineApp.getPassword();
        DownloadTask asyncTask = new DownloadTask();
        asyncTask.setDownlineApp(app);
        asyncTask.setRefreshListener(this);
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

        Log.i(LOG_TAG, "fmGroupMember.getEarnings() " + Utils.formatGetal(fmGroupMember.getEarnings()));
        TextView memberEarnings = (TextView) findViewById(R.id.textMemberEarnings);
        memberEarnings.setText("€ " + Utils.formatGetal(fmGroupMember.getEarnings()));

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


    @Override
    public void refresh() {
        if (refreshItem != null && refreshItem.getActionView() != null) {
            refreshItem.getActionView().clearAnimation();
        }

        updateLatestUpdate();
        FmGroupMember fmDownlineTree = app.getFmDownline();
        render(fmDownlineTree);
    }

}
