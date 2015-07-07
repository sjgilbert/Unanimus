package com.parse.starter.setting;

/**
 * Created by izzi on 7/5/15.
 */
public class SettingException extends Exception {
    public SettingException() {
        super();
    }

    public SettingException(String message) {
        super(message);
    }


    public SettingException(Exception innerException) {
        super(innerException);
    }

    public SettingException(String message, Exception innerException) {
        super(message, innerException);
    }
}
