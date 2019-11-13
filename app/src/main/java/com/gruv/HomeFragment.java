package com.gruv;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gruv.com.gruv.NewsFeedAdapter;
import com.gruv.models.Author;
import com.gruv.models.Event;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    List<String> eventTitle = new ArrayList<>();
    List<String> author = new ArrayList<>();
    List<String> description = new ArrayList<>();
    List<Integer> countLikes = new ArrayList<>();
    List<Integer> countComments = new ArrayList<>();
    List<Integer> day = new ArrayList<>();
    List<String> month = new ArrayList<>();
    List<Integer> imageIDPostPic = new ArrayList<>();
    List<Integer> imageIDProfilePic = new ArrayList<>();
    Event event;
    NewsFeedAdapter adapter;
    ListView listView;
    ScrollView scrollView;

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
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.title_notifications, R.string.title_notifications);
        listView = getActivity().findViewById(R.id.listNewsFeed);
        scrollView = getActivity().findViewById(R.id.mainScrollView);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Author eventAuthor = new Author("1234", "Night Show", null, R.drawable.profile_pic6);
            event = new Event("123", "Night Show at Mercury", eventAuthor, LocalDate.of(2019, Month.MAY, 3), "Night Show at Mercury has a jam packed line-up", null, null, R.drawable.party_4);
            addPost(event);
            addPost(event);
            addPost(event);

            adapter = new NewsFeedAdapter(getActivity(), eventTitle, author, description, countLikes, countComments, day, month, imageIDPostPic, imageIDProfilePic);
            listView.setAdapter(adapter);
        }

        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // save index and top position
                int index = listView.getFirstVisiblePosition();
                v = listView.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

                // add new post
                addPost(event);

                adapter = new NewsFeedAdapter(getActivity(), eventTitle, author, description, countLikes, countComments, day, month, imageIDPostPic, imageIDProfilePic);

                // restore index and position
                listView.setSelectionFromTop(index, top);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addPost(Event event) {
        eventTitle.add(event.getEventName());
        author.add(event.getAuthor().getName());
        description.add(event.getEventDescription());
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


    }


}

