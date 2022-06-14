package com.samsungschool.umbrellaproject.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;

import com.samsungschool.umbrellaproject.FirestoreDataBase;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.AboutFragment.AboutFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment.HistoryFragment;
import com.samsungschool.umbrellaproject.Fragments.MainFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment.HistoryItem;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment.HistoryItemFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.NFCFragment.NfcFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.ProfileFragment.ProfileFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.QRReadFragment.QrFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.SettingsFragment.SettingsFragment;
import com.samsungschool.umbrellaproject.Interface.MakeTransition;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteDataListener;
import com.samsungschool.umbrellaproject.Interface.QrCheckCompliteInterface;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.Station;
import com.samsungschool.umbrellaproject.User;
import com.samsungschool.umbrellaproject.databinding.ActivityMainBinding;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import io.reactivex.rxjava3.core.Observable;

public class MainActivity extends AppCompatActivity implements MainFragment.onNavBtnClickListener {
    private ActivityMainBinding binding;
    private String code;
    private FirebaseAuth firebaseAuth;
    private final Station station = new Station();
    private NfcAdapter mNfcAdapter;
    private FirestoreDataBase dataBase;
    private User user;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == Activity.RESULT_OK){
            code = result.getData().getStringExtra("code");
            if(code.startsWith("C")){
                station.checkCode(code, new MyOnCompliteDataListener<String>() {
                    @Override
                    public void onCompleteObservable(@NonNull Observable<String> observable) {}

                    @Override
                    public void onComplete(@NonNull String s) {
                        Log.w("document2", s);
                        ((QrCheckCompliteInterface) getSupportFragmentManager().findFragmentByTag("main")).QrCheckComplite(s);
                    }

                    @Override
                    public void onCanceled() {
                        Toast.makeText(MainActivity.this, "Не правельный QR code.\nПожалуйста, повторите попытку", Toast.LENGTH_SHORT).show();
                    }
                });
            }else if(code.length() > 6){
                station.checkQrData(code, new MyOnCompliteDataListener<String>() {
                    @Override
                    public void onCompleteObservable(@NonNull Observable<String> observable) {}

                    @Override
                    public void onComplete(@NonNull String s) {
                        Log.w("document2", s);
                        ((QrCheckCompliteInterface) getSupportFragmentManager().findFragmentByTag("main")).QrCheckComplite(s);
                    }

                    @Override
                    public void onCanceled() {
                        Toast.makeText(MainActivity.this, "Не правельный QR code.\nПожалуйста, повторите попытку", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dataBase = new FirestoreDataBase();
        firebaseAuth = FirebaseAuth.getInstance();
        startFragment(MainFragment.newInstance(), "main");
        binding.materialToolbar2.setNavigationOnClickListener(v -> startFragment(MainFragment.newInstance(), "main"));
//        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//
//
//        if (mNfcAdapter == null) {
//            // Stop here, we definitely need NFC
//            Toast.makeText(this, "This device doesn’t support NFC.", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//
//        }
//
//        if (!mNfcAdapter.isEnabled()) {
//            Log.w("l225","NFC is disabled.");
//        } else {
//            Log.w("l225", "gg");
//        }



        binding.navigationDrawer
                .getHeaderView(0)
                .findViewById(R.id.userAvatar)
                .setOnClickListener(v -> {
                    startFragment(ProfileFragment.newInstance(user), "p");
                    binding.getRoot().close();
                });

        dataBase.getUser(FirebaseAuth.getInstance().getUid(), new MyOnCompliteDataListener<User>() {
            @Override
            public void onCompleteObservable(@NonNull Observable<User> observable) {}

            @Override
            public void onComplete(@NonNull User user) {
                MainActivity.this.user = user;
                TextView nav_user = binding.navigationDrawer.getHeaderView(0)
                        .findViewById(R.id.userName);
                TextView nav_mail = binding.navigationDrawer.getHeaderView(0)
                        .findViewById(R.id.userEmail);
                nav_user.setText(user.getName());
                nav_mail.setText(user.getMail());
            }

            @Override
            public void onCanceled() {}
        });



        itemSelector();

    }



    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        else{
            super.onBackPressed();
        }
    }

    private void itemSelector() {
        binding.navigationDrawer.setNavigationItemSelectedListener(item -> {
            binding.getRoot().close();
            switch (item.getItemId()) {
                case R.id.nav_user:
                    startFragment(ProfileFragment.newInstance(user), "p");

                    return true;
                case R.id.nav_settings:
                    startFragment(SettingsFragment.newInstance(), "s");
                    return true;
                case R.id.nav_history:
                    startFragment(HistoryFragment.newInstance(), "a");
                    return true;
                case R.id.nav_help:
                    Intent intent = new Intent(this, IntroActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_about:
                    startFragment(AboutFragment.newInstance(), "a");
                    return true;
                case R.id.nav_qr:
                    startQRActivity();
                    return true;
                case R.id.nav_nfc:
                    startFragment(NfcFragment.newInstance(), "n");
                    return true;
                default:
                    return false;
            }
        });
    }


    public void startQRActivity(){
        Intent intent = new Intent(this, QrActivity.class);
        mStartForResult.launch(intent);
    }

    public void startHistoryItemFragment(HistoryItem historyItem){
        replaceFragment(HistoryItemFragment.newInstance(historyItem), "null");
    }





    private void startFragment(Fragment fragment, String arg) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragmentContainer, fragment, arg)
                .addToBackStack(null)
                .commit();

    }

    private void replaceFragment(Fragment fragment, String arg) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment, arg)
                .addToBackStack(null)
                .commit();

    }



    @Override
    public void onClick() {
        binding.getRoot().open();
    }

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }


}
