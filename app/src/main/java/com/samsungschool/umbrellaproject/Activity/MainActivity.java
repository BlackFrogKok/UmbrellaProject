package com.samsungschool.umbrellaproject.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.samsungschool.umbrellaproject.Fragments.NavigationItems.AboutFragment.AboutFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment.HistoryFragment;
import com.samsungschool.umbrellaproject.Fragments.MainFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.ProfileFragment.ProfileFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.QRReadFragment.QrFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.SettingsFragment.SettingsFragment;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements MainFragment.onNavBtnClickListener {
    private ActivityMainBinding binding;

    private String qr;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore dataBase;
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == Activity.RESULT_OK){
            qr = result.getData().getStringExtra("qr");
            Toast.makeText(this,qr, Toast.LENGTH_SHORT).show();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        startFragment(MainFragment.newInstance(), "main");

        binding.materialToolbar2.setNavigationOnClickListener(v -> startFragment(MainFragment.newInstance(), "main"));


        dataBase = FirebaseFirestore.getInstance();


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


    private void itemSelector() {
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
                case R.id.nav_history:
                    startFragment(HistoryFragment.newInstance(), "history");
                    binding.materialToolbar2.setTitle(R.string.history_item);
                    return true;

                case R.id.nav_about:
                    startFragment(AboutFragment.newInstance(), "about");
                    binding.materialToolbar2.setTitle(R.string.about_item);
                    return true;
                case R.id.nav_qr:
                    startQRActivity();
                default:
                    return false;
            }
        });
    }


    public void startQRActivity(){
    Intent intent = new Intent(this, QrActivity.class);
    mStartForResult.launch(intent);
    }

    //дим, я ебал в рот фрагменты
    private void startQRFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, new QrFragment())
                .addToBackStack(null)
                .commit();
    }
    //это пиздец как криво работает, оставлю активность

    private void startFragment(Fragment fragment, String arg) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();

    }


    @Override
    public void onClick() {
        binding.getRoot().open();
    }

    public WindowManager getWindowManager() {
        return (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }


    public SensorManager getManager() {
        return (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }


}