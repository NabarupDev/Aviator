package com.nabarup.avator;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;


public class MyApplication extends Application {
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
        }
    }