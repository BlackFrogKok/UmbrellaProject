package com.samsungschool.umbrellaproject.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.makeText;
import static com.samsungschool.umbrellaproject.utils.PrefUtils.PREF_KEY_LANGUAGE;
import static com.samsungschool.umbrellaproject.utils.PrefUtils.PREF_KEY_THEME;
import static com.samsungschool.umbrellaproject.utils.PrefUtils.PREF_NAME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.samsungschool.umbrellaproject.R;

public class PreferenceFragment extends PreferenceFragmentCompat {

    private SharedPreferences sharedPreferences;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        DropDownPreference themePreference = findPreference(PREF_KEY_THEME);
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                sharedPreferences.edit().putInt(PREF_KEY_THEME, Integer.parseInt((String) newValue)).apply();
                showMessage(R.string.theme_set);
                return true;
            });
        }

        DropDownPreference languagePreference = findPreference(PREF_KEY_LANGUAGE);
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                sharedPreferences.edit().putInt(PREF_KEY_LANGUAGE, Integer.parseInt((String) newValue)).apply();
                showMessage(R.string.language_set);
                return true;
            });
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.menu_settings, rootKey);
    }

    private void showMessage(@StringRes int messageRes) {
        makeText(getContext(), messageRes, Toast.LENGTH_SHORT).show();
    }
}

