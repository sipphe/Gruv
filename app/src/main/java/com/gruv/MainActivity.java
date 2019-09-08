package com.gruv;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragManager = getSupportFragmentManager();
    Toolbar toolbar;
    NavigationView drawer;
    DrawerLayout drawerLayout;
    HomeFragment fragHome = new HomeFragment();
    MessagesFragment fragMessages = new MessagesFragment();
    Fragment fragNotification = new NotificationFragment();
    SearchFragment fragSearch = new SearchFragment();
    Fragment active = fragHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.main_app_toolbar);
        drawer = findViewById(R.id.nav_view_drawer2);
        drawerLayout = findViewById(R.id.drawerLayout);

        setSupportActionBar(toolbar);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragManager.beginTransaction().add(R.id.content_main, fragHome, "home").commit();
        fragManager.beginTransaction().add(R.id.content_main, fragMessages, "messages").hide(fragMessages).commit();
        fragManager.beginTransaction().add(R.id.content_main, fragNotification, "notifications").hide(fragNotification).commit();
        fragManager.beginTransaction().add(R.id.content_main, fragSearch, "search").hide(fragSearch).commit();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.setVisibility(View.VISIBLE);

            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);


        actionBarDrawerToggle.syncState();
        drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                }
                return false;
            }
        });


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
                case R.id.navigation_messages:
                    fragManager.beginTransaction().hide(active).show(fragMessages).commit();
                    active = fragMessages;
                    return true;
            }
            return false;
        }
    };

    public void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}

