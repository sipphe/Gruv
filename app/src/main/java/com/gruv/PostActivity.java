package com.gruv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gruv.adapters.CommentListAdapter;
import com.gruv.models.Author;
import com.gruv.models.Comment;
import com.gruv.models.Event;
import com.gruv.models.Like;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private ConstraintLayout appBarLayout;
    private ConstraintLayout layoutDesc;
    private ConstraintLayout layoutLikeComment;
    List<Comment> commentKeys = new ArrayList<>();
    private LinearLayout layoutProgress;
    private Toolbar toolbar;
    private Event postEvent;
    private String eventTitle;
    private ImageView postPic;
    private CircleImageView imageProfilePicture;
    private MaterialButton backImage;
    private MaterialButton buttonReadMore;
    private MaterialButton buttonAddComment;
    private ImageButton buttonLike;
    private ImageButton buttonCloseComment;
    private ImageButton buttonSend;
    private TextView textEventTitle;
    private TextView textAuthor;
    private TextView textDay;
    private TextView textMonth;
    private TextView textEventDescription;
    private TextView textVenue;
    private TextView likeCount, commentCount;
    private TextInputLayout textLayoutAddComment;
    private TextInputEditText editTextAddComment;
    private ConstraintLayout layoutAddComment, layoutVenue;
    private ScrollView scrollView;
    private NestedScrollView scrollViewComments;
    private Boolean liked = false;
    private Like thisLike;
    private CommentListAdapter adapter;
    private Author thisUser;
    Author author;
    private RecyclerView recyclerComments;
    private ConstraintLayout layoutAuthor;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private HashMap<String, Comment> comments;
    private LinearLayoutManager layoutManager;
    private Comment comment;
    private String eventId;
    private Like like;
    private ImageView imageVerified;
    private LinearLayout chipPassed;
    private TextView textDatePosted;
    private TextView textEventEndDate;
    private TextView textDatePostedText;
    private TextView textEventEndDateText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postEvent = (Event) getIntent().getSerializableExtra("Event");
        eventId = postEvent.getEventID();
        if (comments != null)
            comments.clear();
        comments = postEvent.getComments();
        initialiseControls();
        setCurrentUser();
        getAuthor();
        initialiseAdapter();
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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                layoutProgress.setVisibility(View.VISIBLE);
                getAuthorAndUpdateEvent(comments);
            }
        });
        thread.start();


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                collapseToolbar();
            }
        });

        scrollViewComments.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

//                if (scrollViewComments.getChildAt(0).getBottom()
//                        <= (scrollViewComments.getHeight() + scrollViewComments.getScrollY())) {
//                    //scroll view is at bottom
//                    layoutProgress.setVisibility(View.VISIBLE);
//                } else {
//                    //scroll view is not at bottom
//                    layoutProgress.setVisibility(View.GONE);
//                }
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
                    addLike();
                    liked = true;
                } else {
                    addLike();
                    buttonLike.setImageDrawable(getDrawable(R.drawable.sunshine));
                    removeLike(postEvent.getAuthor());
                    liked = false;
                }
                setPost();
            }
        });

        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCommentSectionFocus();
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
                if (!editTextAddComment.getText().toString().trim().equals("")) {
                    addComment(editTextAddComment.getText().toString().trim());
                    editTextAddComment.getText().clear();
                    layoutAddComment.setVisibility(View.INVISIBLE);
                    layoutLikeComment.setVisibility(View.VISIBLE);
                }
            }
        });

        layoutAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!postEvent.getAuthor().getId().equals(thisUser.getId())) {
                    Intent intent = new Intent(PostActivity.this, UserActivity.class);
                    intent.putExtra("selectedUser", postEvent.getAuthor());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            }
        });

        layoutVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(PostActivity.this)
                        .setTitle("Open maps?")
                        .setPositiveButton("Open", ((dialog, which) -> {
                            String uri = "http://maps.google.com/maps?q=loc:" + postEvent.getVenue().getLatitude() + "," + postEvent.getVenue().getLongitude();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            PostActivity.this.startActivity(intent);
                        }))
                        .setNegativeButton("Cancel", (dialog, which) -> {
                        })
                        .show();
            }
        });
    }

    public void getAuthor() {
        databaseReference.child("author").child(thisUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    thisUser = dataSnapshot.getValue(Author.class);
                    thisUser.setId(dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPost() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    if (eventSnapshot.getKey().equals(eventId)) {
                        postEvent = eventSnapshot.getValue(Event.class);
                        postEvent.setEventID(eventSnapshot.getKey());
                        setPost();
                        setComments(postEvent);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    if (eventSnapshot.getKey().equals(eventId)) {
                        postEvent = eventSnapshot.getValue(Event.class);
                        postEvent.setEventID(eventSnapshot.getKey());
                        setPost();
                        setComments(postEvent);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showSnackBar(databaseError.getMessage(), Snackbar.LENGTH_LONG);
            }
        });
    }

    private void initialiseAdapter() {
        Comment comment = new Comment();
        commentKeys.clear();
        if (postEvent.getComments() == null) {
            HashMap<String, Comment> comments = new HashMap<>();
            postEvent.setComments(comments);
            layoutProgress.setVisibility(View.GONE);
        }
        for (Map.Entry<String, Comment> commentEntry : postEvent.getComments().entrySet()) {
            comment = new Comment();
            comment.setCommentId(commentEntry.getKey());
            comment.setPostedDate(commentEntry.getValue().getPostedDate());
            commentKeys.add(comment);

        }
        Collections.sort(commentKeys, Collections.reverseOrder());
        adapter = new CommentListAdapter(this, postEvent, commentKeys, thisUser);
        recyclerComments.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerComments.setLayoutManager(layoutManager);
    }


    private void requestCommentSectionFocus() {
        layoutLikeComment.setVisibility(View.INVISIBLE);
        layoutAddComment.setVisibility(View.VISIBLE);
        scrollView.fullScroll(View.FOCUS_DOWN);
        editTextAddComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void setCurrentUser() {
        thisUser = new Author(currentUser.getUid(), currentUser.getDisplayName(), null, R.drawable.profile_pic4);
        if (currentUser.getPhotoUrl() != null)
            thisUser.setAvatar(currentUser.getPhotoUrl().toString());
        thisUser.setEmail(currentUser.getEmail());
    }

    public void initialiseControls() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbar = findViewById(R.id.main_app_toolbar);
        buttonReadMore = findViewById(R.id.buttonReadMore);
        recyclerComments = findViewById(R.id.listComments);
        backImage = findViewById(R.id.buttonBack);
        scrollView = findViewById(R.id.scrollViewProfile);
        scrollViewComments = findViewById(R.id.scrollViewComments);
        appBarLayout = findViewById(R.id.appBarLayout);
        layoutDesc = findViewById(R.id.layoutDescription);
        layoutProgress = findViewById(R.id.layout_progress);
        layoutAuthor = findViewById(R.id.layoutAuthor);
        layoutLikeComment = findViewById(R.id.layoutLikeComment);
        layoutAddComment = findViewById(R.id.layoutAddComment);
        layoutVenue = findViewById(R.id.layoutVenue);
        buttonAddComment = findViewById(R.id.buttonComment);
        buttonCloseComment = findViewById(R.id.buttonCloseComment);
        buttonLike = findViewById(R.id.buttonLike);
        textLayoutAddComment = findViewById(R.id.textInputLayout);
        editTextAddComment = findViewById(R.id.editTextComment);
        buttonSend = findViewById(R.id.buttonSend);
        imageVerified = findViewById(R.id.imageVerified);
        chipPassed = findViewById(R.id.chipEventPassed);
        textDatePosted = findViewById(R.id.textDatePosted);
        textEventEndDate = findViewById(R.id.textEventEndDate);
        textDatePostedText = findViewById(R.id.textDatePostedText);
        textEventEndDateText = findViewById(R.id.textEventEndDateText);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
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
        postPic = findViewById(R.id.imageViewPostPicture);
        imageProfilePicture = findViewById(R.id.imageProfilePic);
        textEventTitle = findViewById(R.id.textEventTitle);
        textAuthor = findViewById(R.id.textEventAuthor);
        textDay = findViewById(R.id.textViewDay);
        textMonth = findViewById(R.id.textViewMonth);
        textEventDescription = findViewById(R.id.textEventDescription);
        textVenue = findViewById(R.id.textVenue);
        likeCount = findViewById(R.id.textViewLikeCount);
        commentCount = findViewById(R.id.textviewCommentCount);

        try {
            Glide.with(this).load(postEvent.getImagePostUrl()).into(postPic);
            Glide.with(this).load(postEvent.getAuthor().getAvatar()).into(imageProfilePicture);
        } catch (Exception e) {
            showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
        }

        //postPic.setImageResource(postEvent.getImagePostId());
        //imageProfilePicture.setImageResource(postEvent.setCurrentUser().getProfilePictureId());
        eventTitle = postEvent.getEventName();
        textEventTitle.setText(eventTitle);
        textAuthor.setText("by " + postEvent.getAuthor().getName());
        textDay.setText(Integer.toString(postEvent.getEventDate().getDayOfMonth()));
        textMonth.setText(postEvent.getEventDate().getMonth().toString().substring(0, 3).toUpperCase());
        textEventDescription.setText(postEvent.getEventDescription());
        textVenue.setText(postEvent.getVenue().getVenueName());
        if (postEvent.getEventEndDate() != null) {
            if (postEvent.getEventEndDate().isBefore(LocalDateTime.now()))
                chipPassed.setVisibility(View.VISIBLE);
            else
                chipPassed.setVisibility(View.GONE);
        } else {
            if (postEvent.getEventDate().isBefore(LocalDateTime.now()))
                chipPassed.setVisibility(View.VISIBLE);
            else
                chipPassed.setVisibility(View.GONE);
        }
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
        setLiked(postEvent, thisUser);

        if (postEvent.getDatePosted() != null) {
            textDatePosted.setVisibility(View.VISIBLE);
            textDatePostedText.setVisibility(View.VISIBLE);
            textDatePosted.setText(dateString(LocalDateTime.parse(postEvent.getDatePosted(), DateTimeFormatter.ISO_DATE_TIME)));
        } else {
            textDatePosted.setVisibility(View.GONE);
            textDatePostedText.setVisibility(View.GONE);
        }

        if (postEvent.getEventEndDate() != null) {
            textEventEndDate.setVisibility(View.VISIBLE);
            textEventEndDateText.setVisibility(View.VISIBLE);
            textEventEndDate.setText(dateString(LocalDateTime.parse(postEvent.getEventEndDateString(), DateTimeFormatter.ISO_DATE_TIME)));
        } else {
            textEventEndDate.setVisibility(View.GONE);
            textEventEndDateText.setVisibility(View.GONE);
        }
    }

    private String dateString(LocalDateTime startDate) {
        String dateString = "";
        String minutes = "";
        if (startDate.getMinute() < 10) {
            minutes = "0" + startDate.getMinute();
        } else {
            minutes = startDate.getMinute() + "";
        }
        dateString = startDate.getDayOfMonth() + " " + startDate.getMonth().toString().substring(0, 3) + " " + startDate.getYear() + "   " + startDate.getHour() + ":" + minutes;
        return dateString;
    }

    public void setComments(Event postEvent) {
//        commentKeys.clear();
//        this.postEvent = postEvent;
//        for (Map.Entry<String, Comment> commentEntry : postEvent.getComments().entrySet()) {
//            commentKeys.add(commentEntry.getKey());
//        }

        initialiseAdapter();

        setPost();
    }

    public void addComment(String commentText) {
        Comment comment = new Comment();
        Map<String, Object> commentToAdd = new HashMap<>();
        comment.setCommentId(databaseReference.child("Event").child(eventId).child("comments").push().getKey());
        comment.setCommentText(commentText);
        comment.setAuthor(thisUser);
        comment.setPostedDate(LocalDateTime.now().toString());
        postEvent.addComment(comment);
        commentToAdd.put(comment.getCommentId(), comment);

        databaseReference.child("Event").child(eventId).child("comments").updateChildren(commentToAdd, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    showSnackBar(databaseError.getMessage(), Snackbar.LENGTH_LONG);
                }
                setComments(postEvent);
                setPost();
            }
        });
        postEvent.addComment(comment);
        setPost();
        setComments(postEvent);
//        if (postEvent.getComments() != null) {
//            if (postEvent.getComments().size() == 1)
//                commentCount.setText("1 COMMENT");
//            else
//                commentCount.setText(postEvent.getComments().size() + " COMMENTS");
//        }
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
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getIntent().hasExtra("CommentClicked")) {
                    requestCommentSectionFocus();
                }
            }
        }, 1000);

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

    public void addLike() {
        like = new Like();
        if (!liked) {
            like.setEventId(eventId);
            liked = true;
            addLikeToDB(like, postEvent);

        } else {
            liked = false;
            postEvent.removeLike(getUserLike(postEvent));
            removeLikeFromDB(getUserLike(postEvent), postEvent);
        }
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

    public void addLikeToDB(Like like, Event event) {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        Map<String, Object> likeToAdd = new HashMap<>();
        like.setLikeId(databaseReference.child("Event").child(event.getEventID()).child("likes").push().getKey());
        like.setAuthor(thisUser);
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
                setLiked(postEvent, thisUser);
            }
        });

    }

    public void setLiked(Event event, Author thisUser) {
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
            buttonLike.setImageResource(R.drawable.sunshine_clicked);
        } else {
            buttonLike.setImageResource(R.drawable.sunshine);
        }

        if (event.getLikes() == null || event.getLikes().isEmpty())
            likeCount.setText("0");
        else if (event.getLikes().size() == 1)
            likeCount.setText("1 LIKE");
        else
            likeCount.setText(event.getLikes().size() + " LIKES");
    }

    public void removeLike(Author author) {
        postEvent.removeLike(thisLike);
        if (postEvent.getLikes().size() == 1)
            likeCount.setText("1 LIKE");
        else
            likeCount.setText(postEvent.getLikes().size() + " LIKES");
    }

    public void showSnackBar(String message, Integer length) {
        try {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, message, length).show();
        } catch (Exception e) {
            Log.w("Snackbar Error", "Couldn't load Snackbar");
        }
    }

    private void getAuthorAndUpdateEvent(HashMap<String, Comment> comments) {
        comment = new Comment();
        if (comments != null) {
            for (Map.Entry<String, Comment> commentEntry : comments.entrySet()) {
                databaseReference.child("author").child(commentEntry.getValue().getAuthor().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        author = dataSnapshot.getValue(Author.class);
                        author.setId(dataSnapshot.getKey());
                        comment = commentEntry.getValue();
                        comment.setCommentId(commentEntry.getKey());
                        postEvent.addComment(comment);
                        updateEvent(postEvent.getEventID(), author, comment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        } else
            layoutProgress.setVisibility(View.GONE);
    }

    private void updateEvent(String eventId, Author author, Comment comment) {
        Map<String, Object> user = new HashMap<>();
        user.put("author", author);
        databaseReference.child("Event").child(eventId).child("comments").child(comment.getCommentId()).updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                layoutProgress.setVisibility(View.GONE);
                if (databaseError != null) {
                    showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                } else {
                    getPost();
                }
            }
        });
    }
}

