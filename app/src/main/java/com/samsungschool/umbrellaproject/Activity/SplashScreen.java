package com.samsungschool.umbrellaproject.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.samsungschool.umbrellaproject.Auth.SignInActivity;

public class SplashScreen extends AppCompatActivity {


    private ActivityResultLauncher<Intent> registrationGetResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            startMainActivityAuth(firebaseAuth);
        } else {
            startRegistrationActivity(firebaseAuth);
        }

    }

    private void startMainActivityAuth(FirebaseAuth firebaseAuth){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegistrationActivity(FirebaseAuth firebaseAuth){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}