package com.gruv;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    Boolean signedIn = false;
    ListView list;
    private FirebaseAuth authenticateObj;
    private FirebaseUser currentUser;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (!active.equals(fragHome)) {
                        fragManager.beginTransaction().hide(active).show(fragHome).commit();
                        active = fragHome;
                        toolbar = findViewById(R.id.main_app_toolbar);
                        setSupportActionBar(toolbar);
                        addToolbar(toolbar);
                        getSupportActionBar().show();
                    } else {
                        list = findViewById(R.id.listNewsFeed);
                        list.smoothScrollToPosition(fragHome.adapter.getCount());
                    }
                    return true;
                case R.id.navigation_search:
                    fragManager.beginTransaction().hide(active).show(fragSearch).commit();
                    active = fragSearch;
                    toolbar = findViewById(R.id.searchViewBar);
                    setSupportActionBar(toolbar);
                    addToolbar(toolbar);
                    return true;
                case R.id.navigation_notifications:
                    fragManager.beginTransaction().hide(active).show(fragNotification).commit();
                    active = fragNotification;
                    toolbar = findViewById(R.id.main_app_toolbar);
                    setSupportActionBar(toolbar);
                    addToolbar(toolbar);
                    getSupportActionBar().show();
                    return true;
                case R.id.navigation_messages:
                    fragManager.beginTransaction().hide(active).show(fragMessages).commit();
                    active = fragMessages;
                    toolbar = findViewById(R.id.main_app_toolbar);
                    setSupportActionBar(toolbar);
                    addToolbar(toolbar);
                    getSupportActionBar().show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();
        if (currentUser != null) {
            signedIn = true;
        }

        if (!signedIn) {
            Intent myIntent = new Intent(this, LandingActivity.class);
            startActivity(myIntent);
            if (currentUser != null) {
                signedIn = true;
            }

        }

        list = findViewById(R.id.listNewsFeed);
        toolbar = findViewById(R.id.main_app_toolbar);
        drawer = findViewById(R.id.nav_view_drawer2);
        drawerLayout = findViewById(R.id.drawerLayout);
        View view = drawer.getHeaderView(0);

        TextView name = view.findViewById(R.id.textUsername);
        if (currentUser != null) {
            name.setText(currentUser.getEmail());
        }

        name.setClickable(true);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateObj.signOut();
                Intent myIntent = new Intent(getApplicationContext(), LandingActivity.class);
                startActivity(myIntent);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

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

    public void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }
    }

    public void addToolbar(Toolbar toelbar) {
        ActionBarDrawerToggle actionBarDrawerToggl = new ActionBarDrawerToggle(this, drawerLayout, toelbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggl);
        actionBarDrawerToggl.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = drawer.getHeaderView(0);

        TextView name = view.findViewById(R.id.textUsername);
        drawerLayout.closeDrawer(Gravity.LEFT);

        currentUser = authenticateObj.getCurrentUser();
        if (currentUser != null) {
            signedIn = true;
            name.setText(currentUser.getEmail());
        } else {
            signedIn = false;
            name.setText("Not signed in");
            Intent myIntent = new Intent(getApplicationContext(), LandingActivity.class);
            startActivity(myIntent);

        }

    }
}

