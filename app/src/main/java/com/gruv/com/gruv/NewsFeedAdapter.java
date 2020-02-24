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
    private Context context;

    public NewsFeedAdapter(@NonNull Context context, List<Event> eventList, ClickInterface listener, Author thisUser) {
        this.context = context;
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

        viewHolder.setLiked(event, thisUser, position);
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

        Glide.with(context).load(event.getImagePostUrl()).centerCrop().into(imagePost);
        Glide.with(context).load(event.getAuthor().getAvatar()).centerCrop().into(imageProfilePic);
        //imageProfilePic.setImageResource(event.getAuthor().getProfilePictureId());
        //imagePost.setImageResource(event.getImagePostId());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public int position;
        TextView titleText;
        TextView textAuthor;
        TextView textDescription;
        TextView textVenue;
        TextView textDay;
        TextView textMonth;
        TextView textCommentCount;
        public CircleImageView imageProfilePic;
        TextView textLikeCount;
        ImageView imagePost;
        CardView cardPost;
        ImageView likeButton;
        boolean liked = false;


        public ViewHolder(View itemView) {
            super(itemView);
            like = new Like();
            like.setAuthor(thisUser);
            position = getAdapterPosition();



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
                    position = getAdapterPosition();
                    if (!liked) {
                        like.setEventId(eventList.get(position).getEventId());
                        liked = true;
                        eventList.get(position).addLike(like);

                    } else {
                        liked = false;
                        eventList.get(position).removeLike(getUserLike(eventList.get(position)));
                    }
                    setLiked(eventList.get(position), thisUser, position);
                }
            });

        }

        public Like getUserLike(Event event) {
            int size = 0;
            boolean liked = false;
            Like like = null;
            if (event.getLikes() != null)
                size = event.getLikes().size();

            for (int i = 0; i < size; i++) {
                if (event.getLikes().get(i).getAuthor() == thisUser)
                    like = event.getLikes().get(i);
            }
            return like;
        }

        public void setLiked(Event event, Author thisUser, int position) {
            int size = 0;
            boolean liked = false;
            if (event.getLikes() != null)
                size = event.getLikes().size();

            for (int i = 0; i < size; i++) {
                if (event.getLikes().get(i).getAuthor() == thisUser)
                    liked = true;
            }

            if (liked) {
                likeButton.setImageResource(R.drawable.sunshine_clicked);
            } else {
                likeButton.setImageResource(R.drawable.sunshine);
            }

            if (event.getLikes() == null || event.getLikes().isEmpty())
                textLikeCount.setText("0");
            else
                textLikeCount.setText(event.getLikes().size() + "");
        }
    }
}
