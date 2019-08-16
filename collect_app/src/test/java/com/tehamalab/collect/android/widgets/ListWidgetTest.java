package com.tehamalab.collect.android.widgets;

import androidx.annotation.NonNull;

import com.tehamalab.collect.android.widgets.base.GeneralSelectOneWidgetTest;

/**
 * @author James Knight
 */

public class ListWidgetTest extends GeneralSelectOneWidgetTest<ListWidget> {
    @NonNull
    @Override
    public ListWidget createWidget() {
        return new ListWidget(activity, formEntryPrompt, false, false);
    }
}
