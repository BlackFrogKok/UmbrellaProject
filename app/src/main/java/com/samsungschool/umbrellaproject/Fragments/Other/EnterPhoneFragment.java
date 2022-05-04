package com.samsungschool.umbrellaproject.Fragments.Other;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsungschool.umbrellaproject.databinding.FragmentEnterPhoneBinding;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class EnterPhoneFragment extends Fragment {

    private FragmentEnterPhoneBinding binding;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            phoneEnterListener = (onEnterPhoneListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterPhoneBinding.inflate(getLayoutInflater());

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("(___) ___-__-__");
        FormatWatcher formatWatcherPhone = new MaskFormatWatcher(MaskImpl.createTerminated(slots));

        Slot[] slotsCodeContry = new UnderscoreDigitSlotsParser().parseSlots("+____");
        FormatWatcher formatWatcherCodCountry = new MaskFormatWatcher(MaskImpl.createTerminated(slotsCodeContry));

        formatWatcherPhone.installOn(binding.phoneEditText);
        formatWatcherCodCountry.installOn(binding.ContryCodeEditText);

        binding.continueBtn.setOnClickListener(v -> {
            phoneEnterListener.ContinueBtnPhone(formatWatcherCodCountry.getMask().toString() + formatWatcherPhone.getMask().toUnformattedString());
        });
        return binding.getRoot();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}