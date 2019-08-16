package com.tehamalab.collect.android.http.stub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tehamalab.collect.android.http.HttpCredentialsInterface;
import com.tehamalab.collect.android.http.HttpGetResult;

import java.net.URI;

public class StubOpenRosaHttpInterfaceError extends StubOpenRosaHttpInterface {

    @Override
    @NonNull
    public HttpGetResult executeGetRequest(@NonNull URI uri, @Nullable String contentType, @Nullable HttpCredentialsInterface credentials) throws Exception {
        return null;
    }
}
