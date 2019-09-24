package com.gruv.com.gruv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruv.R;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] mainTitle;
    private final String[] startTime;
    private final String[] tagAlongers;
    private final Integer[] imgid;

    public MyListAdapter(Activity context, String[] mainTitle, String[] startTime, String[] tagAlongers, Integer[] imgid) {
        super(context, R.layout.custom_list, mainTitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.mainTitle=mainTitle;
        this.startTime=startTime;
        this.tagAlongers=tagAlongers;
        this.imgid=imgid;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_list, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.textEventTitle);
        TextView textStartTime = (TextView) rowView.findViewById(R.id.textEventStartTime);
        TextView textTagAlongers = (TextView) rowView.findViewById(R.id.textEventTagAlongers);
        ImageView imgProfilePic = (ImageView)rowView.findViewById(R.id.imageViewProfilePic);

        Bitmap bm = BitmapFactory.decodeResource(rowView.getResources(),R.drawable.profile_pic);
        RoundImage roundImage = new RoundImage(bm);
        imgProfilePic.setImageDrawable(roundImage);

        titleText.setText(mainTitle[position]);
        imgProfilePic.setImageResource(imgid[position]);
        textStartTime.setText(startTime[position]);
        textTagAlongers.setText(tagAlongers[position]);

        return rowView;

    };
}