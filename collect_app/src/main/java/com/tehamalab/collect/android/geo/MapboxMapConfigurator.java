package com.tehamalab.collect.android.geo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import com.google.common.collect.ImmutableSet;
import com.mapbox.mapboxsdk.maps.Style;
import com.tehamalab.collect.android.preferences.GeneralKeys;
import com.tehamalab.collect.android.preferences.PrefUtils;
import com.tehamalab.collect.android.utilities.ToastUtils;

import com.tehamalab.collect.android.R;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class MapboxMapConfigurator implements MapConfigurator {
    private final String prefKey;
    private final int sourceLabelId;
    private final MapboxUrlOption[] options;

    /** Constructs a configurator with a few Mapbox style URL options to choose from. */
    MapboxMapConfigurator(String prefKey, int sourceLabelId, MapboxUrlOption... options) {
        this.prefKey = prefKey;
        this.sourceLabelId = sourceLabelId;
        this.options = options;
    }

    @Override public boolean isAvailable(Context context) {
        return MapboxUtils.initMapbox() != null;
    }

    @Override public void showUnavailableMessage(Context context) {
        ToastUtils.showLongToast(context.getString(
            R.string.basemap_source_unavailable, context.getString(sourceLabelId)));
    }

    @Override public MapFragment createMapFragment(Context context) {
        return MapboxUtils.initMapbox() != null ? new MapboxMapFragment() : null;
    }

    @Override public List<Preference> createPrefs(Context context) {
        int[] labelIds = new int[options.length];
        String[] values = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            labelIds[i] = options[i].labelId;
            values[i] = options[i].url;
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
        config.putString(MapboxMapFragment.KEY_STYLE_URL,
            prefs.getString(GeneralKeys.KEY_MAPBOX_MAP_STYLE, Style.MAPBOX_STREETS));
        config.putString(MapboxMapFragment.KEY_REFERENCE_LAYER,
            prefs.getString(GeneralKeys.KEY_REFERENCE_LAYER, null));
        return config;
    }

    @Override public boolean supportsLayer(File file) {
        // MapboxMapFragment supports any file that MbtilesFile can read.
        return MbtilesFile.readLayerType(file) != null;
    }

    @Override public String getDisplayName(File file) {
        String name = MbtilesFile.readName(file);
        return name != null ? name : file.getName();
    }

    static class MapboxUrlOption {
        final String url;
        final int labelId;

        MapboxUrlOption(String url, int labelId) {
            this.url = url;
            this.labelId = labelId;
        }
    }
}
