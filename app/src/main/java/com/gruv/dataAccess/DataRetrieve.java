package com.gruv.dataAccess;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gruv.models.Author;
import com.gruv.models.Comment;
import com.gruv.models.Event;
import com.gruv.models.Like;
import com.gruv.models.Venue;

import java.util.List;

public class DataRetrieve {
    private FirebaseAuth authenticateObj;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Object event;
    private List<Event> allEvents;
    private Context context;
    private String id;

    public DataRetrieve(@NonNull Context context, String id) {
        authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        this.context = context;
        this.id = id;
    }

    public List<Event> getAllEvents() {
        Venue venue;
        Author eventAuthor;
        List<Like> eventLikes;
        List<Comment> eventComments;
        Integer imagePostId;

        databaseReference.child("Event").child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue();

                Toast.makeText(context, Boolean.toString(dataSnapshot.exists()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return allEvents;
    }
}
