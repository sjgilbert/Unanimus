package com.parse.starter.setting;

import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;

/**
 * Created by izzi on 7/6/15.
 */

// Enumeration for kinds of setting objects in a tree
enum ESettingType {
    settingsEdge( // Name of class
            "!"), // Key prefix (JSON only!)
    settingsNode("@"),
    settingArray("#");

    // Prefix for JSON object keys
    public String getPrefix() {
        return prefix;
    }

    public boolean hasPrefix(String name) {
        return name.startsWith(prefix);
    }

    ESettingType(String prefix) {
        // All prefixes must conform to prefixLength
        if (prefixLength != prefix.length()) throw new InvalidParameterException();
        this.prefix = prefix;
    }

    // length of prefix
    private final static int prefixLength = 1;

    private final String prefix;
}

