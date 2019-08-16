/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tehamalab.collect.android.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

import android.widget.Button;

import com.tehamalab.collect.android.BuildConfig;
import com.tehamalab.collect.android.activities.CaptureSelfieActivity;
import com.tehamalab.collect.android.activities.CaptureSelfieActivityNewApi;
import com.tehamalab.collect.android.application.Collect;
import com.tehamalab.collect.android.listeners.PermissionListener;
import com.tehamalab.collect.android.utilities.ApplicationConstants;
import com.tehamalab.collect.android.utilities.CameraUtils;
import com.tehamalab.collect.android.utilities.FileUtils;
import com.tehamalab.collect.android.utilities.WidgetAppearanceUtils;

import org.javarosa.form.api.FormEntryPrompt;
import com.tehamalab.collect.android.R;

import java.io.File;
import java.util.Locale;

import timber.log.Timber;

/**
 * Widget that allows user to take pictures, sounds or video and add them to the form.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */
public class ImageWidget extends BaseImageWidget {

    private Button captureButton;
    private Button chooseButton;

    private boolean selfie;

    public ImageWidget(Context context, final FormEntryPrompt prompt) {
        super(context, prompt);
        imageClickHandler = new ViewImageClickHandler();
        imageCaptureHandler = new ImageCaptureHandler();
        setUpLayout();
        addCurrentImageToLayout();
        addAnswerView(answerLayout);
    }

    @Override
    protected void setUpLayout() {
        super.setUpLayout();

        String appearance = getFormEntryPrompt().getAppearanceHint();
        selfie = appearance != null && (appearance.equalsIgnoreCase(WidgetAppearanceUtils.SELFIE)
                || appearance.equalsIgnoreCase(WidgetAppearanceUtils.NEW_FRONT));

        captureButton = getSimpleButton(getContext().getString(R.string.capture_image), R.id.capture_image);

        chooseButton = getSimpleButton(getContext().getString(R.string.choose_image), R.id.choose_image);

        answerLayout.addView(captureButton);
        answerLayout.addView(chooseButton);
        answerLayout.addView(errorTextView);

        hideButtonsIfNeeded(appearance);
        errorTextView.setVisibility(GONE);

        if (selfie) {
            if (!CameraUtils.isFrontCameraAvailable()) {
                captureButton.setEnabled(false);
                errorTextView.setText(R.string.error_front_camera_unavailable);
                errorTextView.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public Intent addExtrasToIntent(Intent intent) {
        return intent;
    }

    @Override
    public void clearAnswer() {
        super.clearAnswer();
        // reset buttons
        captureButton.setText(getContext().getString(R.string.capture_image));
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        captureButton.setOnLongClickListener(l);
        chooseButton.setOnLongClickListener(l);
        super.setOnLongClickListener(l);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        captureButton.cancelLongPress();
        chooseButton.cancelLongPress();
    }

    @Override
    public void onButtonClick(int buttonId) {
        switch (buttonId) {
            case R.id.capture_image:
                getPermissionUtils().requestCameraPermission((Activity) getContext(), new PermissionListener() {
                    @Override
                    public void granted() {
                        captureImage();
                    }

                    @Override
                    public void denied() {
                    }
                });
                break;
            case R.id.choose_image:
                imageCaptureHandler.chooseImage(R.string.choose_image);
                break;
        }
    }

    private void hideButtonsIfNeeded(String appearance) {
        if (selfie || ((appearance != null
                && appearance.toLowerCase(Locale.ENGLISH).contains(WidgetAppearanceUtils.NEW)))) {
            chooseButton.setVisibility(GONE);
        }
    }

    private void captureImage() {
        errorTextView.setVisibility(GONE);
        Intent intent;
        if (selfie) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(getContext(), CaptureSelfieActivityNewApi.class);
            } else {
                intent = new Intent(getContext(), CaptureSelfieActivity.class);
            }
        } else {
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            // We give the camera an absolute filename/path where to put the
            // picture because of bug:
            // http://code.google.com/p/android/issues/detail?id=1480
            // The bug appears to be fixed in Android 2.0+, but as of feb 2,
            // 2010, G1 phones only run 1.6. Without specifying the path the
            // images returned by the camera in 1.6 (and earlier) are ~1/4
            // the size. boo.

            try {
                Uri uri = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        new File(Collect.TMPFILE_PATH));
                // if this gets modified, the onActivityResult in
                // FormEntyActivity will also need to be updated.
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                FileUtils.grantFilePermissions(intent, uri, getContext());
            } catch (IllegalArgumentException e) {
                Timber.e(e);
            }
        }

        imageCaptureHandler.captureImage(intent, ApplicationConstants.RequestCodes.IMAGE_CAPTURE, R.string.capture_image);
    }

}
