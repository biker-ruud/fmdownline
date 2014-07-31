package nl.fm.downline;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import nl.fm.downline.common.Utils;
import nl.fm.downline.csv.FmGroupMember;

/**
 * @author Ruud de Jong
 */
public class FmGroupMemberAdapter<T extends FmGroupMember> extends ArrayAdapter<T> {
    private static final String LOG_TAG = "FmGroupMemberAdapter";

    public FmGroupMemberAdapter(Context context, int resourceId) {
        super(context, resourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG, "FmGroupMemberAdapter.getView() " + position);
        FmGroupMemberViewHolder holder;
//        TestViewHolder holder;
        View memberView;

        // Try and reuse old view first.
//        if (convertView != null  && convertView.getTag() != null && convertView.getTag() instanceof TestViewHolder) {
        if (convertView != null  && convertView.getTag() != null && convertView.getTag() instanceof FmGroupMemberViewHolder) {
            Log.i(LOG_TAG, "FmGroupMemberAdapter.getView(): re-using old view");
            holder = (FmGroupMemberViewHolder) convertView.getTag();
//            holder = (TestViewHolder) convertView.getTag();
            memberView = convertView;
        } else {
            Log.i(LOG_TAG, "FmGroupMemberAdapter.getView(): inflating new view");
//            LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            memberView = inflater.inflate(R.layout.listmember, parent, false);
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            memberView = inflater.inflate(R.layout.list_item_card, parent, false);
            holder = new FmGroupMemberViewHolder();
            holder.memberNameView = (TextView) memberView.findViewById(R.id.textMemberName);
            holder.memberLineView = (TextView) memberView.findViewById(R.id.textMemberLine);
            holder.memberLevelView = (TextView) memberView.findViewById(R.id.textMemberLevel);
            holder.memberPointsiew = (TextView) memberView.findViewById(R.id.textMemberPoints);
            holder.memberLevelProgress = (ProgressBar) memberView.findViewById(R.id.progressLevel);
//            holder = new TestViewHolder();
//            holder.text1 = (TextView) memberView.findViewById(R.id.text1);
//            holder.text2 = (TextView) memberView.findViewById(R.id.text2);
//            holder.text3 = (TextView) memberView.findViewById(R.id.text3);
        }
        FmGroupMember downlineMember = super.getItem(position);
        Log.i(LOG_TAG, "FmGroupMemberAdapter.getView(): member " + downlineMember.getName());
        holder.memberNameView.setText(downlineMember.getName());
        holder.memberLineView.setText(String.valueOf(downlineMember.getLine()));
        holder.memberLevelView.setText(String.valueOf(downlineMember.getLevel()));
        String personalPoints = Utils.formatGetal(downlineMember.getPersonalPoints());
        String groupPoints = Utils.formatGetal(downlineMember.getGroupPoints());
        String combinedPoints = personalPoints + " / " + groupPoints;
        holder.memberPointsiew.setText(combinedPoints);
        DownlineApp.getInstance().setLevelProgress(holder.memberLevelProgress, downlineMember);
//        holder.text1.setText(downlineMember.getName());
//        holder.text2.setText("tussenregel");
//        holder.text3.setText(downlineMember.getNumber());
        memberView.setTag(holder);
        return memberView;
    }

    private static class FmGroupMemberViewHolder {
        TextView memberNameView;
        TextView memberLineView;
        TextView memberLevelView;
        TextView memberPointsiew;
        ProgressBar memberLevelProgress;
    }

    private static class TestViewHolder {
        TextView text1;
        TextView text2;
        TextView text3;
    }
}
