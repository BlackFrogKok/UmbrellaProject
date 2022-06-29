package com.samsungschool.umbrellaproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.samsungschool.umbrellaproject.databinding.ActivityQrBinding;
import com.samsungschool.umbrellaproject.fragments.CodeDialogFragment;
import com.samsungschool.umbrellaproject.interfaces.NoticeDialogListener;

public class QrActivity extends AppCompatActivity implements NoticeDialogListener {

    public static final String EXTRA_KEY_CODE = "code";

    private ActivityQrBinding binding;
    private CodeScanner mCodeScanner;

    public static Intent newIntent(Context context) {
        return new Intent(context, QrActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mCodeScanner = new CodeScanner(this, binding.codeScannerView);
        mCodeScanner.setDecodeCallback(result -> setResult(result.getText()));

        binding.materialButton.setOnClickListener(v -> CodeDialogFragment.newInstance().show(getSupportFragmentManager(), null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCodeScanner.releaseResources();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Override
    public void onCodeSubmitted(String code) {
        setResult(code);
    }

    private void setResult(String code) {
        Intent data = getIntent();
        data.putExtra(EXTRA_KEY_CODE, code);
        setResult(RESULT_OK, data);
        finish();
    }
}
