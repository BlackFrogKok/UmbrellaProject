package com.samsungschool.umbrellaproject.Activity.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.samsungschool.umbrellaproject.Activity.MainActivity;
import com.samsungschool.umbrellaproject.FirestoreDataBase;
import com.samsungschool.umbrellaproject.Fragments.Auth.EnterCodeFragment;
import com.samsungschool.umbrellaproject.Fragments.RegistrateNewUser;
import com.samsungschool.umbrellaproject.Interface.AutoSetCodeInterfaces;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteListener;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.User;
import com.samsungschool.umbrellaproject.databinding.ActivitySignInBinding;
import com.samsungschool.umbrellaproject.Fragments.Auth.EnterPhoneFragment;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity implements EnterPhoneFragment.onEnterPhoneListener, EnterCodeFragment.onEnterCodeFragmentListener, RegistrateNewUser.EnterUserData {

    private ActivitySignInBinding binding;
    private static final String EXAMPLE_FRAGMENT_TAG = "example_fragment";
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private String mVerificationId;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User user = new User();

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider
            .OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            ((AutoSetCodeInterfaces) getSupportFragmentManager().findFragmentByTag("code"))
                    .setCode(phoneAuthCredential.getSmsCode());
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(SignInActivity.this, "Ошибка:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, forceResendingToken);
            mVerificationId = verificationId;
            forceResendingToken = token;
            binding.progressBar.setVisibility(View.GONE);
            startFragment(EnterCodeFragment.newFragment(user.getPhoneNumber()), "code");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startFragment(EnterPhoneFragment.newFragment(), "phone");
    }

    private void startFragment(Fragment fragment, String arg) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, arg)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void ContinueBtnPhone(String phone) {
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else{
            binding.progressBar.setVisibility(View.VISIBLE);
            user.setPhoneNumber(phone);
            startPhoneVerification(phone);
        }
    }

    @Override
    public void ContinueBtnCode(String code) {
        if (TextUtils.isEmpty(code)){
            Toast.makeText(this, "Введите код", Toast.LENGTH_SHORT).show();
        }
        else{
            binding.progressBar.setVisibility(View.VISIBLE);
            verifyPhoneWithCode(code);
        }

    }

    @Override
    public void ResetBtnCode(String phone) {
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else{
            resendVerificationCode(phone);
        }
    }

    @Override
    public void onBackPressed() {
        binding.progressBar.setVisibility(View.GONE);
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        else{
            super.onBackPressed();
        }
    }

    private void signWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            isNewUser(task1 -> {
                                if(task1.isSuccessful() & task1.getResult().exists()){
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    binding.progressBar.setVisibility(View.GONE);
                                    finish();
                                }else{
                                    binding.progressBar.setVisibility(View.GONE);
                                    startFragment(RegistrateNewUser.newInstance(), "registrate");
                                }
                            });
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    ((AutoSetCodeInterfaces) getSupportFragmentManager().findFragmentByTag("code"))
                            .setCode("");
                    Toast.makeText(SignInActivity.this, "Ошибка:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void verifyPhoneWithCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signWithPhoneAuthCredential(credential);
    }

    public void startPhoneVerification(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void resendVerificationCode(String phone){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .setForceResendingToken(forceResendingToken)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void isNewUser(OnCompleteListener<DocumentSnapshot> listener){
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(firebaseAuth.getUid())
                .get()
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> {

                });
    }

    private void createUserDocument(MyOnCompliteListener listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(firebaseAuth.getUid())
                .set(user)
                .addOnCompleteListener(task -> {
                    listener.OnComplite();
                })
                .addOnFailureListener(e -> {

                });
    }

    @Override
    public void userData(String name, String mail) {

        binding.progressBar.setVisibility(View.VISIBLE);
        user.setName(name);
        user.setMail(mail);
        createUserDocument(() -> {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            binding.progressBar.setVisibility(View.GONE);
            finish();
        });
    }
}