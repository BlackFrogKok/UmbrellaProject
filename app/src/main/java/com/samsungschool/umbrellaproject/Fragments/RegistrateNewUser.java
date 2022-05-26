package com.samsungschool.umbrellaproject.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentMainBinding;
import com.samsungschool.umbrellaproject.databinding.FragmentRegistrateNewUserBinding;

public class RegistrateNewUser extends Fragment {

    private FragmentRegistrateNewUserBinding binding;

    public static RegistrateNewUser newInstance(String param1, String param2) {
        RegistrateNewUser fragment = new RegistrateNewUser();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrateNewUserBinding.inflate(getLayoutInflater());
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public interface onEnterUserData {
        void userData(String name, String mail);
    }
}