package com.gruv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gruv.models.Author;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragManager = getSupportFragmentManager();
    Toolbar toolbar;
    NavigationView drawer;
    DrawerLayout drawerLayout;
    ImageButton buttonDrawer;
    TextView textTitle;
    SearchView searchView;
    CircleImageView imageProfilePicture;
    HomeFragment fragHome = new HomeFragment();
    MessagesFragment fragMessages = new MessagesFragment();
    Fragment fragNotification = new NotificationFragment();
    SearchFragment fragSearch = new SearchFragment();
    Fragment active = fragHome;
    Boolean signedIn = false;
    ListView list;
    Author thisUser = new Author();
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
                        searchBarToggle(false);
                        getSupportActionBar().show();
                    } else {
                        list = findViewById(R.id.listNewsFeed);
                        list.smoothScrollToPosition(fragHome.adapter.getCount());
                    }
                    return true;
                case R.id.navigation_search:
                    if (!active.equals(fragSearch)) {
                        fragManager.beginTransaction().hide(active).show(fragSearch).commit();
                        active = fragSearch;
                        toolbar = findViewById(R.id.searchViewBar);
                        setSupportActionBar(toolbar);
                        searchBarToggle(true);
                        getSupportActionBar().show();
                    } else {
                        searchView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                    return true;
                case R.id.navigation_notifications:
                    fragManager.beginTransaction().hide(active).show(fragNotification).commit();
                    active = fragNotification;
                    toolbar = findViewById(R.id.main_app_toolbar);
                    setSupportActionBar(toolbar);
                    searchBarToggle(false);
                    getSupportActionBar().show();
                    return true;
                case R.id.navigation_messages:
                    fragManager.beginTransaction().hide(active).show(fragMessages).commit();
                    active = fragMessages;
                    toolbar = findViewById(R.id.main_app_toolbar);
                    setSupportActionBar(toolbar);
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
            setCurrentUser();
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
        buttonDrawer = findViewById(R.id.buttonDrawerIcon);
        textTitle = findViewById(R.id.textTitle);
        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        View view = drawer.getHeaderView(0);

        TextView name = view.findViewById(R.id.textUsername);
        TextView descriptor = view.findViewById(R.id.textDescriptor);
        if (currentUser != null) {
            name.setText(thisUser.getName());
            descriptor.setText(thisUser.getEmail());
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


        drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                showSnackBar(menuItem.getTitle() + "", R.id.layoutParent, Snackbar.LENGTH_SHORT);

                switch (menuItem.getTitle().toString()) {
                    case "Profile":
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });

        buttonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    public void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }
    }

    public void addToolbar(Toolbar toelbar) {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toelbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
    }

    public void searchBarToggle(Boolean showSearch) {
        if (showSearch) {
            textTitle.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
        } else {
            textTitle.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
        }

    }

    public void showSnackBar(String message, Integer layout, Integer length) {
        View contextView = findViewById(layout);
        Snackbar.make(contextView, message, length).show();
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
        TextView descriptor = view.findViewById(R.id.textDescriptor);
        imageProfilePicture = view.findViewById(R.id.imageProfilePic);
        drawerLayout.closeDrawer(GravityCompat.START);

        currentUser = authenticateObj.getCurrentUser();
        if (currentUser != null) {
            signedIn = true;
            name.setText(currentUser.getEmail());
            descriptor.setText(currentUser.getDisplayName());
            if (currentUser.getPhotoUrl() != null)
                imageProfilePicture.setImageURI(currentUser.getPhotoUrl());
        } else {
            signedIn = false;
            name.setText("Not signed in");
            Intent myIntent = new Intent(getApplicationContext(), LandingActivity.class);
            startActivity(myIntent);

        }

    }

    public void setCurrentUser() {
        thisUser = new Author(currentUser.getUid(), currentUser.getDisplayName(), null, R.drawable.profile_pic4);
        if (currentUser.getPhotoUrl() != null)
            thisUser.setAvatar(currentUser.getPhotoUrl().toString());
        thisUser.setEmail(currentUser.getEmail());
    }
}


