package com.tehamalab.collect.android.injection.config;

import android.app.Application;
import android.content.Context;
import android.telephony.SmsManager;
import android.webkit.MimeTypeMap;

import com.google.android.gms.analytics.Tracker;
import com.tehamalab.collect.android.application.Collect;
import com.tehamalab.collect.android.utilities.DownloadFormListUtils;
import com.tehamalab.collect.android.utilities.PermissionUtils;
import com.tehamalab.collect.android.utilities.WebCredentialsUtils;

import com.tehamalab.collect.android.dao.FormsDao;
import com.tehamalab.collect.android.dao.InstancesDao;
import com.tehamalab.collect.android.events.RxEventBus;
import com.tehamalab.collect.android.http.CollectServerClient;
import com.tehamalab.collect.android.http.CollectThenSystemContentTypeMapper;
import com.tehamalab.collect.android.http.okhttp.OkHttpConnection;
import com.tehamalab.collect.android.http.okhttp.OkHttpOpenRosaServerClientFactory;
import com.tehamalab.collect.android.http.openrosa.OpenRosaHttpInterface;
import com.tehamalab.collect.android.tasks.sms.SmsSubmissionManager;
import com.tehamalab.collect.android.tasks.sms.contracts.SmsSubmissionManagerContract;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Add dependency providers here (annotated with @Provides)
 * for objects you need to inject
 */
@Module
public class AppDependencyModule {

    @Provides
    public SmsManager provideSmsManager() {
        return SmsManager.getDefault();
    }

    @Provides
    SmsSubmissionManagerContract provideSmsSubmissionManager(Application application) {
        return new SmsSubmissionManager(application);
    }

    @Provides
    Context context(Application application) {
        return application;
    }

    @Provides
    public InstancesDao provideInstancesDao() {
        return new InstancesDao();
    }

    @Provides
    public FormsDao provideFormsDao() {
        return new FormsDao();
    }

    @Provides
    @Singleton
    RxEventBus provideRxEventBus() {
        return new RxEventBus();
    }

    @Provides
    MimeTypeMap provideMimeTypeMap() {
        return MimeTypeMap.getSingleton();
    }

    @Provides
    @Singleton
    OpenRosaHttpInterface provideHttpInterface(MimeTypeMap mimeTypeMap) {
        return new OkHttpConnection(
                new OkHttpOpenRosaServerClientFactory(new OkHttpClient.Builder(), Date::new),
                new CollectThenSystemContentTypeMapper(mimeTypeMap)
        );
    }

    @Provides
    CollectServerClient provideCollectServerClient(OpenRosaHttpInterface httpInterface, WebCredentialsUtils webCredentialsUtils) {
        return new CollectServerClient(httpInterface, webCredentialsUtils);
    }

    @Provides
    WebCredentialsUtils provideWebCredentials() {
        return new WebCredentialsUtils();
    }

    @Provides
    DownloadFormListUtils provideDownloadFormListUtils(
            Application application,
            CollectServerClient collectServerClient,
            WebCredentialsUtils webCredentialsUtils,
            FormsDao formsDao) {
        return new DownloadFormListUtils(
                application,
                collectServerClient,
                webCredentialsUtils,
                formsDao
        );
    }

    @Provides
    @Singleton
    public Tracker providesTracker(Application application) {
        return ((Collect) application).getDefaultTracker();
    }

    @Provides
    public PermissionUtils providesPermissionUtils() {
        return new PermissionUtils();
    }
}
