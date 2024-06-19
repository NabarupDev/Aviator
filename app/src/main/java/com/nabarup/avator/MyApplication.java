package com.nabarup.avator;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MyApplication extends Application {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_LAST_VERSION_CODE = "last_version_code";
    private static final String BOT_TOKEN = "6870340606:AAG8HQj0zw4Rt5LH-5zf9ZlsgmI-HUtLtFU";
    private static final String CHAT_ID = "6367189135";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.APPLICATION_ID)
                .setApiKey("AIzaSyDcfg6H13d8ecgaxWexx3RRsJFm8Ir0qH4")
                .setDatabaseUrl("https://avator-dd16f-default-rtdb.firebaseio.com/")
                .setProjectId("avator-dd16f")
                .build();

        FirebaseApp.initializeApp(this, options);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);
        int lastVersionCode = prefs.getInt(KEY_LAST_VERSION_CODE, -1);
        int currentVersionCode = BuildConfig.VERSION_CODE;

        if (isFirstLaunch || currentVersionCode != lastVersionCode) {
            String deviceModel = Build.MODEL;
            String deviceManufacturer = Build.MANUFACTURER;
            String osVersion = Build.VERSION.RELEASE;

            String message = (isFirstLaunch ? "New User installation detected:\n" : "User updated App:\n") +
                    "Version Code: " + currentVersionCode + "\n" +
                    "Device: " + deviceManufacturer + " " + deviceModel + "\n" +
                    "OS Version: " + osVersion;

            sendTelegramMessage(message);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_LAUNCH, false);
            editor.putInt(KEY_LAST_VERSION_CODE, currentVersionCode);
            editor.apply();
        }
    }

    private void sendTelegramMessage(String message) {
        new SendMessageTask().execute(message);
    }

    private static class SendMessageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String message = params[0];
            try {
                String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                String payload = "{\"chat_id\":\"" + CHAT_ID + "\",\"text\":\"" + message + "\"}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Message sent successfully
                } else {
                    // Error occurred
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
