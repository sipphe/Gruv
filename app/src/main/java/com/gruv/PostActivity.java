package com.gruv;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.gruv.models.Author;
import com.gruv.models.Event;
import com.gruv.models.Like;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    ConstraintLayout appBarLayout;
    ConstraintLayout layoutDesc;
    Toolbar toolbar;
    Event postEvent;
    String eventTitle;
    ImageView postPic;
    CircleImageView imageProfilePicture;
    MaterialButton buttonReadMore;
    ImageButton buttonLike;
    TextView textEventTitle;
    TextView textAuthor;
    TextView textDay;
    TextView textMonth;
    TextView textEventDescription;
    TextView textVenue;
    TextView likeCount, commentCount;
    ScrollView scrollView;
    Boolean liked = false;
    Like thisLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postEvent = (Event) getIntent().getSerializableExtra("Event");
        toolbar = findViewById(R.id.main_app_toolbar);
        buttonReadMore = findViewById(R.id.buttonReadMore);

        setTransparentStatusBar();
        setTopPadding(getStatusBarHeight());
        setPost();

        MaterialButton backImage = findViewById(R.id.buttonBack);
        scrollView = findViewById(R.id.scrollViewPost);
        appBarLayout = findViewById(R.id.appBarLayout);
        layoutDesc = findViewById(R.id.layoutDescription);
        buttonLike = findViewById(R.id.buttonLike);
        textEventDescription.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        int height = textEventDescription.getLineCount();
        if (height >= 6) {
            textEventDescription.setMaxLines(5);
            buttonReadMore.setVisibility(View.VISIBLE);
        } else {
            buttonReadMore.setVisibility(View.GONE);
        }

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                collapseToolbar();
            }
        });


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEventDescription.setMaxLines(100);
                layoutDesc.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                buttonReadMore.setVisibility(View.GONE);
            }
        });

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!liked) {
                    buttonLike.setImageDrawable(getDrawable(R.drawable.sunshine_clicked));
                    addLike(postEvent.getAuthor());
                    liked = true;
                } else {
                    buttonLike.setImageDrawable(getDrawable(R.drawable.sunshine));
                    removeLike(postEvent.getAuthor());
                    liked = false;
                }
            }
        });
    }

    private void collapseToolbar() {
        int scrollY = scrollView.getScrollY();
        if (scrollY > 544) {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(PostActivity.this, R.color.colorPrimary));
            toolbar.setTitle(eventTitle + " - " + postEvent.getEventDate().getDayOfMonth() + " " + postEvent.getEventDate().getMonth().toString().substring(0, 3));
        } else {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(PostActivity.this, R.color.transparent));
            toolbar.setTitle("Event");
        }
    }

    public void setPost() {
        postPic = findViewById(R.id.imageViewPost);
        imageProfilePicture = findViewById(R.id.imageProfilePic);
        textEventTitle = findViewById(R.id.textEventTitle);
        textAuthor = findViewById(R.id.textEventAuthor);
        textDay = findViewById(R.id.textViewDay);
        textMonth = findViewById(R.id.textViewMonth);
        textEventDescription = findViewById(R.id.textEventDescription);
        textVenue = findViewById(R.id.textVenue);
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
        textVenue.setText(postEvent.getVenue().getVenueName());
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

    @Override
    public void onResume() {
        super.onResume();
        textEventDescription.post(new Runnable() {
            @Override
            public void run() {
                int height = textEventDescription.getLineCount();
                if (height >= 6) {
                    textEventDescription.setMaxLines(5);
                    buttonReadMore.setVisibility(View.VISIBLE);
                } else {
                    buttonReadMore.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setTransparentStatusBar() {
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


    }

    public void setTopPadding(int topPadding) {
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.setPadding(0, topPadding, 0, 0);
    }

    public void addLike(Author author) {
        thisLike = new Like(postEvent.getEventId(), author);
        postEvent.addLike(thisLike);
        likeCount.setText(postEvent.getLikes().size() + " LIKES");
    }

    public void removeLike(Author author) {
        postEvent.removeLike(thisLike);
        likeCount.setText(postEvent.getLikes().size() + " LIKES");
    }
}

