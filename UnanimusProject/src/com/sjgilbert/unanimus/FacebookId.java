package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;

/**
 * 9/1/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class FacebookId implements CharSequence {
    private final String facebookIdString;

    @SuppressWarnings("unused")
    public FacebookId(String facebookIdString) {
        this.facebookIdString = facebookIdString;
    }

    @Override
    public int length() {
        return facebookIdString.length();
    }

    @Override
    public char charAt(int index) {
        return facebookIdString.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return facebookIdString.subSequence(start, end);
    }

    @NonNull
    @Override
    public String toString() {
        return facebookIdString;
    }
}
