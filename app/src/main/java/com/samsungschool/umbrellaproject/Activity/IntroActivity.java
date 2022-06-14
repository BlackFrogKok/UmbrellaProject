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
                "Возьмите зонт",
                "С помощью камеры отсканируйте QR-код на экране станции и начните аренду",
                R.drawable.ic_baseline_north_24,
                R.color.slide_1

        ));
        addSlide(AppIntroFragment.createInstance(
                "Дойдите до назначения",
                "Используйте зонт во время внезапного дождя",
                R.drawable.ic_baseline_umbrella_24,
                R.color.slide_2

        ));
        addSlide(AppIntroFragment.createInstance(
                "Верните зонт",
                "Доберитесь до цели и оставьте зонтик в ближайшей станции",
                R.drawable.ic_baseline_south_24,
                R.color.slide_3

        ));
        addSlide(AppIntroFragment.createInstance(
                "Вы готовы!",
                "",
                R.drawable.ic_baseline_add_task_24,
                R.color.slide_4

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
