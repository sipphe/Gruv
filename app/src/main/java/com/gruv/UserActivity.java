package com.gruv;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gruv.com.gruv.PostedEventsAdapter;
import com.gruv.com.gruv.PromotedEventsAdapter;
import com.gruv.interfaces.ClickInterface;
import com.gruv.interfaces.SecondClickInterface;
import com.gruv.models.Author;
import com.gruv.models.Event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity implements ClickInterface, SecondClickInterface {
    private FirebaseAuth authenticateObj;
    private FirebaseUser currentUser;
    private ScrollView scrollView;
    private Toolbar toolbar, profilePicToolbar;
    private ImageView profilePic;
    private CircleImageView profilePicSmall;
    private TextView textFullName, textEventCount, textFollowers, textFollowing, textBio;
    private Author thisUser, selectedUser;
    private RecyclerView recyclerPostedEvents, recyclerPromotedEvents;
    private List<Event> postedEvents = new ArrayList<>(), promotedEvents = new ArrayList<>();
    private ConstraintLayout appBarLayout;
    private ClickInterface postedEventsListener;
    private SecondClickInterface promotedEventsListener;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Event event;
    private ConstraintLayout layoutError;
    private PostedEventsAdapter postedEventsAdapter;
    private PromotedEventsAdapter promotedEventsAdapter;
    private MaterialButton buttonBack, buttonSiteLink, buttonFollow, buttonUnfollow;
    private ImageButton buttonMore;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();
        if (currentUser != null) {
            setCurrentUser();
        }

        selectedUser = (Author) getIntent().getSerializableExtra("selectedUser");

//        setUser();
        initialiseControls();
        setTransparentStatusBar();
        setTopPadding(getStatusBarHeight());
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (hasBackKey) {
            // setBottomPadding(getStatusBarHeight());
        }


        postedEventsListener = this;
        promotedEventsListener = this;

        getAuthors();
        setSelectedUserDetails();


        promotedEventsAdapter = new PromotedEventsAdapter(this, promotedEvents, postedEventsListener);
        recyclerPostedEvents.setAdapter(promotedEventsAdapter);
        recyclerPostedEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        postedEventsAdapter = new PostedEventsAdapter(this, this, thisUser, postedEvents, promotedEventsListener);
        recyclerPromotedEvents.setAdapter(postedEventsAdapter);
        recyclerPromotedEvents.setLayoutManager(new LinearLayoutManager(this));


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonSiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(selectedUser.getSite());
            }
        });
        scrollView.getViewTreeObserver().

                addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        collapseToolbar();
                    }
                });

        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(UserActivity.this, buttonMore);
                popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());
                popupMenu.setGravity(Gravity.RIGHT);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //TODO Add menu code
                        showSnackBar(item.toString(), Snackbar.LENGTH_LONG);
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFollowingFollower();
            }
        });

        buttonUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (thisUser.getFollowing() != null) {
                        for (String following : thisUser.getFollowing())
                            if (following.equals(selectedUser.getId())) {
                                removeFollowingFollower();
                            }
                    }
                } catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addFollowingFollower() {
        List<String> following;
        if (thisUser.getFollowing() != null) {
            following = thisUser.getFollowing();
            following.add(selectedUser.getId());
            thisUser.setFollowing(following);
        }
        Map<String, Object> user = new HashMap<>();
        user.put(thisUser.getId(), thisUser);
        databaseReference.child("author").updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // TODO Add error code
                    showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                } else {
                    showSnackBar("Followed " + selectedUser.getName(), Snackbar.LENGTH_LONG);
                }
                setSelectedUserDetails();
            }
        });

        if (selectedUser.getFollowers() != null) {
            List<String> followers = selectedUser.getFollowers();
            followers.add(thisUser.getId());
            selectedUser.setFollowers(followers);
        }
        user = new HashMap<>();
        user.put(selectedUser.getId(), selectedUser);
        databaseReference.child("author").updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // TODO Add error code
                    showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                }
                setSelectedUserDetails();
            }
        });
    }

    private void removeFollowingFollower() {
        if (thisUser.getFollowing() != null) {
            List<String> following = thisUser.getFollowing();
            following.remove(selectedUser.getId());
            thisUser.setFollowing(following);
        }
        Map<String, Object> user = new HashMap<>();
        user.put(thisUser.getId(), thisUser);
        databaseReference.child("author").updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // TODO Add error code
                    showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                } else {
                    showSnackBar("Unfollowed " + selectedUser.getName(), Snackbar.LENGTH_LONG);
                }
                setSelectedUserDetails();

            }
        });

        if (selectedUser.getFollowers() != null) {
            List<String> followers = selectedUser.getFollowers();
            followers.remove(thisUser.getId());
            selectedUser.setFollowers(followers);
        }
        user = new HashMap<>();
        user.put(selectedUser.getId(), selectedUser);
        databaseReference.child("author").updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // TODO Add error code
                    showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                }
                setSelectedUserDetails();

            }
        });
    }


    private void getAuthors() {
        databaseReference.child("author").child(thisUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisUser = dataSnapshot.getValue(Author.class);
                thisUser.setId(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        databaseReference.child("author").child(selectedUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedUser = dataSnapshot.getValue(Author.class);
                selectedUser.setId(dataSnapshot.getKey());
                getEvents();
                setSelectedUserDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setSelectedUserDetails() {
        if (thisUser.getFollowing() != null) {
            for (String following : thisUser.getFollowing()) {
                if (following.equals(selectedUser.getId())) {
                    buttonFollow.setVisibility(View.GONE);
                    buttonUnfollow.setVisibility(View.VISIBLE);
                } else {
                    buttonFollow.setVisibility(View.VISIBLE);
                    buttonUnfollow.setVisibility(View.GONE);
                }
            }
        }
        if (selectedUser.getAvatar() != null) {
            loadAndSetPicture(Uri.parse(selectedUser.getAvatar()), profilePic);
            loadAndSetPicture(Uri.parse(selectedUser.getAvatar()), profilePicSmall);
        } else {
            profilePic.setImageResource(R.drawable.ic_account_circle_white_140dp);
            profilePicSmall.setImageResource(R.drawable.ic_account_circle_white_140dp);
        }
        textFullName.setText(selectedUser.getName());

        try {
            if (selectedUser.getBio() != null) {
                textBio.setVisibility(View.VISIBLE);
                textBio.setText(selectedUser.getBio());
            } else
                textBio.setVisibility(View.GONE);

            if (selectedUser.getSite() != null) {
                {
                    buttonSiteLink.setVisibility(View.VISIBLE);
                    String display = selectedUser.getSite();
                    if (selectedUser.getSite().length() >= 8) {
                        if (selectedUser.getSite().substring(0, 8).equals("https://"))
                            display = selectedUser.getSite().substring(8);
                        else if (selectedUser.getSite().substring(0, 7).equals("http://"))
                            display = selectedUser.getSite().substring(7);
                        else
                            display = selectedUser.getSite();

                    }

                    if (display.length() >= 4) {
                        if (display.substring(0, 4).equals("www."))
                            display = display.substring(4);
                    }

                    if (display.length() < 20)
                        buttonSiteLink.setText(display);
                    else
                        buttonSiteLink.setText(display.substring(0, 20) + "...");
                    if (display.equals(""))
                        buttonSiteLink.setVisibility(View.GONE);
                }
            } else
                buttonSiteLink.setVisibility(View.GONE);

            if (selectedUser.getFollowers() != null)
                textFollowers.setText(Integer.toString(selectedUser.getFollowers().size()));

            textFollowing.setText(Integer.toString(selectedUser.getFollowingCount()));

            if (selectedUser.getEvents() != null) {
                textEventCount.setText(selectedUser.getEvents().size() + " events");
            }

            checkEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setUserDetails() {

    }

    public void getEvents() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                int count = 0;
                for (DataSnapshot eventDataSnapshot : dataSnapshot.getChildren()) {
                    if (selectedUser.getEvents() != null) {
                        for (String postedEvent : selectedUser.getEvents()) {
                            if (eventDataSnapshot.getKey().equals(postedEvent)) {
                                event = eventDataSnapshot.getValue(Event.class);
                                event.setEventId(eventDataSnapshot.getKey());
                                if (event.getAuthor().getId().equals(selectedUser.getId())) {
                                    event.setAuthor(selectedUser);
                                    updateEvent(event.getEventId());
                                }

                                addPost(event);
                            }

                        }
                    }
                    if (selectedUser.getPromotedEvents() != null) {
                        for (String promotedEvent : selectedUser.getPromotedEvents()) {
                            if (eventDataSnapshot.getKey().equals(promotedEvent)) {
                                event = eventDataSnapshot.getValue(Event.class);
                                event.setEventId(eventDataSnapshot.getKey());
                                addPromotedPost(event);
                            }
                        }
                        count++;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                {

                    int count = 0;
                    for (DataSnapshot eventDataSnapshot : dataSnapshot.getChildren()) {
                        if (selectedUser.getEvents() != null) {
                            for (String postedEvent : selectedUser.getEvents()) {
                                if (eventDataSnapshot.getKey().equals(postedEvent)) {
                                    event = eventDataSnapshot.getValue(Event.class);
                                    event.setEventId(eventDataSnapshot.getKey());
                                    addPost(event, Integer.parseInt(event.getEventId()));
                                }

                            }
                        }
                        if (thisUser.getPromotedEvents() != null) {
                            for (String promotedEvent : thisUser.getPromotedEvents()) {
                                if (eventDataSnapshot.getKey().equals(promotedEvent)) {
                                    event = eventDataSnapshot.getValue(Event.class);
                                    event.setEventId(eventDataSnapshot.getKey());
                                    addPromotedPost(event, Integer.parseInt(event.getEventId()));
                                }
                            }
                            count++;
                        }
                    }
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

            }
        });

    }

    private void updateEvent(String eventId) {
        Map<String, Object> user = new HashMap<>();
        user.put("author", selectedUser);
        databaseReference.child("Event").child(eventId).updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // TODO Add error code
                    showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                }
            }
        });
    }

    public void showSnackBar(String message, Integer length) {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, length).show();
    }

    private void initialiseControls() {
        profilePic = findViewById(R.id.imageViewPostPicture);
        textFullName = findViewById(R.id.textName);
        textEventCount = findViewById(R.id.textEventCount);
        buttonBack = findViewById(R.id.buttonBack);
        scrollView = findViewById(R.id.scrollViewProfile);
        toolbar = findViewById(R.id.main_app_toolbar);
        profilePicToolbar = findViewById(R.id.profilePicToolbar);
        profilePicSmall = findViewById(R.id.imageSmallProfilePic);
        textBio = findViewById(R.id.textBio);
        textFollowers = findViewById(R.id.textViewFollwersCount);
        textFollowing = findViewById(R.id.textViewFollowingCount);
        buttonSiteLink = findViewById(R.id.buttonSiteLink);
        buttonFollow = findViewById(R.id.buttonFollow);
        buttonUnfollow = findViewById(R.id.butttonUnfollow);
        layoutError = findViewById(R.id.layoutError);
        buttonMore = findViewById(R.id.buttonMore);
        fabAdd = findViewById(R.id.fabAdd);


        recyclerPostedEvents = findViewById(R.id.recyclerPostedEvents);
        recyclerPromotedEvents = findViewById(R.id.recyclerPromotedEvents);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        //set static resource
        profilePicToolbar.setTitle(selectedUser.getName());
    }

    public void addPost(@NotNull Event event) {
        if (event.getAuthor() != null)
            postedEvents.add(event);
        if (postedEventsAdapter != null) {
            postedEventsAdapter.notifyDataSetChanged();
        }
    }

    public void addPost(@NotNull Event event, int index) {
        if (event.getAuthor() != null) {
            if (postedEvents.isEmpty())
                postedEvents.add(event);
            else {
//                if (event.getEventId() != postedEvents.get(postedEvents.size() - 1).getEventId())
                int count = 0;
                for (Event listValue : postedEvents) {
                    if (listValue.getEventId().equals(Integer.toString(index))) {
                        postedEvents.set(count, event);
                        break;
                    }
                    count++;
                }
            }
        }
        if (postedEventsAdapter != null) {
            postedEventsAdapter.notifyDataSetChanged();
        }
//        if (event.getAuthor() != null)
//            postedEvents.set(index, event);
//        if (postedEventsAdapter != null) {
//            postedEventsAdapter.notifyDataSetChanged();
//        }
    }

    public void addPromotedPost(@NotNull Event event) {
        if (event.getAuthor() != null)
            promotedEvents.add(event);
        if (promotedEventsAdapter != null) {
            promotedEventsAdapter.notifyDataSetChanged();
        }
    }

    public void addPromotedPost(@NotNull Event event, int index) {
        if (event.getAuthor() != null) {
            if (promotedEvents.isEmpty())
                promotedEvents.add(event);
            else {
//                if (event.getEventId() != postedEvents.get(postedEvents.size() - 1).getEventId())
                int count = 0;
                for (Event listValue : promotedEvents) {
                    if (listValue.getEventId().equals(index)) {
                        promotedEvents.set(count, event);
                        break;
                    }
                    count++;
                }
            }
        }
        if (promotedEventsAdapter != null) {
            promotedEventsAdapter.notifyDataSetChanged();
        }
//        if (event.getAuthor() != null)
//            promotedEvents.set(index, event);
//        if (promotedEventsAdapter != null) {
//            promotedEventsAdapter.notifyDataSetChanged();
//        }
    }

    public void checkEvents() {
        if (thisUser.getEvents() == null || thisUser.getEvents().isEmpty()) {
            layoutError.setVisibility(View.VISIBLE);
        } else {
            layoutError.setVisibility(View.GONE);
        }
    }

    private void setUser() {
        thisUser = new Author(currentUser.getUid(), "This User", null, R.drawable.profile_pic5);
        thisUser.setEmail(currentUser.getEmail());
    }

    public void setTransparentStatusBar() {
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setTopPadding(int topPadding) {
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.setPadding(0, topPadding, 0, 0);
    }

    public void setBottomPadding(int bottomPadding) {
        ScrollView scrollView = findViewById(R.id.scrollViewProfile);
        final float scale = getResources().getDisplayMetrics().density;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fabAdd.getLayoutParams();
        params.setMargins(0, 0, (int) (32 * scale + 0.5f), (int) (bottomPadding + (32 * scale + 0.5f)));
        fabAdd.setLayoutParams(params);
        scrollView.setPadding(0, 0, 0, bottomPadding);
    }

    public void recyclerViewOnClick(int position) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("Event", promotedEvents.get(position));
        startActivity(intent);
    }

    public void recyclerViewPostOnClick(int position) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("Event", postedEvents.get(position));
        startActivity(intent);
    }

    private void collapseToolbar() {
        int scrollY = scrollView.getScrollY();
        if (scrollY > 544) {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(UserActivity.this, R.color.colorPrimary));

            toolbar.setVisibility(View.GONE);
            profilePicToolbar.setVisibility(View.VISIBLE);
            profilePicSmall.setVisibility(View.VISIBLE);

        } else {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(UserActivity.this, R.color.transparent));
            toolbar.setVisibility(View.VISIBLE);
            profilePicToolbar.setVisibility(View.GONE);
            profilePicSmall.setVisibility(View.GONE);
        }
    }

    public void setCurrentUser() {
        thisUser = new Author(currentUser.getUid(), currentUser.getDisplayName(), null, R.drawable.profile_pic4);
        if (currentUser.getPhotoUrl() != null)
            thisUser.setAvatar(currentUser.getPhotoUrl().toString());
        thisUser.setEmail(currentUser.getEmail());
    }

    public void loadAndSetPicture(Uri uri, ImageView imageView) {
        Glide.with(this).load(uri).centerCrop().into(imageView);
    }

    public void openLink(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }
}
