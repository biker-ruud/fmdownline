package nl.fm.downline;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import nl.fm.downline.csv.FmGroupMember;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

/**
 * @author Ruud de Jong
 */
@ContentView(R.layout.main)
public class Downline extends RoboActivity implements MemberSelectionListener {

    private static final String LOG_TAG = "Downline";

    private FmGroupMember chosenMember;

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

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DownlineFragment downlineFragment = new DownlineFragment();
        fragmentTransaction.add(R.id.fragment_placeholder, downlineFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void chosenMember(FmGroupMember member) {
        chosenMember= member;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MemberFragment memberFragment = new MemberFragment();
        fragmentTransaction.replace(R.id.fragment_placeholder, memberFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public FmGroupMember getChosenMember() {
        return chosenMember;
    }
}
