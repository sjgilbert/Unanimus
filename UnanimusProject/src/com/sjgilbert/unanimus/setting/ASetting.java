package com.sjgilbert.unanimus.setting;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by izzi on 7/5/15.
 */

/**
 * WARNING: All subclasses must have the signature `public T(JSONObject o, String n)`
 * All constructors must construct an object from `o.get[T](n);`
 */

// Setting abstract.  Subclasses are SettingNode, SettingArray, and SettingEdge.
public abstract class ASetting {
    // All constructures must call super, because name is private and final.
    public ASetting(JSONObject parent, String name, ESettingType settingType) throws SettingException, JSONException {
        if (!parent.has(name)) // Setting represents a value off a JSON object with the name
            throw new SettingException(new JSONException("`parent` must have `name`"));
        // settingType must match prefix, see ESettingType
        if (!settingType.hasPrefix(name)) throw new IllegalArgumentException();
        // name as displayed to the user, and used internally
        this.name = name.substring(settingType.getPrefix().length());
        this.settingType = settingType;
    }

    public abstract JSONObject jsonify(JSONObject parent) throws JSONException;

    public JSONObject jsonify(JSONObject parent, Object put) throws JSONException {
        return parent.put(settingType.getPrefix() + this.name, put);
    }

    public String getName() {
        return this.name;
    }

    private final String name;
    private final ESettingType settingType;
}
