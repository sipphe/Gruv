package com.gruv;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessagesListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] senderName;
    private final String[] messagePreview;
    private final String[] messageTime;
    private final Integer[] imgid;

    public MessagesListAdapter(Activity context, String[] senderName, String[] messagePreview, String[] messageTime, Integer[] imgid) {
        super(context, R.layout.message_list, senderName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.senderName=senderName;
        this.messagePreview=messagePreview;
        this.messageTime=messageTime;
        this.imgid=imgid;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.message_list, null,true);

        TextView textSenderName = (TextView) rowView.findViewById(R.id.textSendersName);
        TextView textMessagePreview = (TextView) rowView.findViewById(R.id.textMessagePreview);
        TextView textMessageTime = (TextView) rowView.findViewById(R.id.textMessageTime);
        ImageView imgProfilePic = (ImageView)rowView.findViewById(R.id.imageProfilePic);

        textSenderName.setText(senderName[position]);
        imgProfilePic.setImageResource(imgid[position]);
        textMessagePreview.setText(messagePreview[position]);
        textMessageTime.setText(messageTime[position]);

        return rowView;

    };
}
