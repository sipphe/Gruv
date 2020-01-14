package com.gruv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gruv.com.gruv.NewsFeedAdapter;
import com.gruv.models.Author;
import com.gruv.models.Comment;
import com.gruv.models.Event;
import com.gruv.models.Venue;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private List<String> eventTitle = new ArrayList<>();
    private List<String> author = new ArrayList<>();
    private List<String> description = new ArrayList<>();
    public NewsFeedAdapter adapter;
    private List<Integer> countLikes = new ArrayList<>();
    private List<Integer> countComments = new ArrayList<>();
    private List<Integer> day = new ArrayList<>();
    private List<String> month = new ArrayList<>();
    private List<Integer> imageIDPostPic = new ArrayList<>();
    private List<Integer> imageIDProfilePic = new ArrayList<>();
    private List<Event> eventList = new ArrayList<>();
    private Event event, event2;
    private List<Venue> venue = new ArrayList<>();
    private ListView feed;
    private ScrollView scrollView;
    private LinearLayout layoutProgress;

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
        Toolbar toolbar = getActivity().findViewById(R.id.main_app_toolbar);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.app_name, R.string.app_name);

        layoutProgress = getActivity().findViewById(R.id.layout_progress);
        feed = getActivity().findViewById(R.id.listNewsFeed);
        //scrollView = getActivity().findViewById(R.id.mainScrollView);

        Author eventAuthor = new Author("1234", "Night Show", null, R.drawable.profile_pic6);
        event = new Event("123", "Night Show at Mercury", "Night Show at Mercury has a jam packed line-up", eventAuthor, LocalDateTime.of(2019, Month.MAY, 3, 16, 30), new Venue("Mercury Live"), R.drawable.party_4);
        addPost(event);

        adapter = new NewsFeedAdapter(getActivity(), eventTitle, author, description, venue, countLikes, countComments, day, month, imageIDPostPic, imageIDProfilePic);
        feed.setAdapter(adapter);

        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButton);
        fab.setOnClickListener((v) -> {
            addPostToFeed(v);
        });

        feed.setOnItemClickListener((parent, view1, position, id) -> {
            startPostActivity(position);
        });


    }


    public void addPost(@NotNull Event event) {


        eventTitle.add(event.getEventName());
        author.add(event.getAuthor().getName());
        description.add(event.getEventDescription());
        venue.add(event.getVenue());
        if (event.getComments() == null || event.getComments().isEmpty())
            countComments.add(0);
        else
            countComments.add(event.getComments().size());

        if (event.getLikes() == null || event.getLikes().isEmpty())
            countLikes.add(0);
        else
            countLikes.add(event.getComments().size());

        day.add(event.getEventDate().getDayOfMonth());
        month.add(event.getEventDate().getMonth().toString().substring(0, 3).toUpperCase());
        imageIDProfilePic.add(event.getAuthor().getProfilePictureId());
        imageIDPostPic.add(event.getImagePostId());
        eventList.add(event);

    }

    public void addPostToFeed(View v) {
        // save index and top position
        int index = feed.getFirstVisiblePosition();
        v = feed.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - feed.getPaddingTop());

        // add new post
        Comment comment = new Comment("123", "124", "Okay, I can't wait to be there. The line up looks so nice. There should be a photographer!", new Author("2", "Finn Human", null, R.drawable.profile_pic5));
        ArrayList<Comment> list = new ArrayList<>();
        list.add(comment);
        comment = new Comment("123", "124", "Lit!", new Author("1", "Night Show", null, R.drawable.profile_pic4));
        list.add(comment);
        list.add(comment);
        list.add(comment);
        list.add(comment);
        list.add(new Comment("123", "124", "Hela Khanjonge", new Author("1", "Random", null, R.drawable.profile_pic5)));
        list.add(comment);
        list.add(comment);
        event2 = new Event("124", "Deep Brew Sundaze", new Author("1", "The Grand", null, R.drawable.profile_pic6), LocalDateTime.of(2019, Month.DECEMBER, 16, 19, 0), new Venue("Roof Garden Bar"), "Deep Brew is set at the sunset of the roof garden bar. Situated in the heart of Port Elizabeth, it is an event to enjoy, and maybe throw an after party at the end of it all. The Indian Ocean is named after India (Oceanus Orientalis Indicus) since at least 1515. India, then, is the Greek/Roman name for the \"region of the Indus River\".[6]\n\nCalled the Sindhu Mahasagara or the great sea of the Sindhu by the Ancient Indians, this ocean has been variously called Hindu Ocean, Indic Ocean, etc. in various languages. The Indian Ocean was also known earlier as the Eastern Ocean, a term was still in use during the mid-18th century (see map).[6] Conversely, when China explored the Indian Ocean in the 15th century they called it the \"Western Oceans\".", list, null, R.drawable.party);
        addPost(event2);

        adapter = new NewsFeedAdapter(getActivity(), eventTitle, author, description, venue, countLikes, countComments, day, month, imageIDPostPic, imageIDProfilePic);

        // restore index and position
        feed.setSelectionFromTop(index, top);
    }

    public void startPostActivity(int position) {
        Object o = feed.getItemAtPosition(position);

        Intent post = new Intent(getActivity(), PostActivity.class);
        post.putExtra("Event", eventList.get(position));
        startActivity(post);
    }

    public void scrollToTop() {
    }
}

