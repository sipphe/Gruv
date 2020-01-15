package com.gruv;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gruv.com.gruv.CommentListAdapter;
import com.gruv.models.Author;
import com.gruv.models.Comment;
import com.gruv.models.Event;
import com.gruv.models.Like;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    ConstraintLayout appBarLayout;
    ConstraintLayout layoutDesc;
    ConstraintLayout layoutLikeComment;
    ConstraintLayout layoutAddComment;
    LinearLayout layoutProgress;
    Toolbar toolbar;
    Event postEvent;
    String eventTitle;
    ImageView postPic;
    CircleImageView imageProfilePicture;
    MaterialButton backImage;
    MaterialButton buttonReadMore;
    MaterialButton buttonAddComment;
    ImageButton buttonLike;
    ImageButton buttonCloseComment;
    ImageButton buttonSend;
    TextView textEventTitle;
    TextView textAuthor;
    TextView textDay;
    TextView textMonth;
    TextView textEventDescription;
    TextView textVenue;
    TextView likeCount, commentCount;
    TextInputLayout textLayoutAddComment;
    TextInputEditText editTextAddComment;
    ListView commentList;
    ScrollView scrollView;
    NestedScrollView scrollViewComments;
    Boolean liked = false;
    Like thisLike;
    CommentListAdapter adapter;
    Author currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postEvent = (Event) getIntent().getSerializableExtra("Event");

        currentUser = getAuthor();
        initialiseControls();
        setTransparentStatusBar();
        setTopPadding(getStatusBarHeight());
        setPost();

        textEventDescription.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        int height = textEventDescription.getLineCount();
        if (height >= 6) {
            textEventDescription.setMaxLines(5);
            buttonReadMore.setVisibility(View.VISIBLE);
        } else {
            buttonReadMore.setVisibility(View.GONE);
        }

        setComments();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                collapseToolbar();
            }
        });

        scrollViewComments.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if (scrollViewComments.getChildAt(0).getBottom()
                        <= (scrollViewComments.getHeight() + scrollViewComments.getScrollY())) {
                    //scroll view is at bottom
                    layoutProgress.setVisibility(View.VISIBLE);
                } else {
                    //scroll view is not at bottom
                    layoutProgress.setVisibility(View.GONE);
                }
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

        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLikeComment.setVisibility(View.INVISIBLE);
                layoutAddComment.setVisibility(View.VISIBLE);
                scrollView.fullScroll(View.FOCUS_DOWN);
                editTextAddComment.requestFocus();
            }
        });

        buttonCloseComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextAddComment.getText().toString().matches("")) {
                    layoutAddComment.setVisibility(View.INVISIBLE);
                    layoutLikeComment.setVisibility(View.VISIBLE);
                } else {
                    editTextAddComment.getText().clear();
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(editTextAddComment.getText().toString().trim());
                editTextAddComment.getText().clear();
                layoutAddComment.setVisibility(View.INVISIBLE);
                layoutLikeComment.setVisibility(View.VISIBLE);
            }
        });

    }

    private Author getAuthor() {
        //TODO get authorCode
        return new Author("1234", "This User", null, R.drawable.profile_pic6);
    }

    public void initialiseControls() {
        toolbar = findViewById(R.id.main_app_toolbar);
        buttonReadMore = findViewById(R.id.buttonReadMore);
        commentList = findViewById(R.id.listComments);
        backImage = findViewById(R.id.buttonBack);
        scrollView = findViewById(R.id.scrollViewPost);
        scrollViewComments = findViewById(R.id.scrollViewComments);
        appBarLayout = findViewById(R.id.appBarLayout);
        layoutDesc = findViewById(R.id.layoutDescription);
        layoutProgress = findViewById(R.id.layout_progress);
        layoutLikeComment = findViewById(R.id.layoutLikeComment);
        layoutAddComment = findViewById(R.id.layoutAddComment);
        buttonAddComment = findViewById(R.id.buttonComment);
        buttonCloseComment = findViewById(R.id.buttonCloseComment);
        buttonLike = findViewById(R.id.buttonLike);
        textLayoutAddComment = findViewById(R.id.textInputLayout);
        editTextAddComment = findViewById(R.id.editTextComment);
        buttonSend = findViewById(R.id.buttonSend);
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
        postPic = findViewById(R.id.imageViewProfilePicture);
        imageProfilePicture = findViewById(R.id.imageProfilePic);
        textEventTitle = findViewById(R.id.textEventTitle);
        textAuthor = findViewById(R.id.textEventAuthor);
        textDay = findViewById(R.id.textViewDay);
        textMonth = findViewById(R.id.textViewMonth);
        textEventDescription = findViewById(R.id.textEventDescription);
        textVenue = findViewById(R.id.textVenue);
        likeCount = findViewById(R.id.textViewLikeCount);
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
        if (postEvent.getLikes() != null) {
            if (postEvent.getLikes().size() == 1)
                likeCount.setText("1 LIKE");
            else
                likeCount.setText(postEvent.getLikes().size() + " LIKES");
        }
        if (postEvent.getComments() != null) {
            if (postEvent.getComments().size() == 1)
                commentCount.setText("1 COMMENT");
            else
                commentCount.setText(postEvent.getComments().size() + " COMMENTS");
        }

    }

    public void setComments() {
        List<String> strings = new ArrayList<>();
        if (postEvent.getComments() != null) {
            for (int i = 0; i < postEvent.getComments().size(); i++) {
                strings.add(postEvent.getComments().get(i).getCommentText());
            }

        }
        adapter = new CommentListAdapter(this, postEvent.getComments(), strings);


        commentList.setAdapter(adapter);
    }

    public void addComment(String commentText) {
        int newCommentId;
        if (postEvent.getComments() != null)
            newCommentId = Integer.parseInt(postEvent.getComments().get(postEvent.getComments().size() - 1).getCommentId()) + 1;
        else
            newCommentId = 0;

        Comment comment = new Comment(newCommentId + "", postEvent.getEventId(), commentText, currentUser);

        postEvent.addComment(comment);
        setComments();
        if (postEvent.getComments() != null) {
            if (postEvent.getComments().size() == 1)
                commentCount.setText("1 COMMENT");
            else
                commentCount.setText(postEvent.getComments().size() + " COMMENTS");
        }
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
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);


    }

    public void setTopPadding(int topPadding) {
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.setPadding(0, topPadding, 0, 0);
    }

    public void addLike(Author author) {
        thisLike = new Like(postEvent.getEventId(), author);
        postEvent.addLike(thisLike);
        if (postEvent.getLikes().size() == 1)
            likeCount.setText("1 LIKE");
        else
            likeCount.setText(postEvent.getLikes().size() + " LIKES");
    }

    public void removeLike(Author author) {
        postEvent.removeLike(thisLike);
        if (postEvent.getLikes().size() == 1)
            likeCount.setText("1 LIKE");
        else
            likeCount.setText(postEvent.getLikes().size() + " LIKES");
    }
}

