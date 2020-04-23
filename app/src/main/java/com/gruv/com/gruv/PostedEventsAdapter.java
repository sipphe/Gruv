package com.gruv.com.gruv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gruv.PostActivity;
import com.gruv.R;
import com.gruv.interfaces.SecondClickInterface;
import com.gruv.models.Author;
import com.gruv.models.Event;
import com.gruv.models.Like;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostedEventsAdapter extends RecyclerView.Adapter<PostedEventsAdapter.ViewHolder> {


    private final SecondClickInterface listener;
    private List<Event> eventList;
    private Author thisUser;
    private Like like;
    private Context context;
    private Activity activity;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public PostedEventsAdapter(Context context, Activity activity, Author thisUser, List<Event> eventList, SecondClickInterface listener) {
        this.context = context;
        this.activity = activity;
        this.thisUser = thisUser;
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
        ImageView commentButton;
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
            commentButton = itemView.findViewById(R.id.imageComment);

            cardPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.recyclerViewPostOnClick(getAdapterPosition());
                }
            });
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    setLiked(eventList.get(position), thisUser, position);
                    if (!liked) {
                        like.setEventId(eventList.get(position).getEventId());
                        liked = true;
                        addLikeToDB(like, eventList.get(position));

                    } else {
                        liked = false;
                        eventList.get(position).removeLike(getUserLike(eventList.get(position)));
                        removeLikeFromDB(getUserLike(eventList.get(position)), eventList.get(position));
                    }
                    setLiked(eventList.get(position), thisUser, position);
                }
            });
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    Intent intent = new Intent(context, PostActivity.class);
                    intent.putExtra("Event", eventList.get(position));
                    intent.putExtra("CommentClicked", true);
                    context.startActivity(intent);
                }
            });

        }

        public Like getUserLike(Event event) {
            int size = 0;
            boolean liked = false;
            Like thislike = null;
            if (event.getLikes() != null)
                size = event.getLikes().size();

            if (size != 0) {
                for (Map.Entry<String, Like> like : event.getLikes().entrySet()) {
                    if (like.getValue().getAuthor().getId().equals(thisUser.getId()))
                        thislike = like.getValue();
                }
            }
//            for (int i = 0; i < size; i++) {
//                if (event.getLikes().get(i).getAuthor() == thisUser)
//                    like = event.getLikes().get(i);
//            }
            return thislike;
        }

        public void setLiked(Event event, Author thisUser, int position) {
            int size = 0;
            liked = false;
            if (event.getLikes() != null)
                size = event.getLikes().size();

            if (size != 0) {
                for (Map.Entry<String, Like> like : event.getLikes().entrySet()) {
                    if (like.getValue().getAuthor().getId().equals(thisUser.getId()))
                        liked = true;
                }
            }

//            for (int i = 0; i < size; i++) {
//                if (event.getLikes().get(i).getAuthor() == thisUser)
//                    liked = true;
//            }

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

        public void addLikeToDB(Like like, Event event) {
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference();
            Map<String, Object> likeToAdd = new HashMap<>();
            like.setLikeId(databaseReference.child("Event").child(event.getEventId()).child("likes").push().getKey());
            event.addLike(like);
            likeToAdd.put(like.getLikeId(), like);

            databaseReference.child("Event").child(event.getEventId()).child("likes").updateChildren(likeToAdd, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        showSnackBar(databaseError.getMessage(), Snackbar.LENGTH_LONG);
                    }
                }
            });

        }

        public void removeLikeFromDB(Like like, Event event) {
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference();

            databaseReference.child("Event").child(event.getEventId()).child("likes").child(like.getLikeId()).setValue(null)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showSnackBar(e.getMessage(), Snackbar.LENGTH_LONG);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setLiked(eventList.get(position), thisUser, position);
                }
            });

        }


        public void showSnackBar(String message, Integer length) {
            try {
                View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                Snackbar.make(rootView, message, length).show();
            } catch (Exception e) {
                Log.w("Snackbar Error", "Couldn't load Snackbar");
            }
        }

    }

}
