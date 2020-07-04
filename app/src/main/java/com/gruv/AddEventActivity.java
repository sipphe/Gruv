package com.gruv;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.fenchtose.nocropper.BitmapResult;
import com.fenchtose.nocropper.CropMatrix;
import com.fenchtose.nocropper.CropState;
import com.fenchtose.nocropper.CropperView;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gruv.models.Author;
import com.gruv.models.Event;
import com.gruv.models.Venue;
import com.gruv.utils.BitmapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddEventActivity extends AppCompatActivity {
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    protected static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_READ_PERMISSION = 22;
    private static final int REQUEST_GALLERY = 21;
    private static final String TAG = "AddEventActivity";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    int hoursStart, minutesStart, hoursEnd, minutesEnd;
    private MainActivity mainActivity = Base.getMainActivity();
    private HashMap<String, CropMatrix> matrixMap = new HashMap<>();
    private CropperView imagePost;
    private MaterialButton buttonAddPicture, buttonNext, buttonNext1, buttonNext2, buttonSkip, buttonPost;
    private TextInputLayout textLayoutName, textLayoutTime, textLayoutTimeEnd, textLayoutVenue, textLayoutDescription, textLayoutTicketLink;
    private TextInputEditText textInputName, textTime, textTimeEnd, textVenue, textDescription, textTicketLink;
    private TextView textEventNameVerify, textEndDate, textEventStartDate;
    private CircleImageView imageProfilePicturePost;
    private ConstraintLayout appBarLayout, layoutStep1, layoutStep2, layoutStep3, layoutStep4, layoutEndDate;
    private MaterialButton buttonBack, buttonSnap, buttonRotate;
    private ProgressBar progressBreadcrumbs;
    private Toolbar toolbar;
    private DatePicker datePickerStart, datePickerEnd;
    private ImageView imagePostVerify;
    private Uri filePath;
    private String currentFilePath, pathCropped;
    private int rotationCount;
    private Event event = new Event();
    private Venue venue = new Venue();
    private Bitmap bitmap, bitmapCropped;
    private boolean isSnappedToCenter = false, pictureAdded = false, ticketLinkValid = false, venueValidated = false, bitmapSaved = false;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private Author thisUser;
    private String url;
    private Uri pathCroppedUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setTransparentStatusBar();

        FirebaseAuth authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();

        initialiseControls();
        checkPermissions(this);
        getAuthor();
        setTopPadding(getStatusBarHeight());

        progressBreadcrumbs.setProgress(20);
        datePickerStart.setMinDate(System.currentTimeMillis());
//        View pickerHeader = datePickerStart.getChildAt(0);
//        pickerHeader.setVisibility(View.GONE);
//        pickerHeader =  datePickerEnd.getChildAt(0);
//        pickerHeader.setVisibility(View.GONE);
        LocalDateTime now = LocalDateTime.now();
        hoursStart = now.getHour();
        minutesStart = now.getMinute();
        textTime.setText(hoursStart + ":" + String.format("%02d", minutesStart));


        buttonBack.setOnClickListener(v -> {
            onBackPressed();
        });

        buttonAddPicture.setOnClickListener(v -> {
            try {
                startGalleryIntent();
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "No gallery: " + e);
            }
        });
        buttonSnap.setOnClickListener(v -> snapImage());

        buttonRotate.setOnClickListener(v -> rotateImage());

        textInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isNameValid(s);
            }
        });

        buttonNext.setOnClickListener(v -> {
            if (isNameValid(textInputName.getText()) && initialise4thLayout()) {
                event.setEventName(textInputName.getText().toString().trim());
                layoutStep1.setVisibility(View.GONE);
                layoutStep2.setVisibility(View.VISIBLE);
                progressBreadcrumbs.setProgress(40);
                appBarLayout.setBackgroundColor(ContextCompat.getColor(AddEventActivity.this, R.color.colorPrimary));
                toolbar.setTitle("When does it start?");
            }
        });

        textTime.setOnClickListener(v -> {
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(AddEventActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                hoursStart = selectedHour;
                minutesStart = selectedMinute;
                textTime.setText(hoursStart + ":" + String.format("%02d", minutesStart));
            }, hoursStart, minutesStart, true);
            mTimePicker.show();

        });

        buttonNext1.setOnClickListener(v -> {
            LocalDateTime eventStartDate = LocalDateTime.of(datePickerStart.getYear(), datePickerStart.getMonth() + 1, datePickerStart.getDayOfMonth(), hoursStart, minutesStart);
            event.setEventDate(eventStartDate);
            ZonedDateTime zdt = eventStartDate.atZone(ZoneId.systemDefault());
            long millis = zdt.toInstant().toEpochMilli();
            datePickerEnd.setMinDate(millis);
            datePickerEnd.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
                compareDateTime();

            });
            textTimeEnd.setText(eventStartDate.getHour() + ":" + String.format("%02d", eventStartDate.getMinute()));
            layoutStep2.setVisibility(View.GONE);
            layoutStep3.setVisibility(View.VISIBLE);
            progressBreadcrumbs.setProgress(60);
            toolbar.setTitle("When does it end?");
        });

        textTimeEnd.setOnClickListener(v -> {
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(AddEventActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                hoursEnd = selectedHour;
                minutesEnd = selectedMinute;
                compareDateTime();
            }, event.getEventDate().getHour(), event.getEventDate().getMinute(), true);

            mTimePicker.show();
        });

        buttonNext2.setOnClickListener(v -> {
            LocalDateTime eventEndDate = LocalDateTime.of(datePickerEnd.getYear(), datePickerEnd.getMonth() + 1, datePickerEnd.getDayOfMonth(), hoursEnd, minutesEnd);
            event.setEventEndDate(eventEndDate);
            event.setEventEndDateString(eventEndDate.format(DateTimeFormatter.ISO_DATE_TIME));

            if (initialise4thLayout()) {
                layoutStep3.setVisibility(View.GONE);
                layoutStep4.setVisibility(View.VISIBLE);
                progressBreadcrumbs.setProgress(80);
                toolbar.setTitle("Just a few more details");
            }
        });

        buttonSkip.setOnClickListener(v -> {
            event.setEventEndDate(null);
            event.setEventEndDateString(null);
            if (initialise4thLayout()) {
                layoutStep3.setVisibility(View.GONE);
                layoutStep4.setVisibility(View.VISIBLE);
                progressBreadcrumbs.setProgress(80);
                toolbar.setTitle("Just a few more details");
            }
        });

        textVenue.setOnClickListener(v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields).setCountry("ZA")
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });
        textVenue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateVenue(s);
            }
        });

        textTicketLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateTicketLink(s);
            }
        });

        buttonPost.setOnClickListener(v -> {
            ProgressBar progressBar = findViewById(R.id.progressPost);
            progressBar.setVisibility(View.VISIBLE);
            validateVenue(textVenue.getText());
            validateTicketLink(textTicketLink.getText());
            if (venueValidated && ticketLinkValid) {
                if (pictureAdded)
                    saveBitmapToFile(event.getEventName() + "_" + System.currentTimeMillis(), bitmapCropped);
                if (bitmapSaved) {
                    event.setVenue(venue);
                    if (textDescription.getText().toString().trim().length() != 0)
                        event.setEventDescription(textDescription.getText().toString().trim());
                    if (textTicketLink.getText().toString().trim().length() != 0)
                        event.setTicketLink(textTicketLink.getText().toString().trim());
                    event.setEventID(databaseReference.child("Event").push().getKey());
                    thisUser.addEvent(event.getEventID());
                    event.setAuthor(thisUser);
                    event.setDatePosted(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

                    finish();
                    mainActivity.showPostingProgress(event, bitmapCropped);

                    uploadImage(pathCroppedUri, FirebaseStorage.getInstance().getReference());
//                    databaseReference.child("Event").


                }
            } else {
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    private void uploadImage(Uri filePath, StorageReference storageReference) {

        if (filePath != null) {
            String path = currentUser.getUid() + "/posts/" + event.getEventID();
            StorageReference ref = storageReference.child(path);


            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url = uri.toString();
                                    updateEvent(uri);

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    mainActivity.hidePostingProgress("Something went wrong");
                    e.printStackTrace();
                }
            });
        }
    }

    private void updateEvent(Uri uri) {
        event.setEventDate(null);
        event.setEventEndDate(null);
        event.setImagePostUrl(uri.toString());
        Map<String, Object> eventToAdd = new HashMap<>();
        eventToAdd.put(event.getEventID(), event);


        databaseReference.child("Event").updateChildren(eventToAdd, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                updateUser();
            }
        });
    }

    private void updateUser() {
        Map<String, Object> user = new HashMap<>();
        user.put(thisUser.getId(), thisUser);

        databaseReference.child("author").updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null)
                    mainActivity.hidePostingProgress(databaseError.getMessage());
                else
                    mainActivity.hidePostingProgress(null);
            }
        });
    }


    private void checkPermissions(Context context) {
        int camera = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
        int readStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStorage = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        }
    }

    private void validateVenue(Editable s) {
        if (s.toString().trim().length() == 0) {

            buttonPost.setEnabled(false);
            textLayoutVenue.setError("The event has to have a venue");
        } else {
            if (ticketLinkValid)
                buttonPost.setEnabled(true);
            textLayoutVenue.setError(null);
            venueValidated = true;
        }
    }

    public void validateTicketLink(Editable s) {
        String regex = "[\\x00-\\x7F]+\\.+[\\x00-\\x7F]+";
//        String regex = "_(^|[\\s.:;?\\-\\]<\\(])(https?://[-\\w;/?:@&=+$\\|\\_.!~*\\|'()\\[\\]%#,☺]+[\\w/#](\\(\\))?)(?=$|[\\s',\\|\\(\\).:;?\\-\\[\\]>\\)])_i";

        if (s.toString().trim().length() > 0 && !s.toString().trim().matches(regex)) {
            buttonPost.setEnabled(false);
            textLayoutTicketLink.setError("This doesn't seem to be a link");
        } else {
            if (venueValidated)
                buttonPost.setEnabled(true);

            textLayoutTicketLink.setError(null);
            ticketLinkValid = true;
        }
    }

    private void compareDateTime() {
        textTimeEnd.setText(hoursEnd + ":" + String.format("%02d", minutesEnd));
        int timeStart = Integer.parseInt(hoursStart + String.format("%02d", minutesStart));
        int timeEnd = Integer.parseInt(hoursEnd + String.format("%02d", minutesEnd));
        LocalDate dateStart = event.getEventDate().toLocalDate(), dateEnd = LocalDate.of(datePickerEnd.getYear(), datePickerEnd.getMonth() + 1, datePickerEnd.getDayOfMonth());
        int comparison = dateStart.compareTo(dateEnd);
        if (comparison == 0 && timeEnd < timeStart) {
            textLayoutTimeEnd.setError("Event can't end before it started");
            buttonNext2.setEnabled(false);
        } else {
            textLayoutTimeEnd.setError(null);
            buttonNext2.setEnabled(true);
        }

    }

    private boolean initialise4thLayout() {
        if (pictureAdded) {
            BitmapResult result = imagePost.getCroppedBitmap();

            if (result.getState() == CropState.FAILURE_GESTURE_IN_PROCESS) {
                showSnackBar("Unable to crop, Gesture in progress", Snackbar.LENGTH_LONG);
                return false;
            }

            bitmapCropped = result.getBitmap();
            imagePostVerify.setImageBitmap(bitmapCropped);
        } else {
            imagePostVerify.setImageDrawable(getDrawable(R.drawable.ic_event_busy_black_24dp));
        }
        textEventNameVerify.setText(event.getEventName());

        if (event.getEventDate() != null)
            textEventStartDate.setText("Starts at " + event.getEventDate().getHour() + ":" + String.format("%02d", event.getEventDate().getMinute()) + " on " + event.getEventDate().getMonth() + " " + event.getEventDate().getDayOfMonth() + ", " + event.getEventDate().getYear());

        if (event.getEventEndDate() != null) {
            layoutEndDate.setVisibility(View.VISIBLE);
            textEndDate.setText("Ends at " + event.getEventEndDate().getHour() + ":" + String.format("%02d", event.getEventEndDate().getMinute()) + " on " + event.getEventEndDate().getMonth() + " " + event.getEventEndDate().getDayOfMonth() + ", " + event.getEventEndDate().getYear());
        } else {
            layoutEndDate.setVisibility(View.GONE);
        }

//

        return true;
    }

    private void getAuthor() {
        databaseReference.child("author").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisUser = dataSnapshot.getValue(Author.class);
                thisUser.setId(dataSnapshot.getKey());
                if (thisUser.getAvatar() != null) {
                    Glide.with(AddEventActivity.this).load(thisUser.getAvatar()).into(imageProfilePicturePost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean isNameValid(Editable text) {
        if (text.toString().trim().length() == 0) {
            textLayoutName.setError("Did you forget to type the event name?");
            buttonNext.setEnabled(false);
            return false;
        } else {
            textLayoutName.setError(null);
            buttonNext.setEnabled(true);
            return true;
        }

    }

    private void initialiseControls() {
        imagePost = findViewById(R.id.cropper_view);
        buttonAddPicture = findViewById(R.id.buttonAddPicture);
        appBarLayout = findViewById(R.id.appBarLayout);
        buttonBack = findViewById(R.id.buttonBack);
        buttonNext = findViewById(R.id.buttonNext);
        buttonSnap = findViewById(R.id.buttonCrop);
        buttonRotate = findViewById(R.id.buttonRotate);
        textLayoutName = findViewById(R.id.textLayoutName);
        textLayoutTime = findViewById(R.id.textLayoutTime);
        textInputName = findViewById(R.id.textInputName);
        textTime = findViewById(R.id.textTime);
        buttonNext1 = findViewById(R.id.buttonNext1);
        textLayoutTimeEnd = findViewById(R.id.textLayoutTimeEnd);
        textTimeEnd = findViewById(R.id.textTimeEnd);
        textLayoutVenue = findViewById(R.id.textLayoutVenue);
        textVenue = findViewById(R.id.textVenue);
        buttonNext2 = findViewById(R.id.buttonNext2);
        buttonSkip = findViewById(R.id.buttonSkip);
        progressBreadcrumbs = findViewById(R.id.progressBreadcrumbs);
        toolbar = findViewById(R.id.main_app_toolbar);
        datePickerStart = findViewById(R.id.datePickerStart);
        datePickerEnd = findViewById(R.id.datePickerEnd);
        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);
        layoutStep3 = findViewById(R.id.layoutStep3);
        layoutStep4 = findViewById(R.id.layoutStep4);
        imagePostVerify = findViewById(R.id.imagePostVerify);
        textEventNameVerify = findViewById(R.id.textEventName);
        textEventStartDate = findViewById(R.id.textEventStartDate);
        layoutEndDate = findViewById(R.id.layoutEndDate);
        textEndDate = findViewById(R.id.textEndDate);
        textDescription = findViewById(R.id.textDescription);
        textLayoutDescription = findViewById(R.id.textLayoutDescription);
        textTicketLink = findViewById(R.id.textTicketLink);
        textLayoutTicketLink = findViewById(R.id.textLayoutTicketLink);
        imageProfilePicturePost = findViewById(R.id.imageProfilePic);
        buttonPost = findViewById(R.id.buttonPost);
        // Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);
    }

    private void startGalleryIntent() {

        if (currentFilePath != null) {
            matrixMap.put(currentFilePath, imagePost.getCropMatrix());
        }

        if (!hasGalleryPermission()) {
            askForGalleryPermission();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private boolean hasGalleryPermission() {
        return ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    private void askForGalleryPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_READ_PERMISSION);
    }

    public void setTransparentStatusBar() {
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setTopPadding(int topPadding) {
        appBarLayout.setPadding(0, topPadding, 0, 0);
        layoutStep4.setPadding(0, topPadding + appBarLayout.getHeight(), 0, 0);
        layoutStep3.setPadding(0, topPadding + appBarLayout.getHeight(), 0, 0);
        layoutStep2.setPadding(0, topPadding + appBarLayout.getHeight(), 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);


        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (responseCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(resultIntent);
                if (place.getName() != null)
                    venue.setVenueName(place.getName());
                else
                    venue.setVenueName(place.getAddress());

                if (place.getLatLng() != null)
                    venue.setLatLong(place.getLatLng().latitude, place.getLatLng().longitude);

                if (place.getAddress() != null)
                    venue.setAddress(place.getAddress());
                textVenue.setText(venue.getVenueName());
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (responseCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                Status status = Autocomplete.getStatusFromIntent(resultIntent);
                Log.i(TAG, status.getStatusMessage());
            } else if (responseCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (responseCode == RESULT_OK) {
            String absPath = BitmapUtils.getFilePathFromUri(this, resultIntent.getData());
            loadNewImage(absPath);
            buttonAddPicture.setText("Change Picture");
        }
    }

    private void loadNewImage(String filePath) {
        pictureAdded = true;
        this.currentFilePath = filePath;
        rotationCount = 0;
        Log.i(TAG, "load image: " + filePath);
        bitmap = BitmapFactory.decodeFile(filePath);
        Log.i(TAG, "bitmap: " + bitmap.getWidth() + " " + bitmap.getHeight());

        int maxP = Math.max(bitmap.getWidth(), bitmap.getHeight());
        float scale1280 = (float) maxP / 1280;
        Log.i(TAG, "scaled: " + scale1280 + " - " + (1 / scale1280));
        imagePost.setImageBitmap(bitmap);
        if (imagePost.getWidth() != 0) {
            imagePost.setMaxZoom(imagePost.getWidth() * 2 / 1280f);
        } else {

            ViewTreeObserver vto = imagePost.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    imagePost.getViewTreeObserver().removeOnPreDrawListener(this);
                    imagePost.setMaxZoom(imagePost.getWidth() * 2 / 1280f);
                    return true;
                }
            });

            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale1280),
                    (int) (bitmap.getHeight() / scale1280), true);

            imagePost.setImageBitmap(bitmap);
            final CropMatrix matrix = matrixMap.get(filePath);
            if (matrix != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imagePost.setCropMatrix(matrix, true);
                    }
                }, 30);
            }
        }
    }

    private void rotateImage() {
        if (bitmap == null) {
            Log.e(TAG, "bitmap is not loaded yet");
            return;
        }

        bitmap = BitmapUtils.rotateBitmap(bitmap, 90);
        imagePostVerify.setImageBitmap(bitmap);
        rotationCount++;
    }

    private void snapImage() {
        if (isSnappedToCenter) {
            imagePost.cropToCenter();
        } else {
            imagePost.fitToCenter();
        }
        isSnappedToCenter = !isSnappedToCenter;
    }

    public void saveBitmapToFile(String filename, Bitmap bitmap) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + getResources().getString(R.string.app_name));
        directory.mkdirs();
        pathCropped = directory + "/" + filename + ".png";
        try (FileOutputStream out = new FileOutputStream(pathCropped)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            bitmapSaved = true;
        } catch (IOException e) {
            showSnackBar(e.getMessage(), Snackbar.LENGTH_LONG);
            e.printStackTrace();
            bitmapSaved = false;
        }
        directory = new File(directory + "/" + filename + ".png");
        pathCroppedUri = Uri.fromFile(directory);
    }

    public void showSnackBar(String message, Integer length) {
        try {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, message, length).show();
        } catch (Exception e) {
            Log.w("Snackbar Error", "Couldn't load Snackbar");
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutStep1.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else if (layoutStep2.getVisibility() == View.VISIBLE) {
            progressBreadcrumbs.setProgress(20);
            appBarLayout.setBackgroundColor(ContextCompat.getColor(AddEventActivity.this, R.color.transparent));
            toolbar.setTitle("Add a new event");
            layoutStep2.setVisibility(View.GONE);
            layoutStep1.setVisibility(View.VISIBLE);
        } else if (layoutStep3.getVisibility() == View.VISIBLE) {
            layoutStep3.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
            progressBreadcrumbs.setProgress(40);
            appBarLayout.setBackgroundColor(ContextCompat.getColor(AddEventActivity.this, R.color.colorPrimary));
            toolbar.setTitle("When does it start?");
        } else if (layoutStep4.getVisibility() == View.VISIBLE) {
            layoutStep4.setVisibility(View.GONE);
            layoutStep3.setVisibility(View.VISIBLE);
            progressBreadcrumbs.setProgress(60);
            toolbar.setTitle("When does it end?");
        }
    }

}
