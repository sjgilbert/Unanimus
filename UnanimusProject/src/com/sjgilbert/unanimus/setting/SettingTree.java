package com.sjgilbert.unanimus.setting;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by izzi on 7/4/15.
 */

public class SettingTree {
    public static SettingTree settings;

    public SettingTree(JSONObject jsonObject, String profileName) throws JSONException, SettingException {
        root = new SettingEdge(jsonObject, profileName);
    }

    public JSONObject jsonify() throws JSONException {
        return root.jsonify(new JSONObject());
    }

    protected final SettingEdge root;
}
