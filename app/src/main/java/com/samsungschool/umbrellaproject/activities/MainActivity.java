package com.samsungschool.umbrellaproject.activities;

import static android.widget.Toast.makeText;
import static com.samsungschool.umbrellaproject.activities.QrActivity.EXTRA_KEY_CODE;
import static com.samsungschool.umbrellaproject.fragments.MainFragment.TAG_MAIN_FRAGMENT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.samsungschool.umbrellaproject.data.FirestoreDatabase;
import com.samsungschool.umbrellaproject.databinding.ActivityMainBinding;
import com.samsungschool.umbrellaproject.fragments.AboutFragment;
import com.samsungschool.umbrellaproject.fragments.HistoryFragment;
import com.samsungschool.umbrellaproject.fragments.MainFragment;
import com.samsungschool.umbrellaproject.interfaces.NavigationListener;
import com.samsungschool.umbrellaproject.interfaces.UserListener;
import com.samsungschool.umbrellaproject.items.HistoryItem;
import com.samsungschool.umbrellaproject.fragments.RouteFragment;
import com.samsungschool.umbrellaproject.fragments.NfcFragment;
import com.samsungschool.umbrellaproject.fragments.ProfileFragment;
import com.samsungschool.umbrellaproject.fragments.SettingsFragment;
import com.samsungschool.umbrellaproject.interfaces.OnCompleteDataListener;
import com.samsungschool.umbrellaproject.interfaces.QrCheckCompleteListener;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.data.Station;
import com.samsungschool.umbrellaproject.models.User;

import io.reactivex.rxjava3.core.Observable;

public class MainActivity extends AppCompatActivity implements NavigationListener {

    private final Station station = new Station();
    private User user;

    private ActivityMainBinding binding;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startFragment(MainFragment.newInstance(), TAG_MAIN_FRAGMENT);
        binding.materialToolbar2.setNavigationOnClickListener(v -> startFragment(MainFragment.newInstance(), TAG_MAIN_FRAGMENT));
        binding.navigationDrawer
                .getHeaderView(0)
                .findViewById(R.id.userAvatar)
                .setOnClickListener(v -> {
                    if (user != null) startFragment(ProfileFragment.newInstance(user));
                    binding.getRoot().close();
                });
        setupNavigation();
        loadUser();
    }

    @SuppressLint("NonConstantResourceId")
    private void setupNavigation() {
        binding.navigationDrawer.setNavigationItemSelectedListener(item -> {
            binding.getRoot().close();
            switch (item.getItemId()) {
                case R.id.nav_user:
                    if (user != null) startFragment(ProfileFragment.newInstance(user));
                    return true;
                case R.id.nav_settings:
                    startFragment(SettingsFragment.newInstance());
                    return true;
                case R.id.nav_history:
                    startFragment(HistoryFragment.newInstance());
                    return true;
                case R.id.nav_help:
                    startActivity(HelpActivity.newIntent(this));
                    return true;
                case R.id.nav_about:
                    startFragment(AboutFragment.newInstance());
                    return true;
                case R.id.nav_qr:
                    startQRActivity();
                    return true;

                default:
                    return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNavigationClick() {
        binding.getRoot().open();
    }

    private void loadUser() {
        new FirestoreDatabase().getUser(FirebaseAuth.getInstance().getUid(), new OnCompleteDataListener<User>() {
            @Override
            public void onCompleteObservable(@NonNull Observable<User> observable) {
                // Do nothing
            }

            @Override
            public void onComplete(@NonNull User user) {
                MainActivity.this.user = user;
                TextView nav_user = binding.navigationDrawer.getHeaderView(0).findViewById(R.id.userName);
                TextView nav_mail = binding.navigationDrawer.getHeaderView(0).findViewById(R.id.userEmail);
                nav_user.setText(user.getName());
                nav_mail.setText(user.getMail());

                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MAIN_FRAGMENT);
                if (fragment != null) ((UserListener) fragment).onLoaded(user);
            }

            @Override
            public void onCanceled() {
                // Do nothing
            }

        });
    }

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    private void showError() {
        makeText(this, R.string.error_qr, Toast.LENGTH_SHORT).show();
    }

    public void startQRActivity() {
        qrLauncher.launch(QrActivity.newIntent(this));
    }

    public void startHistoryItemFragment(HistoryItem historyItem) {
        replaceFragment(RouteFragment.newInstance(historyItem));
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void startFragment(Fragment fragment) {
        startFragment(fragment, null);
    }

    private void startFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragmentContainer, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    ActivityResultLauncher<Intent> qrLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                String code = data.getStringExtra(EXTRA_KEY_CODE);
                if (code != null && code.startsWith("C")) {
                    checkStationCode(code);
                } else if (code != null && code.length() > 6) {
                    checkStationQr(code);
                } else {
                    showError();
                }
            }
        }
    });

    private void checkStationCode(String code) {
        station.checkCode(code, new OnCompleteDataListener<String>() {
            @Override
            public void onComplete(@NonNull String s) {
                Log.w("document2", s);
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MAIN_FRAGMENT);
                if (fragment != null) ((QrCheckCompleteListener) fragment).QrCheckComplete(s);
            }

            @Override
            public void onCanceled() {
                showError();
            }
        });
    }

    private void checkStationQr(String code) {
        station.checkQrData(code, new OnCompleteDataListener<String>() {
            @Override
            public void onComplete(@NonNull String s) {
                Log.w("document2", s);
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MAIN_FRAGMENT);
                if (fragment != null) ((QrCheckCompleteListener) fragment).QrCheckComplete(s);
            }

            @Override
            public void onCanceled() {
                showError();
            }
        });
    }
}
