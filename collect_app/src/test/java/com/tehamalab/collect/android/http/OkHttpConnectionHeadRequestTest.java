package com.tehamalab.collect.android.http;

import android.webkit.MimeTypeMap;

import org.junit.runner.RunWith;
import com.tehamalab.collect.android.http.okhttp.OkHttpConnection;
import com.tehamalab.collect.android.http.okhttp.OkHttpOpenRosaServerClientFactory;
import com.tehamalab.collect.android.http.openrosa.OpenRosaHttpInterface;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import okhttp3.OkHttpClient;

@RunWith(RobolectricTestRunner.class)
public class OkHttpConnectionHeadRequestTest extends OpenRosaHeadRequestTest {

    @Override
    protected OpenRosaHttpInterface buildSubject() {
        return new OkHttpConnection(
                new OkHttpOpenRosaServerClientFactory(new OkHttpClient.Builder(), Date::new),
                new CollectThenSystemContentTypeMapper(MimeTypeMap.getSingleton())
        );
    }
}
