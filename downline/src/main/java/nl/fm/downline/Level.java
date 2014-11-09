package nl.fm.downline;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import nl.fm.downline.common.LevelRanges;
import nl.fm.downline.common.Utils;
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
    @InjectView(R.id.textCurrentLevel)
    private TextView currentLevel;
    @InjectView(R.id.textStartCurrentLevel)
    private TextView startCurrentLevel;
    @InjectView(R.id.textStopCurrentLevel)
    private TextView stopCurrentLevel;
    @InjectView(R.id.textProgressCurrentLevel)
    private TextView progressCurrentLevel;
    @InjectView(R.id.textLeftCurrentLevel)
    private TextView leftCurrentLevel;

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
            currentLevel.setText(String.valueOf(fmGroupMember.getLevel()));
            LevelRanges.Range currentRange = LevelRanges.getRange(fmGroupMember.getLevel());
            if (currentRange != null) {
                startCurrentLevel.setText(String.valueOf((int)currentRange.getMin()));
                stopCurrentLevel.setText(String.valueOf((int)currentRange.getMax()));
                float rangeWidth = currentRange.getMax() - currentRange.getMin();
                float relativeStart = fmGroupMember.getGroupPoints() - currentRange.getMin();
                float percentage = 100.0f * relativeStart / rangeWidth;
                progressCurrentLevel.setText(Utils.formatGetal(percentage) + " %");
                leftCurrentLevel.setText(Utils.formatGetal(currentRange.getMax() - fmGroupMember.getGroupPoints()));
            }
        }
    }
}
