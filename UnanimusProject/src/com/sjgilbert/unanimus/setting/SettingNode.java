package com.sjgilbert.unanimus.setting;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by izzi on 7/5/15.
 */
public class SettingNode<T> extends ASetting {
    public SettingNode(JSONObject parent, String name) throws JSONException, SettingException {
        super(parent, name, settingType);
        this.value = (T) parent.get(name);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public JSONObject jsonify(JSONObject parent) throws JSONException {
        return super.jsonify(parent, value);
    }

    private T value;

    private final static ESettingType settingType = ESettingType.settingsNode;
}
