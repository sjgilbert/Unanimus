package com.sjgilbert.unanimus;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
    private ArrayList<String> groupMembersFacebookIDs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_picker_activity);
        try {
            setTitleBar(R.string.friend_picker_activity_title, (ViewGroup) findViewById(R.id.friend_picker_activity).findViewById(R.id.friend_picker_title_bar));
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }

        groupMembersFacebookIDs = new ArrayList<>();

        Button doneButton = (Button) findViewById(R.id.friend_picker_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:whatever needs to be done with groupMembersFacebookIDs
            }
        });

        //The request for facebook friends
        GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray friends, GraphResponse response) {
                if (response.getError() != null) {
                    Log.d("Unanimus", response.getError().toString());
                } else {
                    try {
                        int length = friends.length();
                        List<String> names = new ArrayList<>(length);
                        final List<String> ids = new ArrayList<>(length);
                        for (int i = 0; i < length; i++) {
                            names.add(friends.getJSONObject(i).getString("name"));
                            ids.add(friends.getJSONObject(i).getString("id"));
                        }

                        ListView friendListView = (ListView) findViewById(R.id.friend_picker_list_view);
                        friendListView.setAdapter(new FriendPickerListAdapter(FriendPickerActivity.this, names, ids));
                        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (groupMembersFacebookIDs.contains(ids.get(position))) {
                                    groupMembersFacebookIDs.remove(ids.get(position));
                                    TextView nameTextView = (TextView) view.findViewById(R.id.friend_picker_facebook_name);
                                    nameTextView.setBackgroundColor(Color.WHITE);
                                    System.out.println(groupMembersFacebookIDs.toString());
                                } else {
                                    groupMembersFacebookIDs.add(ids.get(position));
                                    /*When I named this TextView the same as above there was a bug where selecting another
                                    friend would only change the color of the first one selected.
                                    TODO: Decide whether this implementation is sufficient or whether there's a better one*/
                                    TextView nameTxt = (TextView) view.findViewById(R.id.friend_picker_facebook_name);
                                    nameTxt.setBackgroundColor(Color.BLUE);
                                    System.out.println(groupMembersFacebookIDs.toString());
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        request.executeAsync();
    }
}
