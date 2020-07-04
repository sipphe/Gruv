package com.gruv.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gruv.ProfileActivity;
import com.gruv.R;
import com.gruv.UserActivity;
import com.gruv.models.Author;
import com.gruv.models.Comment;
import com.gruv.models.Event;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private final Activity context;
    private final Event event;
    private Author thisUser;
    private List<Comment> commentList;
    private List<Comment> commentKeys;

    public CommentListAdapter(@NonNull Activity context, Event event, List<Comment> commentKeys, Author thisUser) {
        this.context = context;
        this.thisUser = thisUser;
        this.event = event;
        this.commentKeys = commentKeys;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.commentcontent, parent, false);
        CommentListAdapter.ViewHolder viewHolder = new CommentListAdapter.ViewHolder(eventView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (event.getComments() != null) {
            Comment comment = event.getComments().get(commentKeys.get(position).getCommentId());
            CircleImageView profilePic = holder.profilePic;
            TextView textComment = holder.textComment;
            ImageView imageVerified = holder.imageVerified;
            TextView textDatePosted = holder.textDatePosted;

            if (comment.getAuthor().isVerified()) {
                imageVerified.setVisibility(View.VISIBLE);
            } else {
                imageVerified.setVisibility(View.GONE);
            }
            if (comment.getAuthor().getAvatar() != null) {
                Glide.with(context).load(comment.getAuthor().getAvatar()).into(profilePic);
            } else {
                profilePic.setImageResource(R.drawable.ic_account_circle_black_24dp);
            }

            textDatePosted.setText(getCounterFromDate(LocalDateTime.parse(comment.getPostedDate(), DateTimeFormatter.ISO_DATE_TIME)));


//        textAuthor.setText(comment.getAuthor().getName());
            textComment.setText(HtmlCompat.fromHtml("<b>" + comment.getAuthor().getName() + "</b>    " + comment.getCommentText(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
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
                counter = monthDays + "d";
            }
        } else {
            counter = monthDays + "d";
        }

        return counter;
//        LocalDateTime durationDateTime = LocalDateTime.of(year, months, monthDays, hours, minutes, seconds);
    }

    @Override
    public int getItemCount() {
        if (event.getComments() != null)
            return event.getComments().size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDatePosted;
        CircleImageView profilePic;
        TextView textComment;
        ImageView imageVerified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.imageProfilePic);
//            textAuthor = itemView.findViewById(R.id.textEventAuthor);
            textComment = itemView.findViewById(R.id.textComment);
            imageVerified = itemView.findViewById(R.id.imageVerified);
            textDatePosted = itemView.findViewById(R.id.textDatePosted);
            textComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!event.getComments().get(commentKeys.get(getAdapterPosition()).getCommentId()).getAuthor().getId().equals(thisUser.getId())) {
                        Intent intent = new Intent(context, UserActivity.class);
                        intent.putExtra("selectedUser", event.getComments().get(commentKeys.get(getAdapterPosition()).getCommentId()).getAuthor());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
