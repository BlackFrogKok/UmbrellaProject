package com.samsungschool.umbrellaproject.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentDialogCodeBinding;
import com.samsungschool.umbrellaproject.interfaces.NoticeDialogListener;

public class CodeDialogFragment extends DialogFragment {

    public static DialogFragment newInstance() {
        return new CodeDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentDialogCodeBinding binding = FragmentDialogCodeBinding.inflate(requireActivity().getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity()).setView(binding.getRoot())
                .setNegativeButton(R.string.decline, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.accept, (dialog, which) -> {
                    ((NoticeDialogListener) requireActivity()).onCodeSubmitted(binding.textEditCode.getText().toString());
                    dialog.dismiss();
                });
        return builder.create();
    }
}
