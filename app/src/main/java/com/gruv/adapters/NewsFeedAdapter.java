package com.gruv.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gruv.PostActivity;
import com.gruv.ProfileActivity;
import com.gruv.R;
import com.gruv.UserActivity;
import com.gruv.interfaces.ClickInterface;
import com.gruv.models.Author;
import com.gruv.models.Event;
import com.gruv.models.Like;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {


    private final ClickInterface listener;
    private final Activity activity;
    private List<Event> eventList;
    private Author thisUser;
    private Like like;
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public NewsFeedAdapter(@NonNull Context context, Activity activity, List<Event> eventList, ClickInterface listener, Author thisUser) {
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
    public void onBindViewHolder(NewsFeedAdapter.ViewHolder viewHolder, int position) {
        Event event = eventList.get(position);
        TextView titleText = viewHolder.titleText;
        TextView textAuthor = viewHolder.textAuthor;
        TextView textDescription = viewHolder.textDescription;
        TextView textVenue = viewHolder.textVenue;
        TextView textDay = viewHolder.textDay;
        TextView textMonth = viewHolder.textMonth;
        TextView textYear = viewHolder.textYear;
        TextView textCommentCount = viewHolder.textCommentCount;
        TextView textLikeCount = viewHolder.textLikeCount;
        CircleImageView imageProfilePic = viewHolder.imageProfilePic;
        ImageView imagePost = viewHolder.imagePost;
        ImageView imageVerified = viewHolder.imageVerified;
        LinearLayout chipPassed = viewHolder.chipPassed;
        TextView textDatePosted = viewHolder.textDatePosted;

        textDatePosted.setText(getCounterFromDate(LocalDateTime.parse(event.getDatePosted(), DateTimeFormatter.ISO_DATE_TIME)));

        if (event.getEventEndDate() != null) {
            if (event.getEventEndDate().isBefore(LocalDateTime.now()))
                chipPassed.setVisibility(View.VISIBLE);
            else
                chipPassed.setVisibility(View.GONE);
        } else {
            if (event.getEventDate().isBefore(LocalDateTime.now()))
                chipPassed.setVisibility(View.VISIBLE);
            else
                chipPassed.setVisibility(View.GONE);
        }
        viewHolder.setLiked(event, thisUser, position);
        titleText.setText(event.getEventName());
        textAuthor.setText(event.getAuthor().getName());
        if (event.getAuthor().isVerified()) {
            imageVerified.setVisibility(View.VISIBLE);
        } else {
            imageVerified.setVisibility(View.GONE);
        }
        textDescription.setText(event.getEventDescription());
        textVenue.setText(event.getVenue().getVenueName());
        textDay.setText(event.getEventDate().getDayOfMonth() + "");
        textMonth.setText(event.getEventDate().getMonth().toString().substring(0, 3).toUpperCase());
        if (event.getEventDate().getYear() != LocalDateTime.now().getYear()) {
            textYear.setText(event.getEventDate().getYear() + "");
            textYear.setVisibility(View.VISIBLE);
        }
        if (event.getComments() == null || event.getComments().isEmpty())
            textCommentCount.setText("0");
        else
            textCommentCount.setText(event.getComments().size() + "");
        if (event.getLikes() == null || event.getLikes().isEmpty())
            textLikeCount.setText("0");
        else
            textLikeCount.setText(event.getLikes().size() + "");

        Glide.with(context).load(event.getImagePostUrl()).centerCrop().into(imagePost);
        if (event.getAuthor().getAvatar() != null)
            Glide.with(context).load(event.getAuthor().getAvatar()).centerCrop().into(imageProfilePic);
        else
            imageProfilePic.setImageResource(R.drawable.ic_account_circle_black_always);

    }

    private String getCounterFromDate(LocalDateTime startDate) {
        LocalDateTime dateNow = LocalDateTime.now();
        String counter = "";

        Duration duration = Duration.between(startDate, dateNow);
        long allDays = duration.toDays();
        int year = (int) (allDays / 365);
        long yearDays = duration.toDays() - (year * 365);
        int months = (int) (yearDays / 30.4167);
        int monthDays = (int) (yearDays - (months * 30.4167));
        long allHours = duration.toHours();
        int hours = (int) (allHours - (allDays * 24));
        long allMinutes = duration.toMinutes();
        int minutes = (int) (allMinutes - (allHours * 60));
        int seconds = (int) (duration.getSeconds() - (allMinutes * 60));

        if (year == 0) {
            if (months == 0) {
                if (monthDays == 0) {
                    if (hours == 0) {
                        if (minutes == 0) {
                            counter = seconds + "s";
                        } else {
                            counter = minutes + "m";
                        }
                    } else {
                        counter = hours + "h";
                    }
                } else {
                    counter = monthDays + "d";
                }
            } else {
                counter = startDate.getDayOfMonth() + " " + startDate.getMonth().toString().substring(0, 3);
            }
        } else {
            counter = startDate.getDayOfMonth() + " " + startDate.getMonth().toString().substring(0, 3) + " " + startDate.getYear();
        }
        return counter;
//        LocalDateTime durationDateTime = LocalDateTime.of(year, months, monthDays, hours, minutes, seconds);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public int position;
        public CircleImageView imageProfilePic;
        TextView textYear;
        ImageView imageVerified;
        TextView titleText;
        TextView textAuthor;
        TextView textDescription;
        TextView textVenue;
        TextView textDay;
        TextView textMonth;
        TextView textCommentCount;
        TextView textLikeCount;
        ImageView imagePost;
        CardView cardPost;
        ImageView likeButton;
        ImageView commentButton;
        ConstraintLayout layoutUser;
        LinearLayout chipPassed;
        TextView textDatePosted;
        boolean liked = false;

        public ViewHolder(View itemView) {
            super(itemView);

            like = new Like();
            like.setAuthor(thisUser);
            position = getAdapterPosition();

            textYear = itemView.findViewById(R.id.textViewYear);
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
            layoutUser = itemView.findViewById(R.id.layoutAuthor);
            imageVerified = itemView.findViewById(R.id.imageVerified);
            chipPassed = itemView.findViewById(R.id.chipEventPassed);
            textDatePosted = itemView.findViewById(R.id.textDatePosted);

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
                    setLiked(eventList.get(position), thisUser, position);
                    if (!liked) {
                        like.setEventId(eventList.get(position).getEventID());
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

            layoutUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    if (!eventList.get(position).getAuthor().getId().equals(thisUser.getId())) {
                        Intent intent = new Intent(context, UserActivity.class);
                        intent.putExtra("selectedUser", eventList.get(position).getAuthor());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent);
                    }
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
            like.setLikeId(databaseReference.child("Event").child(event.getEventID()).child("likes").push().getKey());
            event.addLike(like);
            likeToAdd.put(like.getLikeId(), like);

            databaseReference.child("Event").child(event.getEventID()).child("likes").updateChildren(likeToAdd, new DatabaseReference.CompletionListener() {
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

            databaseReference.child("Event").child(event.getEventID()).child("likes").child(like.getLikeId()).setValue(null)
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
