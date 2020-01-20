package com.gruv.com.gruv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gruv.R;
import com.gruv.interfaces.ClickInterface;
import com.gruv.models.Event;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PromotedEventsAdapter extends RecyclerView.Adapter<PromotedEventsAdapter.ViewHolder> {


    private final ClickInterface listener;
    private List<Event> eventList;

    public PromotedEventsAdapter(List<Event> eventList, ClickInterface listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.posted_events, parent, false);
        ViewHolder viewHolder = new ViewHolder(eventView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PromotedEventsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Event event = eventList.get(position);
        CircleImageView imagePost = viewHolder.imagePost;
        TextView textTitle = viewHolder.textTitle;

        imagePost.setImageResource(event.getImagePostId());
        if (event.getEventName().length() > 11)
            textTitle.setText(event.getEventName().substring(0, 11) + "...");
        else
            textTitle.setText(event.getEventName());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imagePost;
        public TextView textTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            imagePost = itemView.findViewById(R.id.imagePost);
            textTitle = itemView.findViewById(R.id.textEventName);

            imagePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.recyclerViewOnClick(getAdapterPosition());
                }
            });
        }
    }
}
