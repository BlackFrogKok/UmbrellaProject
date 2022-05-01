package com.samsungschool.umbrellaproject.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

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
        MapKitFactory.setApiKey("d80212c2-0e79-43e5-b249-7e7612d3f566");
        startFragment(MainFragment.newInstance());
        binding.materialToolbar2.setNavigationOnClickListener(v -> onClick());


        binding.navigationDrawer.setNavigationItemSelectedListener(item -> {
            binding.getRoot().close();
            switch (item.getItemId()) {
                case R.id.nav_first:
                    startFragment(MainFragment.newInstance());
                    return true;
                case R.id.nav_second:
                    startFragment(SettingsFragment.newInstance());
                    return true;
                case R.id.nav_third:
                    startFragment(AboutFragment.newInstance());
                    return true;
                default:
                    return false;
            }
        });

        binding.navigationDrawer.setCheckedItem(R.id.nav_first);

    }



    private void startFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onClick() {
        binding.getRoot().open();
    }
}