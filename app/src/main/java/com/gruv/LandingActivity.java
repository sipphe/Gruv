package com.gruv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.widget.Button;
import android.widget.TextView;

import com.gruv.com.gruv.navigation.Navigation;

public class LandingActivity extends AppCompatActivity {
    protected static ConstraintLayout layoutLoginStart, layoutLoginEmail, layoutRegister, layoutForgotPassword, layoutEnterVerifyCode, layoutProgress, layoutResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        layoutLoginStart = (ConstraintLayout) findViewById(R.id.constraintLayoutLoginStart);
        layoutLoginEmail = (ConstraintLayout) findViewById(R.id.constraintLayoutLogin);
        layoutRegister = (ConstraintLayout) findViewById(R.id.constraintLayoutRegister);
        layoutForgotPassword = (ConstraintLayout) findViewById(R.id.constraintLayoutForgotPassword);
        layoutEnterVerifyCode = (ConstraintLayout) findViewById(R.id.constraintLayoutEnterVerifyCode);
        layoutResetPassword = (ConstraintLayout)findViewById(R.id.constraintLayoutResetPassword);
        layoutProgress = (ConstraintLayout) findViewById(R.id.constraintLayoutProgressBar);


        TextView textSignUp = (TextView) findViewById(R.id.textViewSignUp);
        TextView textForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        Button buttonEmail = (Button) findViewById(R.id.buttonEmail);
        Button buttonSignIn = (Button) findViewById(R.id.buttonLogin);
        Button buttonRegister = (Button) findViewById(R.id.buttonRegister);
        Button buttonNext1 = (Button) findViewById(R.id.buttonNext1);
        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        Button buttonResetPassword = (Button) findViewById(R.id.buttonResetPassword);

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layoutLoginEmail.setVisibility(View.VISIBLE);
                layoutLoginStart.setVisibility(View.GONE);
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
                //TODO Add email sign in authorisation code
                try {
                    Navigation.showProgress(3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add sign up authorisation code
                try {
                    Navigation.showProgress(3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
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
                try {
                    Navigation.showProgress(3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
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


}

