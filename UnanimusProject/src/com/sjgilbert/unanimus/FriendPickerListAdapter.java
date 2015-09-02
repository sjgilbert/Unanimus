package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

/**
 * This is for the ListView of the FriendPickerActivity.  It's just an adapter with two
 * Lists of strings, one for allFriendNames and the other for facebook IDs.
 */
class FriendPickerListAdapter extends ArrayAdapter<String> {
    static final int SELECTED_COLOR = Color.parseColor("#81c784");
    static final int UNSELECTED_COLOR = Color.TRANSPARENT;

    private final List<String> allFriendNames;
    private final List<FacebookId> allFacebookIds;
    private final List<FacebookId> selectedFacebookIds;

    public FriendPickerListAdapter(Activity context, List<String> allFriendNames, List<FacebookId> allFacebookIds, List<FacebookId> selectedFacebookIds) {
        super(context, R.layout.friend_fragment, allFriendNames);
        this.allFriendNames = allFriendNames;
        this.allFacebookIds = allFacebookIds;
        this.selectedFacebookIds = selectedFacebookIds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if (convertView == null) {
            mViewHolder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.friend_fragment, parent, false);

            try {
                mViewHolder.profPic = (ProfilePictureView) convertView.findViewById(R.id.fpa_profile_picture);
                mViewHolder.profPic.setProfileId(allFacebookIds.get(position).toString());
            } catch (NullPointerException | ClassCastException e) {
                mViewHolder.profPic = new ProfilePictureView(getContext());
            }

            try {
                mViewHolder.name = (TextView) convertView.findViewById(R.id.fpa_facebook_name);
                mViewHolder.name.setText(allFriendNames.get(position));
            } catch (NullPointerException | ClassCastException e) {
                logError(R.id.fpa_facebook_name, e);
                mViewHolder.name = new TextView(getContext());
            }

            convertView.setTag(mViewHolder);
        } else {
            try {
                mViewHolder = (ViewHolder) convertView.getTag();
            } catch (ClassCastException e) {
                Log.e("Unanimus", "Unexpected exception", e);
                return getView(position, null, parent);
            }
        }

        if (selectedFacebookIds.contains(allFacebookIds.get(position))) {
            convertView.setBackgroundColor(SELECTED_COLOR);
        } else {
            convertView.setBackgroundColor(UNSELECTED_COLOR);
        }

        mViewHolder.profPic.setProfileId(allFacebookIds.get(position).toString());
        mViewHolder.name.setText(allFriendNames.get(position));

        return convertView;
    }

    private int logError(int id, Throwable e) {
        return Log.e(
                "Unanimus",
                String.format(
                        "%s\n%s: %s (\"%s\")\n%s",
                        "Unexpected error!",
                        "Check for resource with id",
                        Integer.toHexString(id),
                        "fpa_profile_picture",
                        new Object() {
                            public String getMessage(Throwable tr) {
                                if (tr instanceof NullPointerException) {
                                    return "Cannot find layout with id.";
                                }
                                if (tr instanceof ClassCastException) {
                                    return "Found layout with id, but it is not a TextView.";
                                }
                                Log.w("Unanimus", "Caught unexpected exception with no log message to match.");
                                return new String();
                            }
                        }.getMessage(e)
                ),
                e
        );
    }

    private static class ViewHolder {
        private ProfilePictureView profPic;
        private TextView name;
    }
}
