package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

/**
 * This is for the ListView of the FriendPickerActivity.  It's just an adapter with two
 * Lists of strings, one for names and the other for facebook IDs.
 */
public class FriendPickerListAdapter extends ArrayAdapter<String> {
    private final List<String> names;
    private final List<String> ids;

    public FriendPickerListAdapter(Activity context, List<String> names, List<String> ids) {
        super(context, R.layout.friend_fragment, names);
        this.names = names;
        this.ids = ids;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if(convertView == null){
            mViewHolder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.friend_fragment, parent, false);

            try {
                mViewHolder.profPic = (ProfilePictureView) convertView.findViewById(R.id.fpa_profile_picture);
                mViewHolder.profPic.setProfileId(ids.get(position));
            } catch (NullPointerException | ClassCastException e) {
                e.printStackTrace();
                mViewHolder.profPic = new ProfilePictureView(getContext());
            }

            try {
                mViewHolder.name = (TextView) convertView.findViewById(R.id.fpa_facebook_name);
                mViewHolder.name.setText(names.get(position));
            } catch (NullPointerException | ClassCastException e) {
                e.printStackTrace();
                mViewHolder.name = new TextView(getContext());
            }

            convertView.setTag(mViewHolder);
        } else {
            try {
                mViewHolder = (ViewHolder) convertView.getTag();
            } catch (ClassCastException e) {
                e.printStackTrace();
                return getView(position, null, parent);
            }
        }

        mViewHolder.profPic.setProfileId(ids.get(position));
        mViewHolder.name.setText(names.get(position));

        return convertView;
    }

    private static class ViewHolder {
        private ProfilePictureView profPic;
        private TextView name;
    }
}
