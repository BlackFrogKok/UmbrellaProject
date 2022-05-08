package com.samsungschool.umbrellaproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.common.SignInButton;
import com.google.zxing.Result;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.ActivityQrBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class QrActivity extends AppCompatActivity {
    ActivityQrBinding binding;
    private CodeScanner mCodeScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        CodeScannerView scannerView = findViewById(R.id.codeScannerView);
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
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mCodeScanner.startPreview();
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
}