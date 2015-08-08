package com.sjgilbert.unanimus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

/**
 * This is for the ListView of the FriendPickerActivity.  It's just an adapter with two
 * Lists of strings, one for names and the other for facebook IDs.
 */
public class FriendPickerListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final List<String> names;
    private final List<String> ids;
    public FriendPickerListAdapter(Activity context,
                      List<String> names, List<String> ids) {
        super(context, R.layout.friend_fragment, names);
        this.context = context;
        this.names = names;
        this.ids = ids;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.friend_fragment, null, true);
        TextView name = (TextView) rowView.findViewById(R.id.friend_picker_facebook_name);

        ProfilePictureView profPic = (ProfilePictureView) rowView.findViewById(R.id.friend_picker_profile_picture);
        name.setText(names.get(position));

        profPic.setProfileId(ids.get(position));
        return rowView;
    }
}
