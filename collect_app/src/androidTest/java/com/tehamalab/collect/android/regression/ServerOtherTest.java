package com.tehamalab.collect.android.regression;

import androidx.test.runner.AndroidJUnit4;

import com.tehamalab.collect.android.espressoutils.FormEntry;
import com.tehamalab.collect.android.espressoutils.MainMenu;
import com.tehamalab.collect.android.espressoutils.Settings;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.pressBack;

// Issue number NODK-235
@RunWith(AndroidJUnit4.class)
public class ServerOtherTest extends BaseRegressionTest {

    @Test
    public void formListPath_ShouldBeUpdated() {
        //TestCase1
        MainMenu.clickOnMenu();
        MainMenu.clickGeneralSettings();
        Settings.openServerSettings();
        Settings.clickOnServerType();
        FormEntry.clickOnAreaWithIndex("CheckedTextView", 2);
        FormEntry.clickOnAreaWithKey("formlist_url");
        FormEntry.focusOnTextAndTextInput("/formList", "/sialala");
        FormEntry.clickOk();
        FormEntry.checkIsTextDisplayed("/formList/sialala");
        pressBack();
        pressBack();
        Settings.resetSettings();
    }

    @Test
     public void submissionsPath_ShouldBeUpdated() {
         //TestCase2
         MainMenu.clickOnMenu();
         MainMenu.clickGeneralSettings();
         Settings.openServerSettings();
         Settings.clickOnServerType();
         FormEntry.clickOnAreaWithIndex("CheckedTextView", 2);
         FormEntry.clickOnAreaWithKey("submission_url");
         FormEntry.focusOnTextAndTextInput("/submission", "/blabla");
         FormEntry.clickOk();
         FormEntry.checkIsTextDisplayed("/submission/blabla");
         pressBack();
         pressBack();
         Settings.resetSettings();

    }

}
