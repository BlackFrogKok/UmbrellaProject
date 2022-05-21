package com.samsungschool.umbrellaproject.Fragments.Auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsungschool.umbrellaproject.Interface.AutoSetCodeInterfaces;
import com.samsungschool.umbrellaproject.PinEntryEditText;
import com.samsungschool.umbrellaproject.databinding.FragmentEnterCodeBinding;

public class EnterCodeFragment extends Fragment implements AutoSetCodeInterfaces {

    private FragmentEnterCodeBinding binding;
    private String code = "";

    public static Fragment newFragment(){
        Fragment fragment = new EnterCodeFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void setCode(String code) {
        binding.txtPinEntry.setText(code);
    }

    public interface onEnterCodeFragmentListener {
        void ContinueBtnCode(String code);
        void ResetBtnCode(String phone);
    }

    onEnterCodeFragmentListener EnterCodeFragmentListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            EnterCodeFragmentListener = (onEnterCodeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }
    private void addCodeSimbol(String simbol) {
        if(code.length() < 6){
            code += simbol;
            binding.txtPinEntry.setText(code);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentEnterCodeBinding.inflate(getLayoutInflater());
        binding.buttons.button0.setOnClickListener(view -> addCodeSimbol("0"));
        binding.buttons.button1.setOnClickListener(view -> addCodeSimbol("1"));
        binding.buttons.button2.setOnClickListener(view -> addCodeSimbol("2"));
        binding.buttons.button3.setOnClickListener(view -> addCodeSimbol("3"));
        binding.buttons.button4.setOnClickListener(view -> addCodeSimbol("4"));
        binding.buttons.button5.setOnClickListener(view -> addCodeSimbol("5"));
        binding.buttons.button6.setOnClickListener(view -> addCodeSimbol("6"));
        binding.buttons.button7.setOnClickListener(view -> addCodeSimbol("7"));
        binding.buttons.button8.setOnClickListener(view -> addCodeSimbol("8"));
        binding.buttons.button9.setOnClickListener(view -> addCodeSimbol("9"));
        binding.buttons.buttonBack.setOnClickListener(view -> {
            if (code.length() != 0){
                code = code.substring(0, code.length() - 1);
                binding.txtPinEntry.setText(code);
            }

        });

        binding.txtPinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                if(str.length() == 6) EnterCodeFragmentListener.ContinueBtnCode(String.valueOf(str));
            }
        });

        return binding.getRoot();
    }
}