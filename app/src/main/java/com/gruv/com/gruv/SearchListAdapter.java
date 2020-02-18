package com.gruv.com.gruv;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gruv.PostActivity;
import com.gruv.R;
import com.gruv.models.Event;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private final List<Event> eventList;
    private final Context context;

    public SearchListAdapter(Context context, List<Event> eventList) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.search_result, parent, false);
        SearchListAdapter.ViewHolder viewHolder = new SearchListAdapter.ViewHolder(eventView);


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(SearchListAdapter.ViewHolder viewHolder, int position) {

        TextView eventTitle = viewHolder.eventTitle;
        TextView venue = viewHolder.venue;
        TextView eventDay = viewHolder.eventDay;
        TextView eventMonth = viewHolder.eventMonth;

        eventTitle.setText(eventList.get(position).getEventName() + " - by " + eventList.get(position).getAuthor().getName());
        venue.setText(eventList.get(position).getVenue().getVenueName());
        eventDay.setText(eventList.get(position).getEventDate().getDayOfMonth() + "");
        eventMonth.setText(eventList.get(position).getEventDate().getMonth().toString().substring(0, 3).toUpperCase());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle;
        public TextView venue;
        public TextView eventDay;
        public TextView eventMonth;
        public ConstraintLayout layoutEventSummary;

        public ViewHolder(View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.textEventTitle);
            venue = itemView.findViewById(R.id.textVenue);
            eventDay = itemView.findViewById(R.id.textViewDay);
            eventMonth = itemView.findViewById(R.id.textViewMonth);
            layoutEventSummary = itemView.findViewById(R.id.layoutEventSummary);

            layoutEventSummary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent post = new Intent(context, PostActivity.class);
                    post.putExtra("Event", eventList.get(getAdapterPosition()));
                    context.startActivity(post);
                }
            });
        }
    }
}
