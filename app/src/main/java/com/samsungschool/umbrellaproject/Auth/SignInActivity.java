package com.samsungschool.umbrellaproject.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.umbrellaproject.databinding.ActivityMainBinding;
import com.example.umbrellaproject.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }


}