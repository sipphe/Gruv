package com.gruv;

import android.app.Application;

public class Base extends Application {

    private static MainActivity activity;

    public static MainActivity getMainActivity() {
        return activity;
    }

    public static void setMainActivity(MainActivity activityP) {
        activity = activityP;
    }

}
