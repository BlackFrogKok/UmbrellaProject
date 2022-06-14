package com.samsungschool.umbrellaproject.Fragments.NavigationItems.SettingsFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.samsungschool.umbrellaproject.R;

public class PreferenceFragment extends PreferenceFragmentCompat {

    private DropDownPreference uiTheme;
    private DropDownPreference language;
    private SharedPreferences sp;
    private static final String UI_THEME_SETTINGS = "ui_theme_mode";
    private static final String LANGUAGE_SETTINGS = "language";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uiTheme = (DropDownPreference) findPreference("ui_theme");
        language = (DropDownPreference) findPreference("language");
        sp = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);

        uiTheme.setOnPreferenceChangeListener((preference, newValue) -> {
            sp.edit().putInt(UI_THEME_SETTINGS, Integer.parseInt((String) newValue)).apply();
            Toast.makeText(getContext(),"Тема установлена, перезагрузите приложение", Toast.LENGTH_SHORT).show();
            return true;
        });
        language.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                sp.edit().putInt(LANGUAGE_SETTINGS, Integer.parseInt((String) newValue)).apply();
                Toast.makeText(getContext(),"Язык установлен, перезагрузите приложение", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.menu_settings, rootKey);
    }

}

