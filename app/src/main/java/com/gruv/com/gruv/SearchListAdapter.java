package com.gruv.com.gruv;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gruv.R;
import com.gruv.models.Event;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchListAdapter extends ArrayAdapter {

    private final Activity context;
    private final List<Event> events;

    public SearchListAdapter(@NonNull Activity context, List<Event> events, List<String> strings) {
        super(context, R.layout.search_result, strings);
        this.context = context;
        this.events = events;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.search_result, null, true);

        TextView eventTitle = rowView.findViewById(R.id.textEventTitle);
        TextView venue = rowView.findViewById(R.id.textVenue);
        TextView eventDay = rowView.findViewById(R.id.textViewDay);
        TextView eventMonth = rowView.findViewById(R.id.textViewMonth);
        CircleImageView profilePicture = rowView.findViewById(R.id.imageViewProfilePic);

        eventTitle.setText(events.get(position).getEventName() + " - by " + events.get(position).getAuthor().getName());
        venue.setText(events.get(position).getVenue().getVenueName());
        eventDay.setText(events.get(position).getEventDate().getDayOfMonth() + "");
        eventMonth.setText(events.get(position).getEventDate().getMonth().toString().substring(0, 3).toUpperCase());

        return rowView;
    }
}
