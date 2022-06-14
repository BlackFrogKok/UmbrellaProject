package com.samsungschool.umbrellaproject.Fragments.NavigationItems.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.samsungschool.umbrellaproject.Activity.SplashScreen;
import com.samsungschool.umbrellaproject.User;
import com.samsungschool.umbrellaproject.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private User user;
    public static ProfileFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        user = getArguments().getParcelable("user");
        binding.userCard.textView6.setText(user.getName());
        binding.userCard.textView9.setText(user.getPhoneNumber());
        binding.userCard.textView11.setText(user.getMail());
        binding.logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), SplashScreen.class);
            startActivity(intent);
            getActivity().finish();
        });

        setToolBarListener();
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    private void setToolBarListener(){
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }
}
