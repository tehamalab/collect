package com.tehamalab.collect.android.widgets;

import androidx.annotation.NonNull;

import com.tehamalab.collect.android.widgets.base.GeneralExStringWidgetTest;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;

import static org.mockito.Mockito.when;

/**
 * @author James Knight
 */

public class ExStringWidgetTest extends GeneralExStringWidgetTest<ExStringWidget, StringData> {

    @NonNull
    @Override
    public ExStringWidget createWidget() {
        return new ExStringWidget(activity, formEntryPrompt);
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(RandomString.make());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(formEntryPrompt.getAppearanceHint()).thenReturn("");
    }
}
