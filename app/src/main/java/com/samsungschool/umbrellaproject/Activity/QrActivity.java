package com.samsungschool.umbrellaproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.common.SignInButton;
import com.google.zxing.Result;
import com.samsungschool.umbrellaproject.Fragments.EnterQRcodeFragment;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.ActivityQrBinding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class QrActivity extends AppCompatActivity implements EnterQRcodeFragment.NoticeDialogListener {
    ActivityQrBinding binding;
    private CodeScanner mCodeScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        CodeScannerView scannerView = binding.codeScannerView;
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                Intent data = new Intent();
                data.putExtra("qr", result.getText());
                setResult(RESULT_OK, data);
                finish();

            }
        });

        binding.materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment enterQRcodeFragment = new EnterQRcodeFragment();
                enterQRcodeFragment.show(getSupportFragmentManager(), null);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String text) {
        Intent data = new Intent();
        data.putExtra("qr", text);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}