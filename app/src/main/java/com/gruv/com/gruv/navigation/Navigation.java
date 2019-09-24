package com.gruv.com.gruv.navigation;

import android.view.View;

import com.gruv.LandingActivity;

public class Navigation extends LandingActivity {

    public Navigation() {
    }


    public static void showProgress(int seconds) {
        layoutProgress.setVisibility(View.VISIBLE);
        //TimeUnit.MINUTES.sleep(seconds);
        layoutProgress.setVisibility(View.GONE);
    }
}
