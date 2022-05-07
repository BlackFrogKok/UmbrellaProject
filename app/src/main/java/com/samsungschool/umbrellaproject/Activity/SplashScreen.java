package com.samsungschool.umbrellaproject.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.samsungschool.umbrellaproject.Auth.SignInActivity;
import com.yandex.mapkit.MapKitFactory;

import java.util.List;

public class SplashScreen extends AppCompatActivity {


    private ActivityResultLauncher<Intent> registrationGetResult;

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

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA)
                .check();

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

    private void checkAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            startMainActivityAuth(firebaseAuth);
        } else {
            startRegistrationActivity(firebaseAuth);
        }
    }
}