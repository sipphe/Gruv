package com.gruv;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.gruv.com.gruv.MyListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    ListView list;

    public NotificationFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Toolbar toolbar = getActivity().findViewById(R.id.main_app_toolbar);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.title_notifications, R.string.title_notifications);

        String[] mainTitle = {"The Ivyson Tour", "Back To The Beach", "Deep Brew Sundaze", "The Night Show at Meercury", "Sneaker Exchange"};

        String[] startingTime = {"starting in 4 hours", "starting tomorrow at 18:00 ", "starting on Thursday at 16:30", "starting in a week", "starting in a week and 2 days"};

        String[] tagAlongers = {"Karabo and 10 others are going", "Thabo and one other is going", "Jane is going", " ", "Boiphelo and 2 others are going"};
        Integer[] imgid = {
                R.drawable.profile_pic4, R.drawable.profile_pic5,
                R.drawable.profile_pic6, R.drawable.profile_pic5,
                R.drawable.profile_pic4,
        };
        MyListAdapter adapter = new MyListAdapter((Activity) super.getContext(), mainTitle, startingTime, tagAlongers, imgid);
        list = getActivity().findViewById(R.id.notif_list);
        list.setAdapter(adapter);
    }

}
