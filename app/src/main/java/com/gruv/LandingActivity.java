package com.gruv;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.CallbackManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gruv.navigation.Navigation;

public class LandingActivity extends AppCompatActivity {
    protected static ConstraintLayout
            layoutLoginStart, layoutLoginEmail,
            layoutRegister, layoutForgotPassword,
            layoutEnterVerifyCode, layoutProgress,
            layoutResetPassword;
    ImageView imageFacebook;

    private CallbackManager mCallbackManager;
    private FirebaseAuth authenticateObj;
    private FirebaseUser currentUser;
    private EditText textEmail, textPassword, editTextEmail, editTextPassword, editTextName, editTextConfirmPassword;
    private TextView textSignUp, textForgotPassword, textViewSignUp;
    private Button buttonEmail, buttonSignIn, buttonRegister, buttonNext1, buttonNext, buttonResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        initializeControls();
        //getCurrentUser();

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layoutLoginEmail.setVisibility(View.VISIBLE);
                layoutLoginStart.setVisibility(View.GONE);
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createAccount();
            }
        });
        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutRegister.setVisibility(View.VISIBLE);
                layoutLoginStart.setVisibility(View.GONE);
            }
        });

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLoginEmail.setVisibility(View.GONE);
                layoutForgotPassword.setVisibility(View.VISIBLE);
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUserWithEmailAndPassword();
                //finish();
            }
        });

        buttonNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add get verification email code
                layoutForgotPassword.setVisibility(View.GONE);
                layoutEnterVerifyCode.setVisibility(View.VISIBLE);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add verification code match code
                layoutEnterVerifyCode.setVisibility(View.GONE);
                layoutResetPassword.setVisibility(View.VISIBLE);
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add reset password code
                layoutResetPassword.setVisibility(View.GONE);

                finish();
            }
        });
    }

    private void createAccount() {
        Toast.makeText(getApplicationContext(), "Method works", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (layoutLoginEmail.getVisibility() == View.VISIBLE) {
            layoutLoginEmail.setVisibility(View.GONE);
            layoutLoginStart.setVisibility(View.VISIBLE);
        } else if (layoutRegister.getVisibility() == View.VISIBLE) {
            layoutRegister.setVisibility(View.GONE);
            layoutLoginStart.setVisibility(View.VISIBLE);
        } else if (layoutForgotPassword.getVisibility() == View.VISIBLE) {
            layoutForgotPassword.setVisibility(View.GONE);
            layoutLoginEmail.setVisibility(View.VISIBLE);
        } else if (layoutEnterVerifyCode.getVisibility() == View.VISIBLE) {
            layoutEnterVerifyCode.setVisibility(View.GONE);
            layoutForgotPassword.setVisibility(View.VISIBLE);
        } else if (layoutResetPassword.getVisibility() == View.VISIBLE) {
            layoutResetPassword.setVisibility(View.GONE);
            layoutEnterVerifyCode.setVisibility(View.VISIBLE);
        } else if (layoutProgress.getVisibility() == View.VISIBLE) {

        } else {
            this.finishAffinity();
        }
    }

    public void imgFacebookLogin(View v) {
        Intent intent = new Intent(getApplicationContext(), FacebookLoginActivity.class);
        startActivity(intent);
    }

    public boolean login() {
        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();
        boolean result = false;

        Navigation.showProgress();
        if (connectionAvailable()) {
            authenticateObj = FirebaseAuth.getInstance();
            currentUser = authenticateObj.getCurrentUser();

            if (currentUser == null) {
                result = false;
            } else {

                layoutLoginStart.setVisibility(View.VISIBLE);
            }
            if (!email.isEmpty() && email != null) {
                authenticateObj.signInWithEmailAndPassword(email, password);
                finish();
                result = true;
            } else {
                Toast.makeText(getApplicationContext(), "Enter Email and Password", Toast.LENGTH_LONG).show();
            }

            Navigation.hideProgress();
            if (result = true) {
                //replace with snackBar
                Toast.makeText(getApplicationContext(), "Check internet connection and try again later", Toast.LENGTH_LONG).show();
            } else {

            }
        }
        return result;
    }

    public void imgTwitterGenericLogin(View v) {
        Intent intent = new Intent(getApplicationContext(), GenericIdpActivity.class);
        startActivity(intent);
    }

    private boolean connectionAvailable() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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


    private void getCurrentUser() {
        authenticateObj = FirebaseAuth.getInstance();
        currentUser = authenticateObj.getCurrentUser();

        if (currentUser != null) {
            layoutLoginStart.setVisibility(View.VISIBLE);
            finish();
        }
    }

    private void initializeControls() {
        //controls
        //landing
        buttonEmail = findViewById(R.id.buttonEmail);
        buttonSignIn = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonNext1 = findViewById(R.id.buttonNext1);
        buttonNext = findViewById(R.id.buttonNext);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        imageFacebook = findViewById(R.id.imageFacebook);
        textForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        //login
        textEmail = findViewById(R.id.editTextEmailLogin);
        textPassword = findViewById(R.id.editTextPasswordLogin);
        textSignUp = findViewById(R.id.textViewSignUp);

        //register
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        //layouts
        layoutLoginStart = findViewById(R.id.constraintLayoutLoginStart);
        layoutLoginEmail = findViewById(R.id.constraintLayoutLogin);
        layoutRegister = findViewById(R.id.constraintLayoutRegister);
        layoutForgotPassword = findViewById(R.id.constraintLayoutForgotPassword);
        layoutEnterVerifyCode = findViewById(R.id.constraintLayoutEnterVerifyCode);
        layoutResetPassword = findViewById(R.id.constraintLayoutResetPassword);
        layoutProgress = findViewById(R.id.constraintLayoutProgressBar);
    }

    public void registerUserWithEmailAndPassword() {

        layoutRegister.setVisibility(View.VISIBLE);

        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPasword = editTextConfirmPassword.getText().toString().trim();

        if (connectionAvailable()) {
            if (name == null || name.length() == 0
                    || email == null || email.length() == 0
                    || password == null || password.length() == 0
                    || confirmPasword == null || confirmPasword.length() == 0) {

                //replace with snackBar
                Toast.makeText(getApplicationContext(), "Enter all Fields", Toast.LENGTH_LONG).show();
            } else {
                if (!confirmPasword.equals(password)) {
                    Toast.makeText(getApplicationContext(), "Password must match", Toast.LENGTH_LONG).show();
                } else {
                    authenticateObj.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = authenticateObj.getCurrentUser();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        } else {
            //replace with snackBar
            Toast.makeText(getApplicationContext(), "Check internet connection and try again later", Toast.LENGTH_LONG).show();
        }


    }
}

