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
import com.gruv.models.Comment;
import com.gruv.models.Event;

import java.time.LocalDate;
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
    private List<Integer> countLikes = new ArrayList<>();
    private List<Integer> countComments = new ArrayList<>();
    private List<Integer> day = new ArrayList<>();
    private List<String> month = new ArrayList<>();
    private List<Integer> imageIDPostPic = new ArrayList<>();
    private List<Integer> imageIDProfilePic = new ArrayList<>();
    private Event event, event2;
    private NewsFeedAdapter adapter;
    private ListView listView;
    private ScrollView scrollView;

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
                Comment comment = new Comment("123", "124", "Lit!", new Author("1", "Night Show", null, R.drawable.profile_pic4));
                ArrayList<Comment> list = new ArrayList<Comment>();
                list.add(comment);
                list.add(comment);
                event2 = new Event("124", "Another Show at Mercury", new Author("1", "Night Show", null, R.drawable.profile_pic4), LocalDate.of(2019, Month.FEBRUARY, 14), "Night Show at Mercury has a jam packed line-up",list, null, R.drawable.party_3);
                addPost(event2);

                adapter = new NewsFeedAdapter(getActivity(), eventTitle, author, description, countLikes, countComments, day, month, imageIDPostPic, imageIDProfilePic);

                // restore index and position
                listView.setSelectionFromTop(index, top);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addPost(Event event) {

        Integer size = eventTitle.size();
        if (size >= 1) {
            eventTitle.add(0, event.getEventName());
            author.add(0, event.getAuthor().getName());
            description.add(0, event.getEventDescription());

            if (event.getComments() == null || event.getComments().isEmpty()) {
                countComments.add(0, 0);
            } else {
                countComments.add(0, event.getComments().size());
            }
            if (event.getLikes() == null || event.getLikes().isEmpty()) {
                countLikes.add(0, 0);
            } else {
                countLikes.add(0, event.getLikes().size());
            }

            day.add(0, event.getEventDate().getDayOfMonth());
            month.add(0, event.getEventDate().getMonth().toString().substring(0, 3).toUpperCase());
            imageIDProfilePic.add(0, event.getAuthor().getProfilePictureId());
            imageIDPostPic.add(0, event.getImagePostId());
        } else {
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

}

