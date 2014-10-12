package nl.fm.downline;

import android.os.Bundle;
import android.util.Log;
import nl.fm.downline.csv.FmGroupMember;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author Ruud de Jong
 */
@ContentView(R.layout.level)
public class Level extends RoboActivity {

    private static final String LOG_TAG = "Level";

    @InjectView(R.id.levelGraph)
    private LevelView levelGraph;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        FmGroupMember fmGroupMember = DownlineApp.getInstance().getFmDownline();
        if (fmGroupMember != null) {
            levelGraph.setLevelInfo(fmGroupMember.getLevel(), fmGroupMember.getGroupPoints());
            levelGraph.invalidate();
        }
    }
}
