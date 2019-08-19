package com.gruv;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final Fragment fragHome;
    final Fragment fragSearch;
    final Fragment fragNotification;
    final Fragment fragProfile;
    final FragmentManager fragManager = getSupportFragmentManager();
    Fragment active;
    DrawerLayout drawer;
    //Toolbar toolbar = (Toolbar)findViewById(R.id.main_app_toolbar);

    public MainActivity() {
        fragHome = new HomeFragment();
        fragSearch = new SearchFragment();
        fragNotification = new NotificationFragment();
        fragProfile = new ProfileFragment();
        active = fragHome;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragManager.beginTransaction().add(R.id.content_main, fragHome, "1").commit();
        fragManager.beginTransaction().add(R.id.content_main, fragProfile, "4").hide(fragProfile).commit();
        fragManager.beginTransaction().add(R.id.content_main, fragNotification, "3").hide(fragNotification).commit();
        fragManager.beginTransaction().add(R.id.content_main, fragSearch, "2").hide(fragSearch).commit();
        //closeDrawer();

      /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"your icon was clicked",Toast.LENGTH_SHORT).show();
            }
        });*/
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragManager.beginTransaction().hide(active).show(fragHome).commit();
                    active = fragHome;
                    return true;
                case R.id.navigation_search:
                    fragManager.beginTransaction().hide(active).show(fragSearch).commit();
                    active = fragSearch;
                    return true;
                case R.id.navigation_notifications:
                    fragManager.beginTransaction().hide(active).show(fragNotification).commit();
                    active = fragNotification;
                    return true;
                case R.id.navigation_profile:
                    fragManager.beginTransaction().hide(active).show(fragProfile).commit();
                    active = fragProfile;
                    return true;
            }
            return false;
        }
    };

  /*  public void closeDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }*/
    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

