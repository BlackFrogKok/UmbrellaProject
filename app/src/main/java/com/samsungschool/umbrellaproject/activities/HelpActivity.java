package com.samsungschool.umbrellaproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.samsungschool.umbrellaproject.R;

public class HelpActivity extends AppIntro {

    public static Intent newIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
        setColorTransitionsEnabled(true);

        newSlide(R.string.help_slide_1_title, R.string.help_slide_1_description, R.drawable.ic_baseline_north_24, R.color.slide_1);
        newSlide(R.string.help_slide_2_title, R.string.help_slide_2_description, R.drawable.ic_baseline_umbrella_24, R.color.slide_2);
        newSlide(R.string.help_slide_3_title, R.string.help_slide_3_description, R.drawable.ic_baseline_south_24, R.color.slide_3);
        newSlide(R.string.help_slide_4_title, R.string.help_slide_4_description, R.drawable.ic_baseline_add_task_24, R.color.slide_4);
    }

    private void newSlide(@StringRes int title, @StringRes int description, @DrawableRes int iconRes, @ColorRes int colorRes) {
        addSlide(AppIntroFragment.createInstance(getString(title), getString(description), iconRes, colorRes));

    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
