package com.example.movelocationmarker;

import android.app.Application;

/**
 * <br>
 * <p>
 * <p>
 * com.example.movelocationmarker
 * <p>
 * FUXI
 */
public class MyApplication extends Application {
     private static MyApplication sIntance;

    @Override
    public void onCreate() {
        super.onCreate();
        sIntance = this;
    }

    public static MyApplication getIntance() {
        return sIntance;
    }
}
