package com.samsungschool.umbrellaproject.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.samsungschool.umbrellaproject.Fragments.AboutFragment;
import com.samsungschool.umbrellaproject.Fragments.MainFragment;
import com.samsungschool.umbrellaproject.Fragments.ProfileFragment;
import com.samsungschool.umbrellaproject.Fragments.SettingsFragment;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.ActivityMainBinding;
import com.yandex.mapkit.MapKitFactory;

public class MainActivity extends AppCompatActivity implements MainFragment.onNavBtnClickListener {
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        startFragment(MainFragment.newInstance(), "main");
        binding.materialToolbar2.setNavigationOnClickListener(v -> startFragment(MainFragment.newInstance(), "main"));

        binding.navigationDrawer
                .getHeaderView(0)
                .findViewById(R.id.userAvatar)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFragment(ProfileFragment.newInstance(), "profile");
                        binding.getRoot().close();
                    }
                });
        itemSelector();




    }


    private void startFragment(Fragment fragment, String arg) {
        if (arg.equals("main")) {
            binding.materialToolbar2.setVisibility(View.GONE);
        } else {
            binding.materialToolbar2.setVisibility(View.VISIBLE);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void itemSelector(){
        binding.navigationDrawer.setNavigationItemSelectedListener(item -> {
            binding.getRoot().close();
            switch (item.getItemId()) {
                case R.id.nav_user:
                    startFragment(ProfileFragment.newInstance(), "user");
                    binding.materialToolbar2.setTitle(R.string.user_item);
                    return true;
                case R.id.nav_settings:
                    startFragment(SettingsFragment.newInstance(), "settings");
                    binding.materialToolbar2.setTitle(R.string.settings_item);
                    return true;
                case R.id.nav_about:
                    startFragment(AboutFragment.newInstance(), "about");
                    binding.materialToolbar2.setTitle(R.string.about_item);
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    public void onClick() {
        binding.getRoot().open();
    }

    public SensorManager getManager() {
        return (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

}