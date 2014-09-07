package nl.fm.downline;

import android.os.Bundle;
import android.util.Log;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

/**
 * @author Ruud de Jong
 */
@ContentView(R.layout.level)
public class Level extends RoboActivity {

    private static final String LOG_TAG = "Level";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "onCreate");
    }
}
