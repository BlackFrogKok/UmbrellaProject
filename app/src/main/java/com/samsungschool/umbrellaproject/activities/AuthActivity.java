package com.samsungschool.umbrellaproject.activities;

import static android.text.TextUtils.isEmpty;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.google.firebase.auth.PhoneAuthProvider.getCredential;
import static com.samsungschool.umbrellaproject.fragments.SmsFragment.TAG_CODE_FRAGMENT;
import static com.samsungschool.umbrellaproject.fragments.PhoneFragment.TAG_PHONE_FRAGMENT;
import static com.samsungschool.umbrellaproject.fragments.RegisterFragment.TAG_REGISTER_FRAGMENT;
import static com.samsungschool.umbrellaproject.utils.ApiUtils.API_USERS_COLLECTION;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.ActivityAuthBinding;
import com.samsungschool.umbrellaproject.fragments.SmsFragment;
import com.samsungschool.umbrellaproject.fragments.PhoneFragment;
import com.samsungschool.umbrellaproject.fragments.RegisterFragment;
import com.samsungschool.umbrellaproject.interfaces.AuthListener;
import com.samsungschool.umbrellaproject.interfaces.AutoSetCode;
import com.samsungschool.umbrellaproject.models.User;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity implements AuthListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final User user = new User();

    private String mVerificationId;
    private ForceResendingToken mForceResendingToken;

    private ActivityAuthBinding binding;

    public static Intent newIntent(Context context) {
        return new Intent(context, AuthActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startFragment(PhoneFragment.newFragment(), TAG_PHONE_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        binding.progressBar.setVisibility(View.GONE);
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPhoneSubmitted(String phone) {
        if (isEmpty(phone)) {
            makeText(this, R.string.hint_phone, LENGTH_SHORT).show();
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            user.setPhoneNumber(phone);
            startPhoneVerification(phone);
        }
    }

    @Override
    public void onSmsSubmitted(String sms) {
        if (isEmpty(sms)) {
            makeText(this, R.string.hint_code, LENGTH_SHORT).show();
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            verifyPhoneWithCode(sms);
        }
    }

    @Override
    public void onInfoSubmitted(String name, String email) {
        user.setName(name);
        user.setMail(email);
        user.setActiveSession("");
        createUser();
    }

    private void signWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        loadUser(databaseTask -> {
                            if (databaseTask.isSuccessful() & databaseTask.getResult().exists()) {
                                binding.progressBar.setVisibility(View.GONE);
                                startActivity(MainActivity.newIntent(AuthActivity.this));
                                finish();
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                startFragment(RegisterFragment.newInstance(), TAG_REGISTER_FRAGMENT);
                            }
                        });
                    } else {
                        showError("");
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CODE_FRAGMENT);
                    if (fragment != null) ((AutoSetCode) fragment).setCode("");
                    showError(e.getMessage());
                });
    }

    public void verifyPhoneWithCode(String code) {
        signWithPhoneAuthCredential(getCredential(mVerificationId, code));
    }

    public void startPhoneVerification(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CODE_FRAGMENT);
                        if (fragment != null) ((AutoSetCode) fragment).setCode(phoneAuthCredential.getSmsCode());
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        binding.progressBar.setVisibility(View.GONE);
                        showError(e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull ForceResendingToken token) {
                        super.onCodeSent(verificationId, mForceResendingToken);
                        mVerificationId = verificationId;
                        mForceResendingToken = token;
                        binding.progressBar.setVisibility(View.GONE);
                        startFragment(SmsFragment.newFragment(user.getPhoneNumber()), TAG_CODE_FRAGMENT);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void loadUser(OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseFirestore.getInstance()
                .collection(API_USERS_COLLECTION)
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .get()
                .addOnFailureListener(e -> showError(""))
                .addOnCompleteListener(listener);
    }

    private void createUser() {
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance()
                .collection(API_USERS_COLLECTION)
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .set(user)
                .addOnFailureListener(e -> binding.progressBar.setVisibility(View.GONE))
                .addOnCompleteListener(task -> {
                    startActivity(MainActivity.newIntent(this));
                    finish();
                });
    }

    private void showError(String error) {
        if (isEmpty(error)) {
            makeText(this, R.string.error_auth, LENGTH_SHORT).show();
        } else {
            makeText(this, getString(R.string.error_auth) + ": " + error, LENGTH_SHORT).show();
        }

    }

    private void startFragment(Fragment fragment, String arg) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment, arg)
                .addToBackStack(null)
                .commit();
    }
}
