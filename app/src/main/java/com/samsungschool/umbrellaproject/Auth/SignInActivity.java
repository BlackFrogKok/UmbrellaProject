package com.samsungschool.umbrellaproject.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.samsungschool.umbrellaproject.Fragments.Other.EnterCodeFragment;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.ActivitySignInBinding;
import com.samsungschool.umbrellaproject.Fragments.Other.EnterPhoneFragment;

public class SignInActivity extends AppCompatActivity implements EnterPhoneFragment.onEnterPhoneListener, EnterCodeFragment.onEnterCodeFragmentListener{

    private ActivitySignInBinding binding;
    private static final String EXAMPLE_FRAGMENT_TAG = "example_fragment";
    private SignIn signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        signIn = new SignIn(this, FirebaseAuth.getInstance());
        viewFragment("phone");
    }


    public void viewFragment(String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (tag.equals("phone")){
            transaction.add(R.id.frgmCont, EnterPhoneFragment.newFragment());
            transaction.addToBackStack(null);
        }
        else if (tag.equals("code")){
            transaction.replace(R.id.frgmCont, EnterCodeFragment.newFragment());
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }


    @Override
    public void ContinueBtnPhone(String phone) {
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else{
            signIn.startPhoneVerification(phone);
        }
    }

    @Override
    public void ContinueBtnCode(String code) {
        if (TextUtils.isEmpty(code)){
            Toast.makeText(this, "Введите код", Toast.LENGTH_SHORT).show();
        }
        else{
            signIn.verifyPhoneWithCode(code);
        }

    }

    @Override
    public void ResetBtnCode(String phone) {
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else{
            signIn.resendVerificationCode(phone);
        }
    }
}