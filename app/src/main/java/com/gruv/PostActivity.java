package com.gruv;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.gruv.models.Event;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    ConstraintLayout appBarLayout;
    Toolbar toolbar;
    Event postEvent;
    String eventTitle;
    ImageView postPic;
    CircleImageView imageProfilePicture;
    TextView textEventTitle;
    TextView textAuthor;
    TextView textDay;
    TextView textMonth;
    TextView textEventDescription;
    TextView likeCount, commentCount;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postEvent = (Event) getIntent().getSerializableExtra("Event");
        toolbar = findViewById(R.id.main_app_toolbar);

        setTransparentStatusBar();
        setTopPadding(getStatusBarHeight());
        setPost();

        MaterialButton backImage = findViewById(R.id.buttonBack);
        scrollView = findViewById(R.id.scrollViewPost);
        appBarLayout = findViewById(R.id.appBarLayout);


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                int scrollY = scrollView.getScrollY();
                if (scrollY > 544) {
                    appBarLayout.setBackgroundColor(ContextCompat.getColor(PostActivity.this, R.color.colorPrimary));
                    toolbar.setTitle(eventTitle + " - " + postEvent.getEventDate().getDayOfMonth() + " " + postEvent.getEventDate().getMonth().toString().substring(0, 3));
                } else {
                    appBarLayout.setBackgroundColor(ContextCompat.getColor(PostActivity.this, R.color.transparent));
                    toolbar.setTitle("Event");
                }
            }
        });


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void setPost() {
        postPic = findViewById(R.id.imageViewPost);
        imageProfilePicture = findViewById(R.id.imageProfilePic);
        textEventTitle = findViewById(R.id.textEventTitle);
        textAuthor = findViewById(R.id.textEventAuthor);
        textDay = findViewById(R.id.textViewDay);
        textMonth = findViewById(R.id.textViewMonth);
        textEventDescription = findViewById(R.id.textEventDescription);
        likeCount = findViewById(R.id.textviewLikeCount);
        commentCount = findViewById(R.id.textviewCommentCount);

        postPic.setImageResource(postEvent.getImagePostId());
        imageProfilePicture.setImageResource(postEvent.getAuthor().getProfilePictureId());
        eventTitle = postEvent.getEventName();
        textEventTitle.setText(eventTitle);
        textAuthor.setText("by " + postEvent.getAuthor().getName());
        textDay.setText(Integer.toString(postEvent.getEventDate().getDayOfMonth()));
        textMonth.setText(postEvent.getEventDate().getMonth().toString().substring(0, 3).toUpperCase());
        textEventDescription.setText(postEvent.getEventDescription());
        if (postEvent.getLikes() != null)
            likeCount.setText(postEvent.getLikes().size() + " LIKES");
        if (postEvent.getComments() != null)
            commentCount.setText(postEvent.getComments().size() + " COMMENTS");
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setTransparentStatusBar() {
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


    }

    public void setTopPadding(int topPadding) {
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.setPadding(0, topPadding, 0, 0);
    }
}

