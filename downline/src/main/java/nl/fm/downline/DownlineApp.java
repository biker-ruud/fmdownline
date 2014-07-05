package nl.fm.downline;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Ruud de Jong
 */
public class DownlineApp extends Application {

    public static final String PREF_KEY_USERNAME = "pref_username";
    public static final String PREF_KEY_PASSWORD = "pref_password";

    private static final String LOG_TAG = "DownlineApp";
    private static final String SHARED_PREFERENCE_NAME = "nl.fm.downline_preferences";
    private static final int SHARED_PREFERENCE_MODE = MODE_PRIVATE;
    private static final String FILENAME = "downlineTree.csv";

    private static SharedPreferences sharedPreferences;
    private static DownlineApp INSTANCE;

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

    public static String getPassword() {
        return sharedPreferences.getString(PREF_KEY_PASSWORD, "");
    }

    public void saveDownlineTree(String csvFile) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(csvFile.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "saveDownlineTree(): Could not save file", e);
        }
    }


}
