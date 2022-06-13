package com.samsungschool.umbrellaproject.Fragments.Auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.samsungschool.umbrellaproject.Activity.Auth.SignInActivity;
import com.samsungschool.umbrellaproject.databinding.FragmentEnterPhoneBinding;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class EnterPhoneFragment extends Fragment {

    private FragmentEnterPhoneBinding binding;
    private CountryPicker picker;
    private FormatWatcher formatWatcherCodCountry;

    public static Fragment newFragment() {
        Fragment fragment = new EnterPhoneFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    public interface onEnterPhoneListener {
        void ContinueBtnPhone(String phone);
    }

    onEnterPhoneListener phoneEnterListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentEnterPhoneBinding.inflate(getLayoutInflater());
        binding.ContryCodeEditText2.setOnClickListener(v -> picker.showBottomSheet((SignInActivity)getActivity()));
        binding.ContryCodeEditText2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER || i == KeyEvent.KEYCODE_BACK){
                    System.out.println("11111111111");
                    binding.CountryCodeEditText.setFocusable(false);
                    binding.CountryCodeEditText.setFocusableInTouchMode(false);
                    return true;
                }else{
                    return false;
                }
            }
        });

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("(___) ___-__-__");
        FormatWatcher formatWatcherPhone = new MaskFormatWatcher(MaskImpl.createTerminated(slots));

        Slot[] slotsCodeContry = new UnderscoreDigitSlotsParser().parseSlots("+____");
        formatWatcherCodCountry = new MaskFormatWatcher(MaskImpl.createTerminated(slotsCodeContry));

        formatWatcherPhone.installOn(binding.phoneEditText);
        formatWatcherCodCountry.installOn(binding.CountryCodeEditText);

        changeCountryCode(picker.getCountryFromSIM());

        binding.continueBtn.setOnClickListener(v -> {
            String s = formatWatcherCodCountry.getMask().toString() + formatWatcherPhone.getMask().toUnformattedString();
            if(s.length() >= 17){
                phoneEnterListener.ContinueBtnPhone(s);
            }
            else{
                binding.phoneTextInputLayout.setError("Неверный формат");
            }

        });
    }

    private void changeCountryCode(Country country){
        if(country != null & !binding.CountryCodeEditText.hasFocus()){
            binding.CountryCodeEditText.setText(country.getDialCode());
            binding.ContryCodeEditText2.setText(country.getDialCode() + " (" + country.getName() + ")");
        } else if(country != null & binding.CountryCodeEditText.hasFocus()){
            binding.ContryCodeEditText2.setText(country.getDialCode() + " (" + country.getName() + ")");
        }else {
            binding.ContryCodeEditText2.setText("Неверно введён код страны");
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CountryPicker.Builder builder = new CountryPicker.Builder().with(context).canSearch(true)
                .listener(new OnCountryPickerListener() {
                    @Override
                    public void onSelectCountry(Country country) {
                        changeCountryCode(country);
                    }
                });
        picker = builder.build();
        try {
            phoneEnterListener = (onEnterPhoneListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding.CountryCodeEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.phoneTextInputLayout.isErrorEnabled()){
                    binding.phoneTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(binding.CountryCodeEditText.hasFocus()){
                    Log.w("test1", formatWatcherCodCountry.getMask().toUnformattedString());
                    changeCountryCode(picker.getCountryByDialCode(formatWatcherCodCountry.getMask().toUnformattedString()));
                    System.out.println("1");
                }
            }
        });

        binding.phoneEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.phoneTextInputLayout.isErrorEnabled()){
                    binding.phoneTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return binding.getRoot();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}