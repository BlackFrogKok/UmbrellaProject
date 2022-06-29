package com.samsungschool.umbrellaproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.samsungschool.umbrellaproject.activities.SplashActivity;
import com.samsungschool.umbrellaproject.databinding.FragmentProfileBinding;
import com.samsungschool.umbrellaproject.models.User;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private static final String ARG_KEY_USER = "user";

    public static ProfileFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_KEY_USER, user);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        Bundle arguments = getArguments();
        if (arguments != null) {
            User user = getArguments().getParcelable(ARG_KEY_USER);
            binding.userCard.textView6.setText(user.getName());
            binding.userCard.textView9.setText(user.getPhoneNumber());
            binding.userCard.textView11.setText(user.getMail());
        }

        binding.logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(SplashActivity.newIntent(requireActivity()));
            requireActivity().finish();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
