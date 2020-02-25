package com.gruv.com.gruv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gruv.R;
import com.gruv.interfaces.SecondClickInterface;
import com.gruv.models.Event;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostedEventsAdapter extends RecyclerView.Adapter<PostedEventsAdapter.ViewHolder> {


    private final SecondClickInterface listener;
    private List<Event> eventList;
    private Context context;

    public PostedEventsAdapter(Context context, List<Event> eventList, SecondClickInterface listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.post, parent, false);
        ViewHolder viewHolder = new ViewHolder(eventView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostedEventsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Event event = eventList.get(position);
        TextView titleText = viewHolder.titleText;
        TextView textAuthor = viewHolder.textAuthor;
        TextView textDescription = viewHolder.textDescription;
        TextView textVenue = viewHolder.textVenue;
        TextView textDay = viewHolder.textDay;
        TextView textMonth = viewHolder.textMonth;
        TextView textCommentCount = viewHolder.textCommentCount;
        TextView textLikeCount = viewHolder.textLikeCount;
        CircleImageView imageProfilePic = viewHolder.imageProfilePic;
        ImageView imagePost = viewHolder.imagePost;

        titleText.setText(event.getEventName());
        textAuthor.setText(event.getAuthor().getName());
        textDescription.setText(event.getEventDescription());
        textVenue.setText(event.getVenue().getVenueName());
        textDay.setText(event.getEventDate().getDayOfMonth() + "");
        textMonth.setText(event.getEventDate().getMonth().toString().substring(0, 3).toUpperCase());
        if (event.getComments() == null || event.getComments().isEmpty())
            textCommentCount.setText("0");
        else
            textCommentCount.setText(event.getComments().size() + "");
        if (event.getLikes() == null || event.getLikes().isEmpty())
            textLikeCount.setText("0");
        else
            textLikeCount.setText(event.getComments().size() + "");

        Glide.with(context).load(event.getImagePostUrl()).into(imagePost);
        Glide.with(context).load(event.getAuthor().getAvatar()).into(imageProfilePic);
//        imageProfilePic.setImageResource(event.getAuthor().getProfilePictureId());
//        imagePost.setImageResource(event.getImagePostId());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText;
        public TextView textAuthor;
        public TextView textDescription;
        public TextView textVenue;
        public TextView textDay;
        public TextView textMonth;
        public TextView textCommentCount;
        public TextView textLikeCount;
        public CircleImageView imageProfilePic;
        public ImageView imagePost;
        public CardView cardPost;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.textViewEventTitle);
            textAuthor = itemView.findViewById(R.id.textViewEventAuthor);
            textDescription = itemView.findViewById(R.id.textEventDescription);
            textVenue = itemView.findViewById((R.id.textVenue));
            textDay = itemView.findViewById(R.id.textViewDay);
            textMonth = itemView.findViewById(R.id.textViewMonth);
            textCommentCount = itemView.findViewById(R.id.counterComments);
            textLikeCount = itemView.findViewById(R.id.counterLikes);
            imageProfilePic = itemView.findViewById(R.id.imageProfilePic);
            imagePost = itemView.findViewById(R.id.imageViewPostPicture);
            cardPost = itemView.findViewById(R.id.card_view_post);

            cardPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.recyclerViewPostOnClick(getAdapterPosition());
                }
            });
        }
    }
}
