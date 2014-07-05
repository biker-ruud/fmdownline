package nl.fm.downline;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ruud de Jong
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String LOG_TAG = "SettingsActivity";
    private Map<String, String> pretextMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        addPreferencesFromResource(R.xml.preferences);
        createPretextMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        //Iterate over all preference objects and set initial values
        for (Map.Entry<String, String> entry : pretextMap.entrySet()){
            changePreferenceSummary(entry.getKey(), getPreferenceScreen().getSharedPreferences());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    private void changePreferenceSummary(String s, SharedPreferences sharedPreferences) {
        Preference preference = findPreference(s);
        String preText = pretextMap.get(s);
        if (preText != null) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                if (listPreference.getEntry() == null) {
                    preference.setSummary(pretextMap.get(s));
                } else {
                    preference.setSummary(pretextMap.get(s) + " " + listPreference.getEntry());
                }
            } else {
                preference.setSummary(pretextMap.get(s) + " " + sharedPreferences.getString(s, ""));
            }
        }
    }

    private void createPretextMap() {
        EditTextPreference usernamePreference = (EditTextPreference) findPreference(DownlineApp.PREF_KEY_USERNAME);
        pretextMap.put(DownlineApp.PREF_KEY_USERNAME, getString(R.string.distributorID_setting_pretext));
        usernamePreference.setTitle(getString(R.string.distributorID));
        changePreferenceSummary(DownlineApp.PREF_KEY_USERNAME, getPreferenceScreen().getSharedPreferences());
    }
}
