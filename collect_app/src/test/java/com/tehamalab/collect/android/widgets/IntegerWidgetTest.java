package com.tehamalab.collect.android.widgets;

import androidx.annotation.NonNull;

import com.tehamalab.collect.android.widgets.base.GeneralStringWidgetTest;

import org.javarosa.core.model.data.IntegerData;

import java.util.Random;

/**
 * @author James Knight
 */
public class IntegerWidgetTest extends GeneralStringWidgetTest<IntegerWidget, IntegerData> {

    @NonNull
    @Override
    public IntegerWidget createWidget() {
        Random random = new Random();
        boolean useThousandSeparator = random.nextBoolean();
        return new IntegerWidget(activity, formEntryPrompt, false, useThousandSeparator);
    }

    @NonNull
    @Override
    public IntegerData getNextAnswer() {
        return new IntegerData(randomInteger());
    }

    private int randomInteger() {
        return Math.abs(random.nextInt()) % 1_000_000_000;
    }
}
