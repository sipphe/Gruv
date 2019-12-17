package com.gruv.com.gruv;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gruv.R;
import com.gruv.models.Venue;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> eventTitle;
    private final List<String> author;
    private final List<String> description;
    private final List<Venue> venue;
    private final List<Integer> countLikes;
    private final List<Integer> countComments;
    private final List<Integer> day;
    private final List<String> month;
    private final List<Integer> imageIDPostPic;
    private final List<Integer> imageIDProfilePic;

    public NewsFeedAdapter(@NonNull Activity context, List<String> eventTitle, List<String> author, List<String> description, List<Venue> venue, List<Integer> countLikes, List<Integer> countComments, List<Integer> day, List<String> month, List<Integer> imageIDPostPic, List<Integer> imageIDProfilePic) {
        super(context, R.layout.post, eventTitle);
        this.context = context;
        this.eventTitle = eventTitle;
        this.author = author;
        this.description = description;
        this.venue = venue;
        this.countLikes = countLikes;
        this.countComments = countComments;
        this.day = day;
        this.month = month;
        this.imageIDPostPic = imageIDPostPic;
        this.imageIDProfilePic = imageIDProfilePic;
    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.post, null, true);

        TextView titleText = rowView.findViewById(R.id.textViewEventTitle);
        TextView textAuthor = rowView.findViewById(R.id.textViewEventAuthor);
        TextView textDescription = rowView.findViewById(R.id.textEventDescription);
        TextView textVenue = rowView.findViewById((R.id.textVenue));
        TextView textDay = rowView.findViewById(R.id.textViewDay);
        TextView textMonth = rowView.findViewById(R.id.textViewMonth);
        TextView textCommentCount = rowView.findViewById(R.id.counterComments);
        TextView textLikeCount = rowView.findViewById(R.id.counterLikes);
        CircleImageView imageProfilePic = rowView.findViewById(R.id.imageProfilePic);
        ImageView imagePost = rowView.findViewById(R.id.imageViewPost);

        titleText.setText(eventTitle.get(position));
        textAuthor.setText(author.get(position));
        textDescription.setText(description.get(position));
        textVenue.setText(venue.get(position).getVenueName());
        textDay.setText(day.get(position).toString());
        textMonth.setText(month.get(position));
        textCommentCount.setText(countComments.get(position).toString());
        textLikeCount.setText(countLikes.get(position).toString());
        imageProfilePic.setImageResource(imageIDProfilePic.get(position));
        imagePost.setImageResource(imageIDPostPic.get(position));
        return rowView;

    }

}