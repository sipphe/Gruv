package com.gruv.com.gruv;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gruv.R;
import com.gruv.models.Comment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private List<Comment> commentList;

    public CommentListAdapter(@NonNull Activity context, List<Comment> commentList, List<String> strings) {
        super(context, R.layout.comment, strings);
        this.context = context;
        this.commentList = commentList;
    }

    public void setData(Comment comment) {
        if (commentList != null) {
            this.commentList.add(comment);
        } else {
            this.commentList = new ArrayList<>();
            this.commentList.add(comment);
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.comment, null, true);

        CircleImageView profilePic = rowView.findViewById(R.id.imageProfilePic);
        TextView textAuthor = rowView.findViewById(R.id.textEventAuthor);
        TextView textComment = rowView.findViewById(R.id.textComment);

        profilePic.setImageResource(commentList.get(position).getAuthor().getProfilePictureId());
        textAuthor.setText(commentList.get(position).getAuthor().getName());
        textComment.setText(commentList.get(position).getCommentText());

        return rowView;
    }
}
