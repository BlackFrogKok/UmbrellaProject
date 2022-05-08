package com.samsungschool.umbrellaproject.Fragments.NavigationItems.SettingsFragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.samsungschool.umbrellaproject.R;

public class PreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.menu_settings, rootKey);
    }
}
