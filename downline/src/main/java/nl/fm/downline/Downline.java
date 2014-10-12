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
    private static final String BUNDLE_STATE_ACTIVE_FRAGMENT = "ActiveFragment";
    private static final String BUNDLE_STATE_CHOSEN_MEMBER = "ChosenMemberNumber";

    private static final String FRAGMENT_TAG_DOWNLINE = "DownlineFragment";
    private static final String FRAGMENT_TAG_MEMBER = "MemberFragment";

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

        if (savedInstanceState == null) {
            Log.i(LOG_TAG, "onCreate: Adding fragment, first creation.");
            createDownlineFragment();
        } else {
            if (savedInstanceState.get(BUNDLE_STATE_ACTIVE_FRAGMENT) != null) {
                Log.i(LOG_TAG, "onCreate: Found old active fragment.");
                String currentFragmentTag = savedInstanceState.getString(BUNDLE_STATE_ACTIVE_FRAGMENT);
                if (FRAGMENT_TAG_MEMBER.equals(currentFragmentTag)) {
                    Log.i(LOG_TAG, "onCreate: Old active fragment was MemberFragment.");
                    createDownlineFragment();
                    replaceMemberFragment();
                } else {
                    Log.i(LOG_TAG, "onCreate: Old active fragment was something else.");
                    createDownlineFragment();
                }
            }
            if (savedInstanceState.get(BUNDLE_STATE_CHOSEN_MEMBER) != null) {
                for (FmGroupMember member : DownlineApp.getInstance().getFmDownline().getDownline()) {
                    if (member.getNumber().equals(savedInstanceState.get(BUNDLE_STATE_CHOSEN_MEMBER))) {
                        Log.i(LOG_TAG, "onCreate: Restoring old chosen member number: " + member.getNumber());
                        chosenMember = member;
                    }
                }
            }
        }
    }

    private void createDownlineFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DownlineFragment downlineFragment = new DownlineFragment();
        fragmentTransaction.add(R.id.fragment_placeholder, downlineFragment, FRAGMENT_TAG_DOWNLINE);
        fragmentTransaction.commit();
    }

    private void replaceMemberFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MemberFragment memberFragment = new MemberFragment();
        fragmentTransaction.replace(R.id.fragment_placeholder, memberFragment, FRAGMENT_TAG_MEMBER);
        fragmentTransaction.addToBackStack(null);
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
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState");
        FragmentManager fragmentManager = getFragmentManager();
        Log.i(LOG_TAG, "onSaveInstanceState backstack count " + fragmentManager.getBackStackEntryCount());
        String currentFragmentTag = FRAGMENT_TAG_DOWNLINE;
        if (fragmentManager.getBackStackEntryCount() == 1) {
            currentFragmentTag = FRAGMENT_TAG_MEMBER;
        }
        Log.i(LOG_TAG, "onSaveInstanceState current fragment is: " + currentFragmentTag);
        outState.putString(BUNDLE_STATE_ACTIVE_FRAGMENT, currentFragmentTag);
        if (chosenMember != null) {
            outState.putString(BUNDLE_STATE_CHOSEN_MEMBER, chosenMember.getNumber());
        }
    }

    @Override
    public void chosenMember(FmGroupMember member) {
        chosenMember= member;
        replaceMemberFragment();
    }

    @Override
    public FmGroupMember getChosenMember() {
        return chosenMember;
    }
}
