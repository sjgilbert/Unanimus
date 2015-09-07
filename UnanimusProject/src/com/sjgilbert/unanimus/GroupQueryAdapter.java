package com.sjgilbert.unanimus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 9/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class GroupQueryAdapter extends ParseQueryAdapter<UnanimusGroup> {
    private static final String TAG = "Gqa";

    public GroupQueryAdapter(Context context, QueryFactory<UnanimusGroup> queryFactory, int itemViewResource) {
        super(context, queryFactory, itemViewResource);
    }

    public GroupQueryAdapter(Context context, QueryFactory<UnanimusGroup> queryFactory) {
        super(context, queryFactory);
    }

    public GroupQueryAdapter(Context context, String className, int itemViewResource) {
        super(context, className, itemViewResource);
    }

    public GroupQueryAdapter(Context context, Class<? extends ParseObject> clazz, int itemViewResource) {
        super(context, clazz, itemViewResource);
    }

    public GroupQueryAdapter(Context context, String className) {
        super(context, className);
    }

    public GroupQueryAdapter(Context context, Class<? extends ParseObject> clazz) {
        super(context, clazz);
    }

    @Override
    public View getItemView(UnanimusGroup newUnanimusGroup, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(getContext(), R.layout.unanimus_group_abstract, null);

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new ViewHolder((ViewGroup) convertView, newUnanimusGroup);
            convertView.setTag(viewHolder);
            return convertView;
        }

        try {
            viewHolder.update(newUnanimusGroup);
        } catch (ParseException e) {
            Log.e(UnanimusApplication.UNANIMUS + TAG, e.getMessage(), e);
        }

        return convertView;
    }

    static class ViewHolder {
        private static final String TAG = GroupQueryAdapter.TAG + "Vh";
        private static final Map<String, String> NAMES = new Hashtable<>();
        private static final Map<String, String> FACEBOOK_IDS = new Hashtable<>();

        private static final Set<String> LOADED = new ConcurrentSkipListSet<>();

        final ProfilePictureView profilePictureView;
        final TextView textView;

        UnanimusGroup currentUnanimusgroup;

        private ViewHolder(
                ViewGroup viewGroup,
                UnanimusGroup newUnanimusGroup
        ) {
            this.profilePictureView = (ProfilePictureView) viewGroup.findViewById(R.id.uga_invited_by);
            this.textView = (TextView) viewGroup.findViewById(R.id.uga_groupID_view);

            try {
                update(newUnanimusGroup);
            } catch (ParseException e) {
                Log.e(UnanimusApplication.UNANIMUS + TAG, e.getMessage(), e);
            }
        }

        private void update(UnanimusGroup newUnanimusGroup) throws ParseException {
            if (newUnanimusGroup == null)
                return;

            final String newObjectId = newUnanimusGroup.getObjectId();

            if (currentUnanimusgroup != null
                    && newObjectId.contentEquals(newUnanimusGroup.getObjectId()))
                return;

            if (!LOADED.contains(newObjectId)) {
                new GqaLoadWorker().execute(newUnanimusGroup);
                return;
            }

            CgaContainer newCgaContainer = newUnanimusGroup.getCgaContainer();

            final String newOwner = newCgaContainer.getOwnerId();

            if (newOwner == null)
                return;

            String newFacebookId = FACEBOOK_IDS.get(newOwner);

            if (newFacebookId == null) {
                ParseQuery
                        .getQuery(ParseUser.class)
                        .getInBackground(newOwner, new GqaGetCallback(newUnanimusGroup));
                return;
            }

            String currentFacebookId = profilePictureView.getProfileId();

            if (currentFacebookId == null
                    || ! currentFacebookId.contentEquals(newFacebookId))
                profilePictureView.setProfileId(newFacebookId);

            String newName = NAMES.get(newOwner);

            if (newName == null) {
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        String.format("/%s", newFacebookId),
                        null,
                        HttpMethod.GET,
                        new GqaGraphRequestCallback(newUnanimusGroup)
                ).executeAsync();
                return;
            }

            CharSequence currentName = textView.getText();

            if (currentName == null
                    || ! newName.contentEquals(currentName))
                textView.setText(NAMES.get(newOwner));

            this.currentUnanimusgroup = newUnanimusGroup;
        }

        private class GqaGetCallback implements GetCallback<ParseUser> {
            private static final String TAG = ViewHolder.TAG + "Ggc";

            final UnanimusGroup newUnanimusGroup;

            GqaGetCallback(UnanimusGroup newUnanimusGroup) {
                this.newUnanimusGroup = newUnanimusGroup;
            }

            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    Log.e(UnanimusApplication.UNANIMUS + TAG, e.getMessage(), e);
                    return;
                }

                CgaContainer newCgaContainer = newUnanimusGroup.getCgaContainer();

                FACEBOOK_IDS.put(
                        newCgaContainer.getOwnerId(),
                        (String) parseUser.get(FriendPickerActivity.FACEBOOK_ID)
                );

                try {
                    update(newUnanimusGroup);
                } catch (ParseException e1) {
                    Log.e(UnanimusApplication.UNANIMUS + TAG, e1.getMessage(), e1);
                }
            }
        }

        private class GqaGraphRequestCallback implements GraphRequest.Callback {
            private static final String TAG = ViewHolder.TAG + "Ggrc";

            final UnanimusGroup newUnanimusGroup;

            GqaGraphRequestCallback(UnanimusGroup newUnanimusGroup) {
                this.newUnanimusGroup = newUnanimusGroup;
            }

            @Override
            public void onCompleted(GraphResponse graphResponse) {
                if (graphResponse.getError() != null) {
                    Log.e(
                            UnanimusApplication.UNANIMUS + TAG,
                            graphResponse.getError().getErrorMessage(),
                            graphResponse.getError().getException()
                    );
                    return;
                }

                CgaContainer newCgaContainer = newUnanimusGroup.getCgaContainer();

                try {
                    NAMES.put(
                            newCgaContainer.getOwnerId(),
                            graphResponse.getJSONObject().getString("name")
                    );

                    update(newUnanimusGroup);
                } catch (NullPointerException | JSONException | ParseException e) {
                    Log.e(UnanimusApplication.UNANIMUS + TAG, e.getMessage(), e);
                }
            }
        }

        private class GqaLoadWorker extends AsyncTask<UnanimusGroup, Void, UnanimusGroup> {
            private static final String TAG = ViewHolder.TAG + "Glw";

            @Override
            protected UnanimusGroup doInBackground(UnanimusGroup... params) {
                try {
                    params[0].load();
                } catch (ParseException e) {
                    Log.e(UnanimusApplication.UNANIMUS + TAG, e.getMessage(), e);
                }

                return params[0];
            }

            @Override
            protected void onPostExecute(UnanimusGroup result) {
                LOADED.add(result.getObjectId());

                try {
                    update(result);
                } catch (ParseException e) {
                    Log.e(UnanimusApplication.UNANIMUS + TAG, e.getMessage(), e);
                }
            }
        }
    }
}
