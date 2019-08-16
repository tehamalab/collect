package com.tehamalab.collect.android.widgets;

import androidx.annotation.NonNull;

import com.tehamalab.collect.android.widgets.base.QuestionWidgetTest;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;

/**
 * @author James Knight
 */

public class UrlWidgetTest extends QuestionWidgetTest<UrlWidget, StringData> {
    @NonNull
    @Override
    public UrlWidget createWidget() {
        return new UrlWidget(activity, formEntryPrompt);
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(RandomString.make());
    }

    @Override
    public void callingClearShouldRemoveTheExistingAnswer() {
        // The widget is ReadOnly, clear shouldn't do anything.
    }
}
