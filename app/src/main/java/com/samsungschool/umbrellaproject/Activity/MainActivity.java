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
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;

import com.samsungschool.umbrellaproject.Fragments.NavigationItems.AboutFragment.AboutFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment.HistoryFragment;
import com.samsungschool.umbrellaproject.Fragments.MainFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.NFCFragment.NfcFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.ProfileFragment.ProfileFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.QRReadFragment.QrFragment;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.SettingsFragment.SettingsFragment;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteDataListener;
import com.samsungschool.umbrellaproject.Interface.QrCheckCompliteInterface;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.Station;
import com.samsungschool.umbrellaproject.databinding.ActivityMainBinding;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import io.reactivex.rxjava3.core.Observable;

public class MainActivity extends AppCompatActivity implements MainFragment.onNavBtnClickListener {
    private ActivityMainBinding binding;
    private String qr;
    private FirebaseAuth firebaseAuth;
    private final Station station = new Station();
    private NfcAdapter mNfcAdapter;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == Activity.RESULT_OK){
            qr = result.getData().getStringExtra("qr");
            station.checkQrData(qr, this, new MyOnCompliteDataListener<String>() {
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
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        startFragment(MainFragment.newInstance(), "main");
        binding.materialToolbar2.setNavigationOnClickListener(v -> startFragment(MainFragment.newInstance(), "main"));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn’t support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Log.w("l225","NFC is disabled.");
        } else {
            Log.w("l225", "gg");
        }



        binding.navigationDrawer
                .getHeaderView(0)
                .findViewById(R.id.userAvatar)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFragment(ProfileFragment.newInstance(), "p");
                        binding.getRoot().close();
                    }
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
                    startFragment(ProfileFragment.newInstance(), "p");
                    binding.materialToolbar2.setTitle(R.string.user_item);
                    return true;
                case R.id.nav_settings:
                    startFragment(SettingsFragment.newInstance(), "s");
                    binding.materialToolbar2.setTitle(R.string.settings_item);
                    return true;
                case R.id.nav_history:
                    startFragment(HistoryFragment.newInstance(), "a");
                    binding.materialToolbar2.setTitle(R.string.history_item);
                    return true;

                case R.id.nav_about:
                    startFragment(AboutFragment.newInstance(), "k");
                    binding.materialToolbar2.setTitle(R.string.about_item);
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





    private void startFragment(Fragment fragment, String arg) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragmentContainer, fragment, arg)
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
