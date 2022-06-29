package com.samsungschool.umbrellaproject.fragments;

import static java.lang.String.format;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.samsungschool.umbrellaproject.BuildConfig;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(getLayoutInflater());
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        binding.textView14.setText(format(getString(R.string.version), BuildConfig.VERSION_CODE));
        return binding.getRoot();
    }


    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
