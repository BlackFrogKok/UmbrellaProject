package com.samsungschool.umbrellaproject.Fragments.NavigationItems.QRReadFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentQrBinding;

public class QrFragment extends Fragment {
    FragmentQrBinding binding;
    private CodeScanner mCodeScanner;
    public static QrFragment newInstance() {
        Bundle args = new Bundle();
        QrFragment fragment = new QrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQrBinding.inflate(getLayoutInflater());
        final Activity activity = getActivity();
        CodeScannerView codeScannerView = binding.qrCamera;



        mCodeScanner = new CodeScanner(activity, codeScannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }


}

