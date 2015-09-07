package com.sjgilbert.unanimus;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.parse.ParseQueryAdapter;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivity;

import org.json.JSONException;

import java.util.Hashtable;
import java.util.Map;

/**
 * 9/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class GroupQueryAdapter extends ParseQueryAdapter<CgaContainer> {
    private static final String VIEW_HOLDER = "viewHolder";

    public GroupQueryAdapter(Context context, QueryFactory<CgaContainer> queryFactory, int itemViewResource) {
        super(context, queryFactory, itemViewResource);
    }

    public GroupQueryAdapter(Context context, QueryFactory<CgaContainer> queryFactory) {
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
    public View getItemView(CgaContainer cgaContainer, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(getContext(), R.layout.unanimus_group_abstract, null);

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new ViewHolder((ViewGroup) convertView, cgaContainer);
            convertView.setTag(viewHolder);
        }

        viewHolder.update(cgaContainer);

        return convertView;
    }

    static class ViewHolder {
        private static final Map<String, String> NAMES = new Hashtable<>();

        final ProfilePictureView profilePictureView;
        final TextView textView;

        CgaContainer cgaContainer;

        private ViewHolder(
                ViewGroup viewGroup,
                CgaContainer cgaContainer
        ) {
            this.profilePictureView = (ProfilePictureView) viewGroup.findViewById(R.id.uga_invited_by);
            this.textView = (TextView) viewGroup.findViewById(R.id.uga_groupID_view);

            update(cgaContainer);
        }

        private void update(CgaContainer newCgaContainer) {
            if (newCgaContainer == cgaContainer)
                return;

            this.cgaContainer = newCgaContainer;

            cgaContainer.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e != null) {
                        Log.e(UnanimusApplication.UNANIMUS, e.getMessage(), e);
                    }

                    cgaContainer = (CgaContainer) parseObject;

                }
            });

            try {
                if (cgaContainer.getFpaContainer() == null)
                    cgaContainer.load();
            } catch (ParseException e) {
                Log.e(UnanimusApplication.UNANIMUS, e.getMessage(), e);
            }

            final String owner = cgaContainer.getOwnerId();

            profilePictureView.setProfileId(owner);

            if (! NAMES.containsKey(owner)) {
                textView.setText(NAMES.get(owner));
                return;
            }

            GraphRequest graphRequest = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    String.format("/%s", owner),
                    null,
                    HttpMethod.GET,
                    new Callback(owner, textView)
            );

            graphRequest.executeAsync();
        }

        private static class Callback implements GraphRequest.Callback {
            private final String owner;
            private final TextView textView;

            private Callback(String owner, TextView textView) {
                this.owner = owner;
                this.textView = textView;
            }

            @Override
            public void onCompleted(GraphResponse graphResponse) {
                if (graphResponse.getError() != null) {
                    Log.e(
                            UnanimusApplication.UNANIMUS,
                            graphResponse.getError().getErrorMessage(),
                            graphResponse.getError().getException()
                    );
                    return;
                }

                try {
                    NAMES.put(owner, graphResponse.getJSONObject().getString("name"));
                    textView.setText(NAMES.get(owner));
                } catch (NullPointerException | JSONException e) {
                    Log.e(UnanimusApplication.UNANIMUS, e.getMessage(), e);
                }
            }
        }
    }
}
