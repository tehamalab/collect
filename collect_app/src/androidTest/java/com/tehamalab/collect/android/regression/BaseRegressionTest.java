package com.tehamalab.collect.android.regression;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import com.tehamalab.collect.android.activities.MainMenuActivity;

public class BaseRegressionTest {

    @Rule
    public ActivityTestRule<MainMenuActivity> main = new ActivityTestRule<>(MainMenuActivity.class);
}