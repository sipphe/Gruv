package com.gruv;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gruv.com.gruv.NewsFeedAdapter;
import com.gruv.interfaces.ClickInterface;
import com.gruv.models.Author;
import com.gruv.models.Event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ClickInterface {
    private FirebaseAuth authenticateObj;
    private FirebaseUser currentUser;
    public NewsFeedAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    ExtendedFloatingActionButton fab;
    private ClickInterface clickInterface;
    private RecyclerView feed;
    private LinearLayout layoutProgress;
    private LinearLayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Event event;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RelativeLayout parentLayout, layoutError;
    private Author thisUser;
    View view;
    private boolean viewCreated = false;
    Bundle savedInstance;
    private List<String> followingEventIds;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.view = view;
        this.savedInstance = savedInstanceState;
        initialiseControls();
        setCurrentUser();
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.app_name, R.string.app_name);

        initialiseAdapter();


        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fab.setVisibility(View.GONE);
                return false;
            }
        });


        if (!connectionAvailable()) {
            showSnackBar("Please check your internet connection", R.id.frame_home_fragment, Snackbar.LENGTH_LONG);
        }
        showProgress();

        if (thisUser != null)
            getAuthor();


        //viewCreated = true;
    }

    private void getAuthor() {
        eventList.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        databaseReference.child("author").child(thisUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    thisUser = dataSnapshot.getValue(Author.class);
                    thisUser.setId(dataSnapshot.getKey());
                    getFollowingsEventIds();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getEvents() {
        if (followingEventIds != null) {
            for (String eventId : followingEventIds) {
                databaseReference.child("Event").addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.getKey().equals(eventId)) {
                            event = dataSnapshot.getValue(Event.class);
                            event.setEventId(dataSnapshot.getKey());
                            addPost(event);
                            checkEvents();
                            hideProgress();
                        }
                    }


                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.getKey().equals(eventId)) {
                            event = dataSnapshot.getValue(Event.class);
                            event.setEventId(dataSnapshot.getKey());
                            addPost(event, Integer.parseInt(eventId));
                            checkEvents();
                            hideProgress();
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        checkEvents();
    }


    public void checkEvents() {
        if (eventList == null || eventList.isEmpty()) {
            hideProgress();
            layoutError.setVisibility(View.VISIBLE);
        } else {
            layoutError.setVisibility(View.GONE);
        }

    }

    public void getFollowingsEventIds() {

        if (thisUser.getFollowing() != null) {
            for (String following : thisUser.getFollowing()) {
                databaseReference.child("author").child(following).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        followingEventIds = (List<String>) dataSnapshot.getValue();
                        getEvents();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void initialiseAdapter() {
        eventList.clear();
        adapter = new NewsFeedAdapter(getActivity(), eventList, clickInterface, thisUser);
        feed.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        feed.setLayoutManager(layoutManager);
    }

    public void recyclerViewOnClick(int position) {
        fab.setVisibility(View.GONE);
        Intent intent = new Intent(getActivity(), PostActivity.class);
        intent.putExtra("Event", eventList.get(position));
        startActivity(intent);
    }

    public void addPost(@NotNull Event event) {
        if (event.getAuthor() != null) {
            if (eventList.isEmpty())
                eventList.add(event);
            else {
                if (event.getEventId() != eventList.get(eventList.size() - 1).getEventId())
                    eventList.add(event);
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void addPost(@NotNull Event event, int index) {
        if (event.getAuthor() != null)
            eventList.set(index, event);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void addPostToFeed(View v) {
        // save index and top position
        int index = layoutManager.findFirstVisibleItemPosition();
        v = feed.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - feed.getPaddingTop());

//        getEvent();

        // restore index and position
        layoutManager.scrollToPositionWithOffset(index, top);
    }

    @Override
    public void onResume() {
        super.onResume();
        onViewCreated(view, savedInstance);
        if (!connectionAvailable()) {
            showSnackBar("Please check your internet connection", R.id.frame_home_fragment, Snackbar.LENGTH_LONG);
        }
    }

    public void initialiseControls() {
        authenticateObj = FirebaseAuth.getInstance();
        parentLayout = getActivity().findViewById(R.id.frame_home_fragment);
        currentUser = authenticateObj.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        toolbar = getActivity().findViewById(R.id.main_app_toolbar);
        drawerLayout = getActivity().findViewById(R.id.drawerLayout);
        clickInterface = this;
        layoutProgress = getActivity().findViewById(R.id.layout_progress);
        fab = getActivity().findViewById(R.id.floatingActionButton);
        feed = getActivity().findViewById(R.id.listNewsFeed);
        layoutError = getActivity().findViewById(R.id.layoutError);
    }

    public void startPostActivity(int position) {

        Intent post = new Intent(getActivity(), PostActivity.class);
        post.putExtra("Event", eventList.get(position));
        startActivity(post);
    }

    public void scrollToTop() {
    }

    public void setCurrentUser() {
        if (currentUser != null) {
            thisUser = new Author(currentUser.getUid(), currentUser.getDisplayName(), null, R.drawable.profile_pic4);
            if (currentUser.getPhotoUrl() != null)
                thisUser.setAvatar(currentUser.getPhotoUrl().toString());
            thisUser.setEmail(currentUser.getEmail());
        }
    }

    public void getEvent() {


    }

    private boolean connectionAvailable() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                connected = true;
            } else {
                connected = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return connected;
    }

    public void showSnackBar(String message, Integer layout, Integer length) {
        View contextView = getActivity().findViewById(layout);
        Snackbar.make(contextView, message, length).show();
    }

    public void showProgress() {
        layoutProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        layoutProgress.setVisibility(View.GONE);
    }


}

