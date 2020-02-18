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

import com.gruv.R;
import com.gruv.interfaces.ClickInterface;
import com.gruv.models.Author;
import com.gruv.models.Event;
import com.gruv.models.Like;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {


    private final ClickInterface listener;
    private List<Event> eventList;
    private Author thisUser;
    private Like like;

    public NewsFeedAdapter(List<Event> eventList, ClickInterface listener, Author thisUser) {
        this.eventList = eventList;
        this.listener = listener;
        this.thisUser = thisUser;

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
    public void onBindViewHolder(NewsFeedAdapter.ViewHolder viewHolder, int position) {
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
            textLikeCount.setText(event.getLikes().size() + "");

        imageProfilePic.setImageResource(event.getAuthor().getProfilePictureId());
        imagePost.setImageResource(event.getImagePostId());

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
        public ImageView likeButton;
        public boolean liked = false;
        public int position = getAdapterPosition();


        public ViewHolder(View itemView) {
            super(itemView);
            like = new Like();
            like.setAuthor(thisUser);


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
            likeButton = itemView.findViewById(R.id.imageLike);

            cardPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.recyclerViewOnClick(getAdapterPosition());
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!liked) {
                        like.setEventId(eventList.get(getAdapterPosition()).getEventId());
                        liked = true;
                        likeButton.setImageResource(R.drawable.sunshine_clicked);
                        eventList.get(getAdapterPosition()).addLike(like);

                        if (eventList.get(getAdapterPosition()).getLikes() != null)
                            textLikeCount.setText(Integer.toString(eventList.get(getAdapterPosition()).getLikes().size()));
                    } else {
                        liked = false;
                        likeButton.setImageResource(R.drawable.sunshine);
                        eventList.get(getAdapterPosition()).removeLike(like);

                        if (eventList.get(getAdapterPosition()).getLikes() != null)
                            textLikeCount.setText(Integer.toString(eventList.get(getAdapterPosition()).getLikes().size()));
                    }
                }
            });

        }
    }
}
