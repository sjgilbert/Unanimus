package com.sjgilbert.unanimus;

/**
 * 9/1/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class FacebookId implements CharSequence {
    private final String facebookIdString;

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

    @Override
    public String toString() {
        return facebookIdString;
    }
}
