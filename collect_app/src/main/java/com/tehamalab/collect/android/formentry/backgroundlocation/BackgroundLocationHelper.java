package com.tehamalab.collect.android.formentry.backgroundlocation;

import android.location.Location;

import com.tehamalab.collect.android.activities.FormEntryActivity;
import com.tehamalab.collect.android.application.Collect;
import com.tehamalab.collect.android.logic.FormController;
import com.tehamalab.collect.android.preferences.GeneralKeys;
import com.tehamalab.collect.android.preferences.GeneralSharedPreferences;
import com.tehamalab.collect.android.utilities.PermissionUtils;
import com.tehamalab.collect.android.location.client.LocationClients;
import com.tehamalab.collect.android.logic.AuditConfig;
import com.tehamalab.collect.android.logic.AuditEvent;

/**
 * Wrapper on resources needed by {@link BackgroundLocationManager} to make testing easier.
 *
 * Ideally this would be replaced by more coherent abstractions in the future.
 *
 * The methods on the {@link FormController} are wrapped here rather
 * than the form controller being passed in when constructing the {@link BackgroundLocationManager}
 * because the form controller isn't set until
 * {@link FormEntryActivity}'s onCreate.
 */
public class BackgroundLocationHelper {
    boolean isAndroidLocationPermissionGranted() {
        return PermissionUtils.areLocationPermissionsGranted(Collect.getInstance().getApplicationContext());
    }

    boolean isBackgroundLocationPreferenceEnabled() {
        return GeneralSharedPreferences.getInstance().getBoolean(GeneralKeys.KEY_BACKGROUND_LOCATION, true);
    }

    boolean arePlayServicesAvailable() {
        return LocationClients.areGooglePlayServicesAvailable(Collect.getInstance().getApplicationContext());
    }

    /**
     * @return true if the global form controller has been initialized.
     */
    boolean isCurrentFormSet() {
        return Collect.getInstance().getFormController() != null;
    }

    /**
     * @return true if the current form definition requests any kind of background location.
     *
     * Precondition: the global form controller has been initialized.
     */
    boolean currentFormCollectsBackgroundLocation() {
        return Collect.getInstance().getFormController().currentFormCollectsBackgroundLocation();
    }

    /**
     * @return true if the current form definition requests a background location audit, false
     * otherwise.
     *
     * Precondition: the global form controller has been initialized.
     */
    boolean currentFormAuditsLocation() {
        return Collect.getInstance().getFormController().currentFormAuditsLocation();
    }

    /**
     * @return the configuration for the audit requested by the current form definition.
     *
     * Precondition: the global form controller has been initialized.
     */
    AuditConfig getCurrentFormAuditConfig() {
        return Collect.getInstance().getFormController().getSubmissionMetadata().auditConfig;
    }

    /**
     * Logs an audit event of the given type.
     *
     * Precondition: the global form controller has been initialized.
     */
    void logAuditEvent(AuditEvent.AuditEventType eventType) {
        Collect.getInstance().getFormController().getAuditEventLogger().logEvent(eventType, false);
    }

    /**
     * Provides the location to the global audit event logger.
     *
     * Precondition: the global form controller has been initialized.
     */
    void provideLocationToAuditLogger(Location location) {
        Collect.getInstance().getFormController().getAuditEventLogger().addLocation(location);
    }
}
