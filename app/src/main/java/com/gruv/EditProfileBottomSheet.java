package com.gruv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gruv.models.Author;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {
    private final ProfileActivity profileActivity;
    public Author thisUser;
    public static MaterialAlertDialogBuilder builder;
    CircleImageView imageView;
    Fragment fragment = this;
    private FirebaseAuth authenticateObj;
    private FirebaseUser currentUser;
    private String url;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private TextInputEditText textInputName, textInputBio, textInputSite, textInputEmail;
    private TextInputLayout textLayoutEmail;
    private ImageButton imageRemovePicture;
    private ProgressBar progressBar;
    private Uri filePath;
    private View v;
    private boolean pictureChanged = false;

    public EditProfileBottomSheet(Author thisUser, ProfileActivity profileActivity) {
        super();
        this.thisUser = thisUser;
        this.profileActivity = profileActivity;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.edit_profile, container, false);

        authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

//        if (currentUser != null) {
//            setCurrentUser();
//        }

        MaterialButton buttonSave = v.findViewById(R.id.buttonSave);
        MaterialButton buttonCancel = v.findViewById(R.id.buttonCancel);
        imageView = v.findViewById(R.id.imagePicture);

        ExtendedFloatingActionButton fabEditPicture = v.findViewById(R.id.fabChoose);
        textInputName = v.findViewById(R.id.textInputName);
        textInputBio = v.findViewById(R.id.textInputBio);
        textInputSite = v.findViewById(R.id.textInputSite);
        textInputEmail = v.findViewById(R.id.textInputEmail);
        textLayoutEmail = v.findViewById(R.id.textLayoutEmail);
        progressBar = v.findViewById(R.id.progressEditProfile);
        imageRemovePicture = v.findViewById(R.id.buttonRemovePicture);


        textInputName.setText(thisUser.getName());

        if (currentUser.getPhotoUrl() == null && thisUser.getAvatar() == null) {
            imageRemovePicture.setVisibility(View.INVISIBLE);
        } else {

            imageRemovePicture.setVisibility(View.VISIBLE);
        }

        if (thisUser.getBio() != null)
            textInputBio.setText(thisUser.getBio());
        if (thisUser.getSite() != null)
            textInputSite.setText(thisUser.getSite());

        textInputEmail.setText(thisUser.getEmail());

        if (thisUser.getAvatar() != null) {
            Glide.with(this).load(thisUser.getAvatar()).centerCrop().into(imageView);
        }
        imageRemovePicture.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Remove picture?")
                    .setMessage("Are you sure you want to remove your picture?")
                    .setPositiveButton("Remove", ((dialog, which) -> {
                        removePicture();
                    }))
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        //this.dismiss();
                    })
                    .show();
        });
        buttonSave.setOnClickListener(v -> {
            showProgress();
            setUserAndUpdateDb();

        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideProgress();
                dismiss();

            }
        });

        fabEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .start(getContext(), EditProfileBottomSheet.this);
            }
        });

        textInputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail(s, textLayoutEmail, buttonSave);
            }
        });
        return v;
    }

    public void validateEmail(Editable s, TextInputLayout layout, MaterialButton button) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z._-]+[a-zA-Z._-]";
        // onClick of button perform this simplest code.
        boolean emailCorrect;
        boolean passwordCorrect;
        if (!s.toString().trim().matches(emailPattern)) {
            emailCorrect = false;
            layout.setError("Invalid email");
            button.setClickable(false);
            button.setTextColor(ContextCompat.getColor(getContext(), (R.color.grey)));
        } else {
            layout.setError(null);
            button.setClickable(true);
            button.setTextColor(ContextCompat.getColor(getContext(), (R.color.colorPrimary)));

        }
    }

    private void updateEmail() {
        builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Enter your password");
        builder.setMessage("Changing your email requires you to re-enter your password");

        // Set up the input
        final TextInputEditText input = new TextInputEditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        TextInputLayout layout = new TextInputLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 132);
        layout.setPadding(30, 30, 30, 30);
        input.setLayoutParams(params);
        layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        input.setTextColor(Color.BLACK);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword(s, layout);
            }
        });
        if (input.getParent() != null) {
            ((ViewGroup) input.getParent()).removeView(input);
        }
        layout.addView(input);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 160);
        layout.setLayoutParams(params);

        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (password.length() <= 6)
                    if (!password.isEmpty()) {
                        hideProgress();
                        dismiss();
                        showSnackBar("That password is too short", Snackbar.LENGTH_LONG);
                    } else {
                        hideProgress();
                        dismiss();
                        showSnackBar("Enter Password", Snackbar.LENGTH_SHORT);
                    }
                else
                    loginAndUpdateEmail(password);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                hideProgress();
            }
        });
        builder.show();
    }


    public void validatePassword(Editable editable, TextInputLayout layout) {
        if (editable.length() < 6) {
            layout.setError("Password must have more than 6 characters");
        } else {
            layout.setError(null);

        }
    }


    private boolean connectionAvailable() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                connected = true;
            } else {
                connected = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return connected;
    }

    private void loginAndUpdateEmail(String password) {
        if (connectionAvailable()) {
            showProgress();
            authenticateObj = FirebaseAuth.getInstance();
            currentUser = authenticateObj.getCurrentUser();


            try {
                authenticateObj.signInWithEmailAndPassword(thisUser.getEmail(), password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            currentUser = authenticateObj.getCurrentUser();
                            thisUser.setEmail(textInputEmail.getText().toString());
                            currentUser.updateEmail(thisUser.getEmail()).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    hideProgress();
                                    showSnackBar(e.getMessage(), Snackbar.LENGTH_SHORT);
                                }
                            });
                            if (pictureChanged) {
                                uploadImage(storageReference);
                            } else {
                                updateDB(false);
                            }
                            hideProgress();
                        }

                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgress();
                                if (e.getClass().equals(FirebaseAuthInvalidCredentialsException.class)) {
                                    showSnackBar("Incorrect password", Snackbar.LENGTH_SHORT);
                                } else {
                                    showSnackBar(e.getMessage(), Snackbar.LENGTH_SHORT);
                                }
                                dismiss();
                                e.printStackTrace();
                            }
                        });
//                            } else {
//                                hideProgress();
//                                showSnackBar("Incorrect password", R.id.layoutParentEdit, Snackbar.LENGTH_SHORT);
//                            }
                    }
                });
            } catch (Exception e) {
                hideProgress();
                dismiss();
                showSnackBar("Something went wrong", Snackbar.LENGTH_SHORT);
                e.printStackTrace();
            }
        } else {
            hideProgress();
            dismiss();
            showSnackBar("Check internet connection", Snackbar.LENGTH_SHORT);
        }
    }

    private void removePicture() {
        updateUser(null, progressBar);
    }

    private void setUserAndUpdateDb() {
        if (!textInputName.getText().toString().trim().equals(""))
            thisUser.setName(textInputName.getText().toString().trim());
        if (!textInputBio.getText().toString().trim().equals(""))
            thisUser.setBio(textInputBio.getText().toString().trim());
        if (!textInputSite.getText().toString().trim().equals(""))
            thisUser.setSite(textInputSite.getText().toString().trim());
        if (!textInputEmail.getText().toString().trim().equals(thisUser.getEmail())) {
            updateEmail();
        } else {
            if (pictureChanged) {
                uploadImage(storageReference);
            } else {
                updateDB(false);
            }
        }
    }

    public void setCurrentUser() {
        thisUser = new Author(currentUser.getUid(), currentUser.getDisplayName(), null, R.drawable.profile_pic4);
        if (currentUser.getPhotoUrl() != null)
            thisUser.setAvatar(currentUser.getPhotoUrl().toString());
        thisUser.setEmail(currentUser.getEmail());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                pictureChanged = true;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
                    e.printStackTrace();
                }
            }
        }
    }


    private void uploadImage(StorageReference storageReference) {

        if (filePath != null) {
            final ProgressBar progressDialog = v.findViewById(R.id.progressEditProfilePercentage);
            progressDialog.setVisibility(View.VISIBLE);
            String path = currentUser.getUid() + "/profilePicture/picture";
            StorageReference ref = storageReference.child(path);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setVisibility(View.INVISIBLE);
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url = uri.toString();
                                    updateUser(uri, progressDialog);

                                }
                            });
                            showSnackBar("Done!", Snackbar.LENGTH_LONG);
                            dismiss();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setProgress((int) progress);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    progressDialog.setVisibility(View.INVISIBLE);
                    showSnackBar("Something went wrong", Snackbar.LENGTH_SHORT);
                    e.printStackTrace();
                }
            });
        }
    }

    public void showSnackBar(String message, Integer length) {
//        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
//        View contextView = v.findViewById(layout);
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, length).show();
//        Snackbar.make(contextView, message, length).show();
    }

    private void updateUser(Uri uri, ProgressBar progressDialog) {
        if (uri != null)
            thisUser.setAvatar(uri.toString());
        else
            thisUser.setAvatar(null);


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.setVisibility(View.INVISIBLE);
                        hideProgress();
                        if (!task.isSuccessful()) {
                            showSnackBar("Couldn't update picture", Snackbar.LENGTH_SHORT);
                        } else {
                            updateDB(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackBar("Something went wrong", Snackbar.LENGTH_LONG);
            }
        });
    }

    private void updateDB(boolean loadPictureDone) {
        Map<String, Object> user = new HashMap<>();
        if (currentUser.getPhotoUrl() != null)
            thisUser.setAvatar(currentUser.getPhotoUrl().toString());
        user.put(thisUser.getId(), thisUser);

        databaseReference.child("author").updateChildren(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError != null) {
                    showSnackBar(databaseError.getMessage(), Snackbar.LENGTH_LONG);

                } else {
                    if (!loadPictureDone) {
                        showSnackBar("Done!", Snackbar.LENGTH_LONG);
                        dismiss();
                    } else
                        dismiss();
                    profileActivity.onBottomSheetDismiss();
                }
            }
        });

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(thisUser.getName())
                .build();

        currentUser.updateProfile(profileUpdates);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileActivity.onBottomSheetDismiss();
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }


}
