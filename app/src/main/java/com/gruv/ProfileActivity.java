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
import android.view.ViewGroup;
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
import com.gruv.adapters.PostedEventsAdapter;
import com.gruv.adapters.PromotedEventsAdapter;
import com.gruv.interfaces.ClickInterface;
import com.gruv.interfaces.SecondClickInterface;
import com.gruv.models.Author;
import com.gruv.models.Event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ClickInterface, SecondClickInterface {
    private FirebaseAuth authenticateObj;
    private FirebaseUser currentUser;
    private ScrollView scrollView;
    private Toolbar toolbar, profilePicToolbar;
    private ImageView profilePic;
    private CircleImageView profilePicSmall;
    private TextView textFullName, textEventCount, textFollowers, textFollowing, textBio;
    private Author thisUser;
    private RecyclerView recyclerPostedEvents, recyclerPromotedEvents;
    private List<Event> postedEvents = new ArrayList<>(), promotedEvents = new ArrayList<>();
    private ConstraintLayout appBarLayout;
    private EditProfileBottomSheet bottomSheet;
    private ClickInterface postedEventsListener;
    private SecondClickInterface promotedEventsListener;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Event event;
    private ConstraintLayout layoutError;
    private PostedEventsAdapter postedEventsAdapter;
    private PromotedEventsAdapter promotedEventsAdapter;
    private Bundle savedInstanceState;
    private MaterialButton buttonBack, buttonSiteLink, buttonEditProfile;
    private ImageButton buttonMore;
    private FloatingActionButton fabAdd;
    private ImageView imageVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_profile);
        authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();
        if (currentUser != null) {
            setCurrentUser();
        }
//        setUser();
        initialiseControls();
        setTransparentStatusBar();
        setTopPadding(getStatusBarHeight());
        int padding = 0;
//        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (hasBackKey) {
            setBottomPadding(getStatusBarHeight());
        }


        postedEventsListener = this;
        promotedEventsListener = this;

        getAuthor();
        setUserDetails();


        promotedEventsAdapter = new PromotedEventsAdapter(this, promotedEvents, postedEventsListener);
        recyclerPostedEvents.setAdapter(promotedEventsAdapter);
        recyclerPostedEvents.setLayoutManager(new

                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        postedEventsAdapter = new PostedEventsAdapter(this, this, thisUser, postedEvents, promotedEventsListener);
        recyclerPromotedEvents.setAdapter(postedEventsAdapter);
        recyclerPromotedEvents.setLayoutManager(new LinearLayoutManager(this));


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet = new EditProfileBottomSheet(thisUser, ProfileActivity.this);
                bottomSheet.show(getSupportFragmentManager(), "edit profile");
            }
        });
        buttonSiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(thisUser.getSite());
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
                PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, buttonMore);
                popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
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


    }


    private void getAuthor() {
        databaseReference.child("author").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisUser = dataSnapshot.getValue(Author.class);
                thisUser.setId(dataSnapshot.getKey());
                getEvents();
                setUserDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setUserDetails() {
        if (thisUser.getAvatar() != null) {
            loadAndSetPicture(Uri.parse(thisUser.getAvatar()), profilePic);
            loadAndSetPicture(Uri.parse(thisUser.getAvatar()), profilePicSmall);
        } else {
            profilePic.setImageResource(R.drawable.ic_account_circle_white_140dp);
            profilePicSmall.setImageResource(R.drawable.ic_account_circle_white_140dp);
        }
        textFullName.setText(thisUser.getName());
        ViewGroup.LayoutParams params = textFullName.getLayoutParams();
        if (thisUser.getName().length() > 15) {
            params.width = (int) (160 * Resources.getSystem().getDisplayMetrics().density);
            textFullName.setLayoutParams(params);
        } else {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        if (thisUser.isVerified()) {
            imageVerified.setVisibility(View.VISIBLE);
        } else {
            imageVerified.setVisibility(View.GONE);
        }
        try {
            if (thisUser.getBio() != null) {
                textBio.setVisibility(View.VISIBLE);
                textBio.setText(thisUser.getBio());
            } else
                textBio.setVisibility(View.GONE);

            if (thisUser.getSite() != null) {
                {
                    String display = "";
                    buttonSiteLink.setVisibility(View.VISIBLE);

                    display = thisUser.getSite();
                    if (thisUser.getSite().length() >= 8) {
                        if (thisUser.getSite().substring(0, 8).equals("https://"))
                            display = thisUser.getSite().substring(8);
                        else if (thisUser.getSite().substring(0, 7).equals("http://"))
                            display = thisUser.getSite().substring(7);
                        else
                            display = thisUser.getSite();

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

            if (thisUser.getFollowers() != null)
                textFollowers.setText(Integer.toString(thisUser.getFollowers().size()));

            textFollowing.setText(Integer.toString(thisUser.getFollowingCount()));

            if (thisUser.getEvents() != null) {
                textEventCount.setText(thisUser.getEvents().size() + " events");
            }

            checkEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getEvents() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                int count = 0;
                for (DataSnapshot eventDataSnapshot : dataSnapshot.getChildren()) {
                    if (thisUser.getEvents() != null) {
                        for (String postedEvent : thisUser.getEvents()) {
                            if (eventDataSnapshot.getKey().equals(postedEvent)) {
                                event = eventDataSnapshot.getValue(Event.class);
                                event.setEventID(eventDataSnapshot.getKey());
                                if (event.getAuthor().getId().equals(thisUser.getId())) {
                                    event.setAuthor(thisUser);
                                    updateEvent(event.getEventID());
                                }

                                addPost(event);
                            }

                        }
                    }
                    if (thisUser.getPromotedEvents() != null) {
                        for (String promotedEvent : thisUser.getPromotedEvents()) {
                            if (eventDataSnapshot.getKey().equals(promotedEvent)) {
                                event = eventDataSnapshot.getValue(Event.class);
                                event.setEventID(eventDataSnapshot.getKey());
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
                        if (thisUser.getEvents() != null) {
                            for (String postedEvent : thisUser.getEvents()) {
                                if (eventDataSnapshot.getKey().equals(postedEvent)) {
                                    event = eventDataSnapshot.getValue(Event.class);
                                    event.setEventID(eventDataSnapshot.getKey());
                                    addPost(event, Integer.parseInt(event.getEventID()));
                                }

                            }
                        }
                        if (thisUser.getPromotedEvents() != null) {
                            for (String promotedEvent : thisUser.getPromotedEvents()) {
                                if (eventDataSnapshot.getKey().equals(promotedEvent)) {
                                    event = eventDataSnapshot.getValue(Event.class);
                                    event.setEventID(eventDataSnapshot.getKey());
                                    addPromotedPost(event, Integer.parseInt(event.getEventID()));
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
        user.put("author", thisUser);
        databaseReference.child("Event").child(eventId).updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // TODO Add error code
                    showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                }
//               else {
//
//               }
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
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        layoutError = findViewById(R.id.layoutError);
        buttonMore = findViewById(R.id.buttonMore);
        fabAdd = findViewById(R.id.fabAdd);
        imageVerified = findViewById(R.id.imageVerified);



        recyclerPostedEvents = findViewById(R.id.recyclerPostedEvents);
        recyclerPromotedEvents = findViewById(R.id.recyclerPromotedEvents);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        //set static resource
        profilePicToolbar.setTitle(thisUser.getName());
    }

    public void addPost(@NotNull Event event) {
        if (event.getAuthor() != null) {
            postedEvents.add(event);
            Collections.sort(postedEvents, Collections.reverseOrder());
        }
        if (postedEventsAdapter != null) {
            postedEventsAdapter.notifyDataSetChanged();
        }
    }

    public void addPost(@NotNull Event event, int index) {
        if (event.getAuthor() != null) {
            if (postedEvents.isEmpty()) {
                postedEvents.add(event);
            }
            else {
//                if (event.getEventId() != postedEvents.get(postedEvents.size() - 1).getEventId())
                int count = 0;
                for (Event listValue : postedEvents) {
                    if (listValue.getEventID().equals(Integer.toString(index))) {
                        postedEvents.set(count, event);
                        break;
                    }
                    count++;
                }
            }
        }
        if (postedEventsAdapter != null) {
            Collections.sort(postedEvents, Collections.reverseOrder());
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
        Collections.sort(promotedEvents, Collections.reverseOrder());
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
                    if (listValue.getEventID().equals(index)) {
                        promotedEvents.set(count, event);
                        break;
                    }
                    count++;
                }
                Collections.sort(promotedEvents, Collections.reverseOrder());
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
            appBarLayout.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.colorPrimary));

            toolbar.setVisibility(View.GONE);
            profilePicToolbar.setVisibility(View.VISIBLE);
            profilePicSmall.setVisibility(View.VISIBLE);

        } else {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.transparent));
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

    public void onBottomSheetDismiss() {
        this.thisUser = bottomSheet.thisUser;
        setUserDetails();
        for (Event postedEvent : postedEvents) {
            updateEvent(postedEvent.getEventID());
        }
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
