package com.tehamalab.collect.android.http.openrosa;

import androidx.annotation.Nullable;

import com.tehamalab.collect.android.http.HttpCredentialsInterface;

public interface OpenRosaServerClientFactory {

    OpenRosaServerClient create(String schema, String userAgent, @Nullable HttpCredentialsInterface credentialsInterface);
}
