package com.tehamalab.collect.android.widgets;

import androidx.annotation.NonNull;

import com.tehamalab.collect.android.widgets.base.WidgetTest;

import org.javarosa.core.model.data.BooleanData;
import org.junit.Test;
import com.tehamalab.collect.android.activities.FormEntryActivity;

import org.robolectric.Robolectric;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author James Knight
 */
public class BooleanWidgetTest extends WidgetTest {

    @NonNull
    private BooleanWidget getWidget() {
        return new BooleanWidget(Robolectric.buildActivity(FormEntryActivity.class).create().get(), formEntryPrompt);
    }

    // BooleanWidgets always return a non-null answer, so we need to handle these tests manually:

    @Override
    public void getAnswerShouldReturnNullIfPromptDoesNotHaveExistingAnswer() {
        when(formEntryPrompt.getAnswerValue()).thenReturn(new BooleanData(false));

        BooleanWidget widget = getWidget();

        assertFalse(widget.isChecked());
        assertFalse((Boolean) widget.getAnswer().getValue());
    }

    @Override
    public void getAnswerShouldReturnExistingAnswerIfPromptHasExistingAnswer() {
        when(formEntryPrompt.getAnswerValue()).thenReturn(new BooleanData(false));

        BooleanWidget widget = getWidget();

        assertFalse(widget.isChecked());
        assertFalse((Boolean) widget.getAnswer().getValue());

        widget.isChecked(true);

        assertTrue(widget.isChecked());
        assertTrue((Boolean) widget.getAnswer().getValue());
    }

    @Override
    public void callingClearShouldRemoveTheExistingAnswer() {
        when(formEntryPrompt.getAnswerValue()).thenReturn(new BooleanData(true));

        BooleanWidget widget = getWidget();

        assertTrue(widget.isChecked());
        assertTrue((Boolean) widget.getAnswer().getValue());

        widget.clearAnswer();

        assertFalse(widget.isChecked());
        assertFalse((Boolean) widget.getAnswer().getValue());
    }

    @Test
    public void changingTheCheckboxShouldChangeTheAnswer() {
        when(formEntryPrompt.getAnswerValue()).thenReturn(new BooleanData(false));

        BooleanWidget widget = getWidget();

        assertFalse(widget.isChecked());
        assertFalse((Boolean) widget.getAnswer().getValue());

        widget.isChecked(true);

        assertTrue(widget.isChecked());
        assertTrue((Boolean) widget.getAnswer().getValue());
    }
}
