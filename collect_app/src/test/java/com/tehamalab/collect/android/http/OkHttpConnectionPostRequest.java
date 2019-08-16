package com.tehamalab.collect.android.http;

import org.junit.runner.RunWith;
import com.tehamalab.collect.android.http.okhttp.OkHttpConnection;
import com.tehamalab.collect.android.http.okhttp.OkHttpOpenRosaServerClientFactory;
import com.tehamalab.collect.android.http.openrosa.OpenRosaHttpInterface;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import okhttp3.OkHttpClient;

@RunWith(RobolectricTestRunner.class)
public class OkHttpConnectionPostRequest extends OpenRosaPostRequestTest {

    @Override
    protected OpenRosaHttpInterface buildSubject(OpenRosaHttpInterface.FileToContentTypeMapper mapper) {
        return new OkHttpConnection(
                new OkHttpOpenRosaServerClientFactory(new OkHttpClient.Builder(), Date::new),
                mapper
        );
    }
}
