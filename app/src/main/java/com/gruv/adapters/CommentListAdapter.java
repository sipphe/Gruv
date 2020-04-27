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


//        textAuthor.setText(comment.getAuthor().getName());
            textComment.setText(HtmlCompat.fromHtml("<b>" + comment.getAuthor().getName() + "</b>    " + comment.getCommentText(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }

    @Override
    public int getItemCount() {
        if (event.getComments() != null)
            return event.getComments().size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        TextView textComment;
        ImageView imageVerified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.imageProfilePic);
//            textAuthor = itemView.findViewById(R.id.textEventAuthor);
            textComment = itemView.findViewById(R.id.textComment);
            imageVerified = itemView.findViewById(R.id.imageVerified);
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
