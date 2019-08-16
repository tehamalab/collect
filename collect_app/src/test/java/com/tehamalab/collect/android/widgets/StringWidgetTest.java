package com.tehamalab.collect.android.widgets;

import androidx.annotation.NonNull;

import com.tehamalab.collect.android.widgets.base.GeneralStringWidgetTest;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;

/**
 * @author James Knight
 */
public class StringWidgetTest extends GeneralStringWidgetTest<StringWidget, StringData> {

    @NonNull
    @Override
    public StringWidget createWidget() {
        return new StringWidget(activity, formEntryPrompt, false);
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(RandomString.make());
    }
}
