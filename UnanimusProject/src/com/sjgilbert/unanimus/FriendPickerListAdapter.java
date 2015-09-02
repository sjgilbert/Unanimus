package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import java.util.ArrayList;
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
    private final Context context;

    @SuppressWarnings("unused")
    public FriendPickerListAdapter(
            Activity context,
            List<String> allFriendNames,
            List<FacebookId> allFacebookIds) {
        super(context, R.layout.friend_fragment, allFriendNames);
        this.context = context;
        this.allFriendNames = allFriendNames;
        this.allFacebookIds = allFacebookIds;

        this.selectedFacebookIds = new ArrayList<>(1);

        selectedFacebookIds.add(
                new FacebookId(ParseUser.getCurrentUser().getString("facebookID"))
        );
    }

    public FacebookId[] getSelectedFacebookIds() {
        return selectedFacebookIds.toArray(new FacebookId[selectedFacebookIds.size()]);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.friend_fragment, parent, false);
        }

        viewHolder = (ViewHolder) convertView.getTag();

        if (viewHolder == null) {
            final ViewGroup convertViewGroup = (ViewGroup) convertView;

            viewHolder = new ViewHolder(
                    convertViewGroup,
                    position);
        }

        viewHolder.update(position);

        return convertView;
    }

    class ViewHolder {
        private final ProfilePictureView profilePictureView;
        private final TextView textViewName;
        private final ViewGroup viewGroup;

        private FacebookId facebookId;

        @SuppressWarnings("unused")
        private ViewHolder(
                ViewGroup viewGroup,
                int position
        ) {
            this.viewGroup = viewGroup;

            ProfilePictureView profilePictureView;
            TextView textViewName;

            profilePictureView = (ProfilePictureView) viewGroup
                    .findViewById(R.id.fpa_profile_picture);

            if (profilePictureView == null) {
                profilePictureView = new ProfilePictureView(getContext());
                profilePictureView.setId(R.id.fpa_profile_picture);

                viewGroup.addView(profilePictureView);
            }

            textViewName = (TextView) viewGroup
                    .findViewById(R.id.fpa_facebook_name);

            if (textViewName == null) {
                textViewName = new TextView(getContext());
                textViewName.setId(R.id.fpa_facebook_name);

                viewGroup.addView(textViewName);
            }

            textViewName.setText(allFriendNames.get(position));

            this.profilePictureView = profilePictureView;
            this.textViewName = textViewName;

            this.viewGroup.setTag(this);

            update(position);
        }

        void toggleSelected() {
            if (selectedFacebookIds.contains(facebookId)) selectedFacebookIds.remove(facebookId);
            else selectedFacebookIds.add(facebookId);

            updateColor();
        }

        private void updateColor() {
            viewGroup.setBackgroundColor(
                    (selectedFacebookIds.contains(facebookId))
                            ? SELECTED_COLOR
                            : UNSELECTED_COLOR
            );
        }

        private void update(int position) {
            facebookId = allFacebookIds.get(position);

            profilePictureView.setProfileId(facebookId.toString());
            textViewName.setText(allFriendNames.get(position));

            updateColor();
        }

    }
}
