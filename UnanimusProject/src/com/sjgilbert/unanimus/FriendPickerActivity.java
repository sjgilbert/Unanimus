package com.sjgilbert.unanimus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the friend picker activity which allows the user to
 * select the friends who will be part of the group.
 * <p/>
 * JSON adapter solution found at http://stackoverflow.com/questions/6277154/populate-listview-from-json
 * Custom ArrayList adapter idea for ListView fount at http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 */
public class FriendPickerActivity extends UnanimusActivityTitle {
    static final String FPA = "FpaContainer";
    private final FpaContainer fpaContainer = new FpaContainer();

    public FriendPickerActivity() {
        super("fpa");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_picker_activity);
        try {
            setTitleBar(R.string.fpa_title, (ViewGroup) findViewById(R.id.friend_picker_activity).findViewById(R.id.fpa_title_bar));
        } catch (NullPointerException | ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        fpaContainer.setDefault();

        //The request for facebook friends
        GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray friends, GraphResponse response) {
                        if (response.getError() != null) {
                            log(ELog.e, response.getError().toString());
                            return;
                        }

                        int length = friends.length();
                        List<String> names = new ArrayList<>(length);
                        final List<String> ids = new ArrayList<>(length);

                        try {
                            for (int i = 0; i < length; ++i) {
                                names.add(friends.getJSONObject(i).getString("name"));
                                ids.add(friends.getJSONObject(i).getString("id"));
                            }
                        } catch (JSONException e) {
                            log(ELog.e, e.getMessage(), e);
                            return;
                        }

                        ListView friendListView = (ListView) findViewById(R.id.fpa_list_view);

                        friendListView.setAdapter(
                                new FriendPickerListAdapter(
                                        FriendPickerActivity.this,
                                        names,
                                        ids
                                )
                        );

                        friendListView.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(
                                            AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id
                                    ) {
                                        final String idp = ids.get(position);
                                        final int color;
                                        if (fpaContainer.facebookIDs.contains(idp)) {
                                            removeFacebookID(idp);
                                            color = Color.TRANSPARENT;
                                        } else {
                                            addFacebookID(idp);
                                            color = Color.argb(127, 0, 0, 255);
                                        }

                                        view.setBackgroundColor(color);
                                    }
                                }
                        );
                    }
                }
        ).executeAsync();

        Button doneButton = (Button) findViewById(R.id.fpa_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fpaContainer.getFacebookIDs().size() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendPickerActivity.this);
                    builder.setMessage("No friends selected!  Continue anyway?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    finish();
                }
            }
        });
    }

    private void addFacebookID(String friendID) {
        fpaContainer.facebookIDs.add(friendID);
        if (fpaContainer.isSet()) fpaContainer.isSet = true;
    }

    private void removeFacebookID(String friendID) {
        fpaContainer.facebookIDs.remove(friendID);
        if (! fpaContainer.isSet()) fpaContainer.isSet = false;
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        int result;
        if (fpaContainer.isSet()) {
            intent.putExtra(FPA, fpaContainer.getAsBundle());
            result = RESULT_OK;
        } else {
            result = RESULT_CANCELED;
        }
        setResult(result, intent);

        super.finish();
    }

    static class FpaContainer extends CreateGroupActivity.ADependencyContainer {
        private final static String FACEBOOK_IDS = "facebookIDs";

        private ArrayList<String> facebookIDs;

        @Override
        public boolean isSet() {
            return (1 < facebookIDs.size());
        }

        @Override
        public Bundle getAsBundle() {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(FACEBOOK_IDS, facebookIDs);

            try {
                super.getAsBundle();
            } catch (NotSetException e) {
                Log.e("Unanimus", e.getMessage(), e);
                return null;
            }

            return bundle;
        }

        @Override
        public void setDefault() {
            final String userFacebookID = ParseUser.getCurrentUser().getString("facebookID");
            facebookIDs = new ArrayList<>();
            facebookIDs.add(userFacebookID);
        }

        @Override
        public void setFromBundle(Bundle bundle) {
            facebookIDs = bundle.getStringArrayList(FACEBOOK_IDS);
            super.setFromBundle(bundle);
        }

        @SuppressWarnings("unused")
        ArrayList<String> getFacebookIDs() {
            return facebookIDs;
        }
    }
}
