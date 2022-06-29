package com.samsungschool.umbrellaproject.fragments;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.samsungschool.umbrellaproject.databinding.FragmentNfcBinding;

public class NfcFragment extends Fragment {

    private FragmentNfcBinding binding;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter intentFilter;
    private Tag mTag;

    public static NfcFragment newInstance() {
        Bundle args = new Bundle();
        NfcFragment fragment = new NfcFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNfcBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this.getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nfcAdapter = null;
        binding = null;
    }
}
