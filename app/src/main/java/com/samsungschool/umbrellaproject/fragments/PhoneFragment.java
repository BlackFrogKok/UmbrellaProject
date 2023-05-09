package com.samsungschool.umbrellaproject.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.activities.AuthActivity;
import com.samsungschool.umbrellaproject.databinding.FragmentPhoneBinding;
import com.samsungschool.umbrellaproject.interfaces.AuthListener;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class PhoneFragment extends Fragment {

    private FragmentPhoneBinding binding;
    private CountryPicker picker;
    private FormatWatcher formatWatcherCodCountry;
    private FormatWatcher formatWatcherPhone;
    Slot[] slots;
    Slot[] slotsCodeCountry;
    public static String TAG_PHONE_FRAGMENT = "phone";

    public static Fragment newFragment() {
        Fragment fragment = new PhoneFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPhoneBinding.inflate(getLayoutInflater());
        binding.ContryCodeEditText2.setOnClickListener(v -> picker.showBottomSheet((AuthActivity) getActivity()));
        binding.ContryCodeEditText2.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER || i == KeyEvent.KEYCODE_BACK) {
                System.out.println("11111111111");
                binding.CountryCodeEditText.setFocusable(false);
                binding.CountryCodeEditText.setFocusableInTouchMode(false);
                return true;
            } else {
                return false;
            }
        });


        slots = new UnderscoreDigitSlotsParser().parseSlots("(___) ___-__-__");
        formatWatcherPhone = new MaskFormatWatcher(MaskImpl.createTerminated(slots));
        slotsCodeCountry = new UnderscoreDigitSlotsParser().parseSlots("+____");
        formatWatcherCodCountry = new MaskFormatWatcher(MaskImpl.createTerminated(slotsCodeCountry));

        formatWatcherPhone.installOn(binding.phoneEditText);
        formatWatcherCodCountry.installOn(binding.CountryCodeEditText);

        changeCountryCode(picker.getCountryFromSIM());

        binding.continueBtn.setOnClickListener(v -> {
            String phone = formatWatcherCodCountry.getMask().toString() + formatWatcherPhone.getMask().toUnformattedString();
            if (phone.length() >= 17) {
                ((AuthListener) requireActivity()).onPhoneSubmitted(phone);
            } else {
                binding.phoneTextInputLayout.setError(getString(R.string.error_wrong_format));
            }

        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("enteredPhone",  formatWatcherPhone.getMask().toUnformattedString());
        outState.putString("enteredCode", formatWatcherCodCountry.getMask().toString());
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("SetTextI18n")
    private void changeCountryCode(Country country) {
        if (country != null & !binding.CountryCodeEditText.hasFocus()) {
            binding.CountryCodeEditText.setText(country.getDialCode());
            binding.ContryCodeEditText2.setText(country.getDialCode() + " (" + country.getName() + ")");
        } else if (country != null & binding.CountryCodeEditText.hasFocus()) {
            binding.ContryCodeEditText2.setText(country.getDialCode() + " (" + country.getName() + ")");
        } else {
            binding.ContryCodeEditText2.setText(R.string.error_wrong_country);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        picker = new CountryPicker.Builder().with(context)
                .canSearch(true)
                .listener(this::changeCountryCode)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding.CountryCodeEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.phoneTextInputLayout.isErrorEnabled()) {
                    binding.phoneTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.CountryCodeEditText.hasFocus()) {
                    Log.w("test1", formatWatcherCodCountry.getMask().toUnformattedString());
                    changeCountryCode(picker.getCountryByDialCode(formatWatcherCodCountry.getMask().toUnformattedString()));
                    System.out.println("1");
                }
            }
        });

        binding.phoneEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.phoneTextInputLayout.isErrorEnabled()) {
                    binding.phoneTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
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
