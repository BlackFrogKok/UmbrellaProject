package com.samsungschool.umbrellaproject.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.samsungschool.umbrellaproject.R;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
        setColorTransitionsEnabled(true);

        addSlide(AppIntroFragment.createInstance(
                "Slide 1",
                "slide 1 description",
                R.drawable.station,
                R.color.primaryColor

        ));
        addSlide(AppIntroFragment.createInstance(
                "Slide 1",
                "slide 1 description",
                R.drawable.station,
                R.color.primaryColor_night

        ));
        addSlide(AppIntroFragment.createInstance(
                "Slide 1",
                "slide 1 description",
                R.drawable.station,
                com.firebase.ui.auth.R.color.colorPrimary

        ));
        addSlide(AppIntroFragment.createInstance(
                "Slide 1",
                "slide 1 description",
                R.drawable.station,
                com.firebase.ui.auth.R.color.colorPrimaryDark

        ));
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
