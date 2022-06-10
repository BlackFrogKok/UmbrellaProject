package com.samsungschool.umbrellaproject.Fragments.NavigationItems.AboutFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.samsungschool.umbrellaproject.Activity.IntroActivity;
import com.samsungschool.umbrellaproject.BuildConfig;
import com.samsungschool.umbrellaproject.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    FragmentAboutBinding binding;
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(getLayoutInflater());
        setToolBarListener();
        binding.textView14.setText("Версия: " + BuildConfig.VERSION_CODE);


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
