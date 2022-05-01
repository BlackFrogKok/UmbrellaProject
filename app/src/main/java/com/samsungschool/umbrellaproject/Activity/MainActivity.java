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

        binding.navigationDrawer.setNavigationItemSelectedListener(item -> {
            binding.getRoot().close();
            switch (item.getItemId()) {
                case R.id.nav_second:
                    startFragment(SettingsFragment.newInstance(), "settings");
                    return true;
                case R.id.nav_third:
                    startFragment(AboutFragment.newInstance(), "about");
                    return true;
                default:
                    return false;
            }
        });

    }


    private void startFragment(Fragment fragment, String arg) {
        if (arg.equals("main")){
            binding.materialToolbar2.setVisibility(View.GONE);
        }
        else{
            binding.materialToolbar2.setVisibility(View.VISIBLE);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onClick() {
        binding.getRoot().open();
    }

    public SensorManager getManager() {
        return (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

}