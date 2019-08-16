package com.tehamalab.collect.android.http;

import com.tehamalab.collect.android.http.okhttp.OkHttpOpenRosaServerClientFactory;
import com.tehamalab.collect.android.http.openrosa.OpenRosaServerClientFactory;
import com.tehamalab.collect.android.utilities.Clock;

import okhttp3.OkHttpClient;
import okhttp3.tls.internal.TlsUtil;

public class OkHttpOpenRosaServerClientFactoryTest extends OpenRosaServerClientFactoryTest {

    @Override
    protected OpenRosaServerClientFactory buildSubject(Clock clock) {
        OkHttpClient.Builder baseClient = new OkHttpClient.Builder()
                .sslSocketFactory(
                        TlsUtil.localhost().sslSocketFactory(),
                        TlsUtil.localhost().trustManager());
        
        return new OkHttpOpenRosaServerClientFactory(baseClient, clock);
    }

    @Override
    protected Boolean useRealHttps() {
        return true;
    }
}
