package com.tehamalab.collect.android.externalintents;

import android.app.Activity;
import androidx.test.rule.ActivityTestRule;

import static com.tehamalab.collect.android.externalintents.ExportedActivitiesUtils.clearDirectories;

class ExportedActivityTestRule<A extends Activity> extends ActivityTestRule<A> {

    ExportedActivityTestRule(Class<A> activityClass) {
        super(activityClass);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();

        clearDirectories();
    }

}
