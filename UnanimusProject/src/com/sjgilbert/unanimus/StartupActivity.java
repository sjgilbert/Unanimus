package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParseUser;
import com.sjgilbert.unanimus.setting.SettingException;
import com.sjgilbert.unanimus.setting.SettingTree;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * This activity is the activity specified to begin on startup in AndroidManifest.xml.
 * It checks for a stored user, and if there isn't one, sends them to log-in/register.
 */
public class StartupActivity extends Activity {

    public StartupActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (loadSettings()) Toast.makeText(
                StartupActivity.this,
                getResources().getString(R.string.setting_load_error),
                Toast.LENGTH_LONG).show();

        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, IntroPageActivity.class));
        }
    }

    private boolean loadSettings() {
        InputStream inputStream = getResources().openRawResource(R.raw.default_settings);
        InputStreamReader inputStreamReader = null;

        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return true;
        }

        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while (null != (line = reader.readLine())) stringBuilder.append(line);
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }

        String profileName = getResources().getString(R.string.default_profile_name);

        String profilePath;
        try {
            profilePath = jsonObject.getString(profileName);
        } catch (JSONException e) {
            e.printStackTrace();
            profilePath = getResources().getString(R.string.default_profile_path);
        }

        try {
            SettingTree.settings = new SettingTree(jsonObject, profilePath);
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        } catch (SettingException e) {
            e.printStackTrace();
            Toast.makeText(StartupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }
}
