package nl.fm.downline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import nl.fm.downline.common.Utils;
import nl.fm.downline.csv.FmGroupMember;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ruud de Jong
 */
public class Downline extends Activity {

    private static final String LOG_TAG = "Downline";

    private boolean firstTime = true;
    private DownlineApp app;

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
//            builder.setIcon(R.drawable.icon);
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
//                builder.setIcon(R.drawable.icon);
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
    }
}
