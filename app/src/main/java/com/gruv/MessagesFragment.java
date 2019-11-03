package com.gruv;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {
    ListView list;
    MessagesListAdapter adapter;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RelativeLayout layoutError = (RelativeLayout)getView().findViewById(R.id.layoutError);
        layoutError.setVisibility(View.GONE);

        final String[] senderName = {"Thabo", "Lyle", "JT", "Tshego", "Kgosi"};

        final String[] messagePreview = {"See you there", "Cool", "It starts early so we can't take long", "Ok ", "Tonight?"};

        String[] messageTime = {"11:30", "9:30", "12h", "10h", "1d"};
        final Integer[] imgid = {
                R.drawable.profile_pic4, R.drawable.profile_pic5,
                R.drawable.profile_pic6, R.drawable.profile_pic5,
                R.drawable.profile_pic4,
        };
        adapter = new MessagesListAdapter((Activity) super.getContext(), senderName, messagePreview, messageTime, imgid);
        list = getActivity().findViewById(R.id.text_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = list.getItemAtPosition(position);
                Intent intentChat = new Intent(getActivity(), ChatActivity.class);
                TextView textSendersName = (TextView)getView().findViewById(R.id.textSendersName) ;
                intentChat.putExtra("sendersName", senderName[position]);
                intentChat.putExtra("pic", imgid[position]);
                intentChat.putExtra("messageText", messagePreview[position]);
                startActivity(intentChat);
            }
        });
    }
}
