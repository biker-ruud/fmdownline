package nl.fm.downline;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ProgressBar;
import nl.fm.downline.common.LevelRanges;
import nl.fm.downline.common.Retour;
import nl.fm.downline.csv.FmGroupMember;
import nl.fm.downline.csv.Parser;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * @author Ruud de Jong
 */
public class DownlineApp extends Application {

    public static final String PREF_KEY_USERNAME = "pref_username";
    public static final String PREF_KEY_PASSWORD = "pref_password";
    public static final String PREF_KEY_LASTEST_UPDATE = "pref_latest_update";
    public static final String INTENT_NAME_MEMBER_NUMBER = "memberNumber";

    private static final String LOG_TAG = "DownlineApp";
    private static final String SHARED_PREFERENCE_NAME = "nl.fm.downline_preferences";
    private static final int SHARED_PREFERENCE_MODE = MODE_PRIVATE;
    private static final String FILENAME = "downlineTree.csv";

    private static SharedPreferences sharedPreferences;
    private static DownlineApp INSTANCE;

    private FmGroupMember cachedParsedDownline;
    private Collection<RefreshListener> refreshListenerCollection = new HashSet<RefreshListener>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "OnCreate()");

        if (sharedPreferences == null) {
            sharedPreferences = this.getSharedPreferences(SHARED_PREFERENCE_NAME, SHARED_PREFERENCE_MODE);
        }

        INSTANCE = this;
    }

    public static DownlineApp getInstance() {
        return INSTANCE;
    }

    public static String getUsername() {
        return sharedPreferences.getString(PREF_KEY_USERNAME, "");
    }

    public static long getLatestUpdate() {
        return sharedPreferences.getLong(PREF_KEY_LASTEST_UPDATE, 0l);
    }

    public static String getPassword() {
        return sharedPreferences.getString(PREF_KEY_PASSWORD, "");
    }

    public FmGroupMember getFmDownline() {
        if (cachedParsedDownline == null) {
            cachedParsedDownline = parseFmDownline();
        }
        return cachedParsedDownline;
    }

    private FmGroupMember parseFmDownline() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n=0;
        int EOF = -1;

        try {
            FileInputStream fis = openFileInput(FILENAME);
            while (EOF != (n = fis.read(buffer))) {
                baos.write(buffer, 0, n);
            }
            fis.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "getFmDownline(): Could not open file", e);
        }
        Parser fmParser = new Parser();
        Retour<FmGroupMember, String> parseRetour = fmParser.parse(baos.toString());
        if (parseRetour.isSuccess()) {
            return parseRetour.getValue();
        }
        Log.e(LOG_TAG, "getFmDownline(): Could not parse csv");
        return null;
    }

    public void saveDownlineTree(String csvFile) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(csvFile.getBytes());
            fos.close();

            Date updateDate = new Date();
            SharedPreferences.Editor prefEditor = sharedPreferences.edit();
            prefEditor.putLong(PREF_KEY_LASTEST_UPDATE, updateDate.getTime());
            prefEditor.commit();

            Log.i(LOG_TAG, "saveDownlineTree(): Succesfully saved a new tree.");
            cachedParsedDownline = null;
            notifyRefreshListeners();
        } catch (IOException e) {
            Log.e(LOG_TAG, "saveDownlineTree(): Could not save file", e);
        }
    }

    public void setLevelProgress(ProgressBar progressBar, FmGroupMember fmGroupMember) {
        LevelRanges.Range levelRange = LevelRanges.getRange(fmGroupMember.getLevel());
        if (levelRange != null) {
            progressBar.setMax((int) (levelRange.getMax() - levelRange.getMin()));
            progressBar.setProgress((int) (fmGroupMember.getGroupPoints() - levelRange.getMin()));
        }
    }

    public void registerListener(RefreshListener resfreshListener) {
        refreshListenerCollection.add(resfreshListener);
    }

    private void notifyRefreshListeners() {
        for (RefreshListener refreshListener : refreshListenerCollection) {
            refreshListener.refresh();
        }
    }



}
