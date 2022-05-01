package com.samsungschool.umbrellaproject.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.samsungschool.umbrellaproject.Auth.SignInActivity;
import com.yandex.mapkit.MapKitFactory;

public class SplashScreen extends AppCompatActivity {


    private ActivityResultLauncher<Intent> registrationGetResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        MapKitFactory.setApiKey("d80212c2-0e79-43e5-b249-7e7612d3f566");
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