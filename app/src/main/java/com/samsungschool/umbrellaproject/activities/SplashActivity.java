package com.samsungschool.umbrellaproject.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.NFC;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static com.samsungschool.umbrellaproject.utils.PrefUtils.PREF_KEY_THEME;
import static com.samsungschool.umbrellaproject.utils.PrefUtils.PREF_NAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.ActivitySplashBinding;

import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, SplashActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setDefaultNightMode(sharedPreferences.getInt(PREF_KEY_THEME, MODE_NIGHT_FOLLOW_SYSTEM));

        requestAppPermissions();
    }

    private void requestAppPermissions() {
        TedPermission.create()
                .setPermissions(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, CAMERA, NFC)
                .setDeniedMessage(R.string.hint_permission)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        checkAuth();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finish();
                    }
                })
                .check();
    }

    private void checkAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(MainActivity.newIntent(this));
        } else {
            startActivity(AuthActivity.newIntent(this));
        }
        finish();
    }
}
