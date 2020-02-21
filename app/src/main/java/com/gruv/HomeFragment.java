package com.gruv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gruv.com.gruv.NewsFeedAdapter;
import com.gruv.interfaces.ClickInterface;
import com.gruv.models.Author;
import com.gruv.models.Event;
import com.gruv.models.Venue;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ClickInterface {
    private FirebaseAuth authenticateObj;
    private FirebaseUser currentUser;
    int count = 0;
    boolean doneAdding = false;
    public NewsFeedAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private Event event, event2;
    private ClickInterface clickInterface;
    private List<Venue> venue = new ArrayList<>();
    private RecyclerView feed;
    private ScrollView scrollView;
    private LinearLayout layoutProgress;
    Map<String, String> map;
    private LinearLayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Author thisUser = new Author("1234", "This User", null, null), eventAuthor;

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
        authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        setCurrentUser();

        clickInterface = this;
        Toolbar toolbar = getActivity().findViewById(R.id.main_app_toolbar);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.app_name, R.string.app_name);

        layoutProgress = getActivity().findViewById(R.id.layout_progress);
        feed = getActivity().findViewById(R.id.listNewsFeed);
        //scrollView = getActivity().findViewById(R.id.mainScrollView);

        Author eventAuthor = new Author("1234", "Night Show", null, R.drawable.profile_pic6);
        event = new Event("123", "Night Show at Mercury", "Night Show at Mercury has a jam packed line-up", eventAuthor, LocalDateTime.of(2019, Month.MAY, 3, 16, 30), new Venue("Mercury Live"), R.drawable.party_4);
        addPost(event);

        adapter = new NewsFeedAdapter(eventList, clickInterface, thisUser);
        feed.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        feed.setLayoutManager(layoutManager);

        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButton);
        fab.setOnClickListener((v) -> {
            addPostToFeed(v);
        });
    }

    public void recyclerViewOnClick(int position) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        intent.putExtra("Event", eventList.get(position));
        startActivity(intent);
    }

    public void addPost(@NotNull Event event) {
        if (event.getAuthor() != null)
            eventList.add(event);

    }

    public void addPostToFeed(View v) {
        // save index and top position
        int index = layoutManager.findFirstVisibleItemPosition();
        v = feed.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - feed.getPaddingTop());

        // add new post
//        Comment comment = new Comment("123", "124", "Okay, I can't wait to be there. The line up looks so nice. There should be a photographer!", new Author("2", "Finn Human", null, R.drawable.profile_pic5));
//        ArrayList<Comment> list = new ArrayList<>();
//        list.add(comment);
//        comment = new Comment("123", "124", "Lit!", new Author("1", "Night Show", null, R.drawable.profile_pic4));
//        list.add(comment);
//        list.add(comment);
//        list.add(comment);
//        list.add(comment);
//        list.add(new Comment("123", "124", "Hela Khanjonge", new Author("1", "Random", null, R.drawable.profile_pic5)));
//        list.add(comment);
//        list.add(comment);
//        event2 = new Event("124", "Deep Brew Sundaze", new Author("1", "The Grand", null, R.drawable.profile_pic6), LocalDateTime.of(2019, Month.DECEMBER, 16, 19, 0), new Venue("Roof Garden Bar"), "Deep Brew is set at the sunset of the roof garden bar. Situated in the heart of Port Elizabeth, it is an event to enjoy, and maybe throw an after party at the end of it all. The Indian Ocean is named after India (Oceanus Orientalis Indicus) since at least 1515. India, then, is the Greek/Roman name for the \"region of the Indus River\".[6]\n\nCalled the Sindhu Mahasagara or the great sea of the Sindhu by the Ancient Indians, this ocean has been variously called Hindu Ocean, Indic Ocean, etc. in various languages. The Indian Ocean was also known earlier as the Eastern Ocean, a term was still in use during the mid-18th century (see map).[6] Conversely, when China explored the Indian Ocean in the 15th century they called it the \"Western Oceans\".", list, null, R.drawable.party);
        getEvent();


        adapter.notifyDataSetChanged();

        // restore index and position
        layoutManager.scrollToPositionWithOffset(index, top);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void startPostActivity(int position) {
//        Object o = feed.getItemAtPosition(position);

        Intent post = new Intent(getActivity(), PostActivity.class);
        post.putExtra("Event", eventList.get(position));
        startActivity(post);
    }

    public void scrollToTop() {
    }

    public void setCurrentUser() {
        thisUser = new Author(currentUser.getUid(), currentUser.getDisplayName(), null, R.drawable.profile_pic4);
        if (currentUser.getPhotoUrl() != null)
            thisUser.setAvatar(currentUser.getPhotoUrl().toString());
        thisUser.setEmail(currentUser.getEmail());
    }

    public void getEvent() {
        authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        doneAdding = false;

        databaseReference.child("Event").child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                addPost(event);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}

