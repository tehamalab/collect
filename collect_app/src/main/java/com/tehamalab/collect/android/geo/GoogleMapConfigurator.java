package com.tehamalab.collect.android.geo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import com.google.android.gms.maps.GoogleMap;
import com.google.common.collect.ImmutableSet;
import com.tehamalab.collect.android.preferences.GeneralKeys;
import com.tehamalab.collect.android.preferences.PrefUtils;
import com.tehamalab.collect.android.utilities.PlayServicesUtil;
import com.tehamalab.collect.android.utilities.ToastUtils;

import com.tehamalab.collect.android.R;
import com.tehamalab.collect.android.geo.MbtilesFile.LayerType;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class GoogleMapConfigurator implements MapConfigurator {
    private final String prefKey;
    private final int sourceLabelId;
    private final GoogleMapTypeOption[] options;

    /** Constructs a configurator with a few Google map type options to choose from. */
    GoogleMapConfigurator(String prefKey, int sourceLabelId, GoogleMapTypeOption... options) {
        this.prefKey = prefKey;
        this.sourceLabelId = sourceLabelId;
        this.options = options;
    }

    @Override public boolean isAvailable(Context context) {
        return isGoogleMapsSdkAvailable(context) && isGooglePlayServicesAvailable(context);
    }

    @Override public void showUnavailableMessage(Context context) {
        if (!isGoogleMapsSdkAvailable(context)) {
            ToastUtils.showLongToast(context.getString(
                R.string.basemap_source_unavailable, context.getString(sourceLabelId)));
        }
        if (!isGooglePlayServicesAvailable(context)) {
            PlayServicesUtil.showGooglePlayServicesAvailabilityErrorDialog(context);
        }
    }

    private boolean isGoogleMapsSdkAvailable(Context context) {
        // The Google Maps SDK for Android requires OpenGL ES version 2.
        // See https://developers.google.com/maps/documentation/android-sdk/config
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000;
    }

    private boolean isGooglePlayServicesAvailable(Context context) {
        return PlayServicesUtil.isGooglePlayServicesAvailable(context);
    }

    @Override public MapFragment createMapFragment(Context context) {
        return new GoogleMapFragment();
    }

    @Override public List<Preference> createPrefs(Context context) {
        int[] labelIds = new int[options.length];
        String[] values = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            labelIds[i] = options[i].labelId;
            values[i] = Integer.toString(options[i].mapType);
        }
        String prefTitle = context.getString(
            R.string.map_style_label, context.getString(sourceLabelId));
        return Collections.singletonList(PrefUtils.createListPref(
            context, prefKey, prefTitle, labelIds, values
        ));
    }

    @Override public Set<String> getPrefKeys() {
        return prefKey.isEmpty() ? ImmutableSet.of(GeneralKeys.KEY_REFERENCE_LAYER) :
            ImmutableSet.of(prefKey, GeneralKeys.KEY_REFERENCE_LAYER);
    }

    @Override public Bundle buildConfig(SharedPreferences prefs) {
        Bundle config = new Bundle();
        config.putInt(GoogleMapFragment.KEY_MAP_TYPE,
            PrefUtils.getInt(GeneralKeys.KEY_GOOGLE_MAP_STYLE, GoogleMap.MAP_TYPE_NORMAL));
        config.putString(GoogleMapFragment.KEY_REFERENCE_LAYER,
            prefs.getString(GeneralKeys.KEY_REFERENCE_LAYER, null));
        return config;
    }

    @Override public boolean supportsLayer(File file) {
        // GoogleMapFragment supports only raster tiles.
        return MbtilesFile.readLayerType(file) == LayerType.RASTER;
    }

    @Override public String getDisplayName(File file) {
        String name = MbtilesFile.readName(file);
        return name != null ? name : file.getName();
    }

    static class GoogleMapTypeOption {
        final int mapType;
        final int labelId;

        GoogleMapTypeOption(int mapType, int labelId) {
            this.mapType = mapType;
            this.labelId = labelId;
        }
    }
}
