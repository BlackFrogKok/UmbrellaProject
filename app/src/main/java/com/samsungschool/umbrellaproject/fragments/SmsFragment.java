package com.samsungschool.umbrellaproject.fragments;

import static java.lang.String.format;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentSmsBinding;
import com.samsungschool.umbrellaproject.interfaces.AuthListener;
import com.samsungschool.umbrellaproject.interfaces.AutoSetCode;

public class SmsFragment extends Fragment implements AutoSetCode {

    private String code = "";

    private FragmentSmsBinding binding;

    public static String TAG_CODE_FRAGMENT = "code";
    private static final String ARG_KEY_PHONE = "phone";

    public static Fragment newFragment(String phoneNumber) {
        Fragment fragment = new SmsFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_KEY_PHONE, phoneNumber);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSmsBinding.inflate(getLayoutInflater());

        Bundle arguments = getArguments();
        if (arguments != null) binding.smsCodeText.setText(format(getString(R.string.hint_sms), arguments.getString(ARG_KEY_PHONE)));

        binding.buttons.button0.setOnClickListener(view -> addCodeSymbol("0"));
        binding.buttons.button1.setOnClickListener(view -> addCodeSymbol("1"));
        binding.buttons.button2.setOnClickListener(view -> addCodeSymbol("2"));
        binding.buttons.button3.setOnClickListener(view -> addCodeSymbol("3"));
        binding.buttons.button4.setOnClickListener(view -> addCodeSymbol("4"));
        binding.buttons.button5.setOnClickListener(view -> addCodeSymbol("5"));
        binding.buttons.button6.setOnClickListener(view -> addCodeSymbol("6"));
        binding.buttons.button7.setOnClickListener(view -> addCodeSymbol("7"));
        binding.buttons.button8.setOnClickListener(view -> addCodeSymbol("8"));
        binding.buttons.button9.setOnClickListener(view -> addCodeSymbol("9"));

        binding.buttons.buttonBack.setOnClickListener(view -> {
            if (code.length() != 0) {
                code = code.substring(0, code.length() - 1);
                binding.txtPinEntry.setText(code);
            }
        });

        binding.txtPinEntry.setOnPinEnteredListener(str -> {
            if (str.length() == 6) {
                ((AuthListener) requireActivity()).onSmsSubmitted(String.valueOf(str));
            }
        });

        return binding.getRoot();
    }

    @Override
    public void setCode(String code) {
        this.code = code;
        binding.txtPinEntry.setText(code);
    }

    private void addCodeSymbol(String simbol) {
        if (code.length() < 6) {
            code += simbol;
            binding.txtPinEntry.setText(code);
        }
    }
}
