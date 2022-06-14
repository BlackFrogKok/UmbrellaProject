package com.samsungschool.umbrellaproject.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.samsungschool.umbrellaproject.Activity.Auth.SignInActivity;
import com.samsungschool.umbrellaproject.databinding.ActivitySplashScreenBinding;

import java.util.List;

public class SplashScreen extends AppCompatActivity {


    private ActivitySplashScreenBinding binding;
    private SharedPreferences sp;
    private static final String UI_THEME_SETTINGS = "ui_theme_mode";
    private TedPermission.Builder builder;

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            checkAuth();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            finish();
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);

        switch(sp.getInt(UI_THEME_SETTINGS, 3)){
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 3:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        builder = TedPermission.create();
        builder.setPermissionListener(permissionlistener)
                .setDeniedMessage("Для использования приложения необходимо предоставить доступ к местоположению и камере\n\nPВключите разрешения в [Настройки] > [Разрешения]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.NFC)
                .check();
    }


    private void startMainActivityAuth(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegistrationActivity(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        builder.setPermissionListener(null);
    }

    private void checkAuth(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            startMainActivityAuth();
        } else {
            startRegistrationActivity();
        }
    }


}