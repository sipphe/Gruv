package com.gruv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.widget.Button;
import android.widget.TextView;

public class LandingActivity extends AppCompatActivity {
    ConstraintLayout layoutLoginStart, layoutLoginEmail, layoutRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        layoutLoginStart = (ConstraintLayout) findViewById(R.id.constraintLayoutLoginStart);
        layoutLoginEmail = (ConstraintLayout) findViewById(R.id.constraintLayoutLogin);
        layoutRegister = (ConstraintLayout) findViewById(R.id.constraintLayoutRegister);

        TextView textSignUp = (TextView) findViewById(R.id.textViewSignUp);
        Button buttonEmail = (Button) findViewById(R.id.buttonEmail);
        Button buttonSignIn = (Button) findViewById(R.id.buttonLogin);
        Button buttonRegister = (Button) findViewById(R.id.buttonRegister);
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

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        } else {
            this.finishAffinity();
        }
    }


}

