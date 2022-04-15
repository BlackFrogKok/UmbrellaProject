package com.samsungschool.umbrellaproject.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import com.samsungschool.umbrellaproject.Activity.MainActivity;
import com.samsungschool.umbrellaproject.Auth.SignInActivity;
import com.samsungschool.umbrellaproject.Auth.User;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {


    private ActivityResultLauncher<Intent> registrationGetResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = new User(Locale.getDefault().getLanguage(), this);
        if(user.isLogin()){
            startMainActivity(user);
        } else {
            startRegistrationActivity();
        }
    }

    private void startMainActivity(User user){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(User.class.getSimpleName(), user);
        startActivity(intent);
        finish();
    }

    private void startRegistrationActivity(User user){
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(User.class.getSimpleName(), user);
        startActivity(intent);
        finish();
    }
}