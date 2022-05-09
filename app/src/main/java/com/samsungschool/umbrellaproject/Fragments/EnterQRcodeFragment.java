package com.samsungschool.umbrellaproject.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.samsungschool.umbrellaproject.Activity.QrActivity;
import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.DialogEnterCodeBinding;


public class EnterQRcodeFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String code);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {

            throw new ClassCastException("qrActivity"
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View mView = inflater.inflate(R.layout.dialog_enter_code, null);
        final EditText editText = (EditText)mView.findViewById(R.id.textEditCode);


        builder.setView(mView)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(EnterQRcodeFragment.this, editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(EnterQRcodeFragment.this);
                    }
        });

        return builder.create();
    }
}
