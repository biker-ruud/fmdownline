package nl.fm.downline;

import android.os.AsyncTask;
import android.util.Log;
import nl.fm.downline.common.Retour;
import nl.fm.downline.csv.FmGroupMember;
import nl.fm.downline.csv.Parser;
import nl.fm.downline.webclient.FmGroupClientImpl;

/**
 * @author Ruud de Jong
 */
public class DownloadTask extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = "DownloadTask";

    private DownlineApp app;

    public void setDownlineApp(DownlineApp app) {
        this.app = app;
    }

    @Override
    protected String doInBackground(String... params) {
        FmGroupClientImpl fmGroupClient = new FmGroupClientImpl();
        Retour<String, String> retour = fmGroupClient.start(params[0], params[1]);
        if (retour.isSuccess()) {
            return retour.getValue();
        }
        Log.i(LOG_TAG, "doInBackground(): not succesfull");
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            return;
        }
        Parser fmParser = new Parser();
        Retour<FmGroupMember, String> downlineTreeParseRetour = fmParser.parse(result);
        if (downlineTreeParseRetour.isSuccess()) {
            app.saveDownlineTree(result);
        }
    }
}
