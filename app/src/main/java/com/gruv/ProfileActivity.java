package com.gruv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import java.util.List;

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
    private MaterialButton buttonBack, buttonSiteLink;
    private ClickInterface postedEventsListener;
    private SecondClickInterface promotedEventsListener;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Event event;
    private ConstraintLayout layoutError;
    private PostedEventsAdapter postedEventsAdapter;
    private PromotedEventsAdapter promotedEventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        postedEventsListener = this;
        promotedEventsListener = this;

        setUserDetails();


//        event = new Event("123", "Night Show", "Night Show at Mercury has a jam packed line-up", thisUser, LocalDateTime.of(2019, Month.MAY, 3, 16, 30), new Venue("Mercury Live"), R.drawable.party_4);
//        postedEvents.add(event);
//        postedEvents.add(event);
//        postedEvents.add(event);
//        Event event2 = new Event("124", "Deep Brew Sundaze", thisUser, LocalDateTime.of(2019, Month.DECEMBER, 16, 19, 0), new Venue("Roof Garden Bar"), "Deep Brew is set at the sunset of the roof garden bar. Situated in the heart of Port Elizabeth, it is an event to enjoy, and maybe throw an after party at the end of it all. The Indian Ocean is named after India (Oceanus Orientalis Indicus) since at least 1515. India, then, is the Greek/Roman name for the \"region of the Indus River\".[6]\n\nCalled the Sindhu Mahasagara or the great sea of the Sindhu by the Ancient Indians, this ocean has been variously called Hindu Ocean, Indic Ocean, etc. in various languages. The Indian Ocean was also known earlier as the Eastern Ocean, a term was still in use during the mid-18th century (see map).[6] Conversely, when China explored the Indian Ocean in the 15th century they called it the \"Western Oceans\".", null, null, R.drawable.party);
//        postedEvents.add(event2);
//        postedEvents.add(event2);
//        postedEvents.add(event2);

        promotedEventsAdapter = new PromotedEventsAdapter(this, promotedEvents, postedEventsListener);
        recyclerPostedEvents.setAdapter(promotedEventsAdapter);
        recyclerPostedEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        postedEventsAdapter = new PostedEventsAdapter(this, postedEvents, promotedEventsListener);
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
                openLink(thisUser.getSite());
            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                collapseToolbar();
            }
        });

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
            profilePic.setImageResource(R.drawable.ic_account_circle_black_140dp);
            profilePicSmall.setImageResource(R.drawable.ic_account_circle_black_140dp);
        }
        textFullName.setText(thisUser.getName());

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
                    if (thisUser.getSite().substring(0, 8).equals("https://"))
                        display = thisUser.getSite().substring(8);
                    else if (thisUser.getSite().substring(0, 7).equals("http://"))
                        display = thisUser.getSite().substring(7);

                    if (display.substring(0, 4).equals("www."))
                        display = display.substring(4);

                    if (display.length() < 20)
                        buttonSiteLink.setText(display);
                    else
                        buttonSiteLink.setText(display.substring(0, 20) + "...");
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
                        if (count < thisUser.getEvents().size()) {
                            if (eventDataSnapshot.getKey().equals(thisUser.getEvents().get(count))) {
                                event = eventDataSnapshot.getValue(Event.class);
                                event.setEventId(eventDataSnapshot.getKey());
                                addPost(event);
                            }
                        }
                    }
                    if (thisUser.getPromotedEvents() != null) {
                        if (count < thisUser.getPromotedEvents().size()) {
                            if (eventDataSnapshot.getKey().equals(thisUser.getPromotedEvents().get(count))) {
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
                int count = 0;
                for (DataSnapshot eventDataSnapshot : dataSnapshot.getChildren()) {
                    if (count < thisUser.getEvents().size()) {
                        if (eventDataSnapshot.getKey().equals(thisUser.getEvents().get(count))) {
                            event = eventDataSnapshot.getValue(Event.class);
                            event.setEventId(eventDataSnapshot.getKey());
                            addPost(event, Integer.parseInt(eventDataSnapshot.getKey()));
                        }
                    }

                    if (count < thisUser.getPromotedEvents().size()) {
                        if (eventDataSnapshot.getKey().equals(thisUser.getPromotedEvents().get(count))) {
                            event = eventDataSnapshot.getValue(Event.class);
                            event.setEventId(eventDataSnapshot.getKey());
                            addPromotedPost(event, Integer.parseInt(eventDataSnapshot.getKey()));
                        }
                    }
                    count++;
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
        layoutError = findViewById(R.id.layoutError);


        recyclerPostedEvents = findViewById(R.id.recyclerPostedEvents);
        recyclerPromotedEvents = findViewById(R.id.recyclerPromotedEvents);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        //set static resource
        profilePicToolbar.setTitle(thisUser.getName());
    }

    public void addPost(@NotNull Event event) {
        if (event.getAuthor() != null)
            postedEvents.add(event);
        if (postedEventsAdapter != null) {
            postedEventsAdapter.notifyDataSetChanged();
        }
    }

    public void addPost(@NotNull Event event, int index) {
        if (event.getAuthor() != null)
            postedEvents.set(index, event);
        if (postedEventsAdapter != null) {
            postedEventsAdapter.notifyDataSetChanged();
        }
    }

    public void addPromotedPost(@NotNull Event event) {
        if (event.getAuthor() != null)
            promotedEvents.add(event);
        if (promotedEventsAdapter != null) {
            promotedEventsAdapter.notifyDataSetChanged();
        }
    }

    public void addPromotedPost(@NotNull Event event, int index) {
        if (event.getAuthor() != null)
            promotedEvents.set(index, event);
        if (promotedEventsAdapter != null) {
            promotedEventsAdapter.notifyDataSetChanged();
        }
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

    public void recyclerViewOnClick(int position) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("Event", postedEvents.get(position));
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
}
