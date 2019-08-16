package com.tehamalab.collect.android.sms;

import android.content.Context;
import android.telephony.SmsManager;

import com.tehamalab.collect.android.sms.base.BaseSmsTest;
import com.tehamalab.collect.android.sms.base.SampleData;
import com.tehamalab.collect.android.support.RobolectricHelpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.tehamalab.collect.android.dao.FormsDao;
import com.tehamalab.collect.android.dao.InstancesDao;
import com.tehamalab.collect.android.events.RxEventBus;
import com.tehamalab.collect.android.logic.FormInfo;
import com.tehamalab.collect.android.tasks.sms.SmsSender;
import com.tehamalab.collect.android.tasks.sms.SmsService;
import com.tehamalab.collect.android.tasks.sms.contracts.SmsSubmissionManagerContract;
import com.tehamalab.collect.android.tasks.sms.models.SmsSubmission;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowSmsManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.tehamalab.collect.android.utilities.FileUtil.getSmsInstancePath;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class SmsServiceTest extends BaseSmsTest {

    SmsSubmissionManagerContract submissionManager;
    SmsManager smsManager;
    InstancesDao instancesDao;
    FormsDao formsDao;
    RxEventBus eventBus;

    private StubSmsService smsService;

    @Before
    public void setUp() {
        instancesDao = mock(InstancesDao.class);
        formsDao = mock(FormsDao.class);
        when(formsDao.isFormEncrypted(anyString(), anyString())).thenReturn(false);

        RobolectricHelpers.overrideAppDependencyModule(new AppDependencyModule(instancesDao, formsDao));

        submissionManager = RobolectricHelpers.getApplicationComponent().smsSubmissionManagerContract();
        smsManager = RobolectricHelpers.getApplicationComponent().smsManager();
        eventBus = RobolectricHelpers.getApplicationComponent().rxEventBus();
        smsService = new StubSmsService(smsManager, submissionManager, instancesDao, RuntimeEnvironment.application, eventBus, formsDao);

        setDefaultGateway();
    }

    @Test
    public void testSubmitForm() throws IOException {

        File dir = RuntimeEnvironment.application.getFilesDir();

        String instancePath = dir + "/test_instance";
        File file = new File(getSmsInstancePath(instancePath));

        String form = "+FN John +LN Doe +CTY London +G Male +ROLE Contractor +PIC image_243.png";

        writeFormToFile(form, file);

        FormInfo info = new FormInfo(instancePath, "", "");

        assertTrue(smsService.submitForm(SampleData.TEST_INSTANCE_ID, info, "Sample Form"));

        ShadowSmsManager.TextMultipartParams params = shadowOf(smsManager).getLastSentMultipartTextMessageParams();

        assertEquals(params.getDestinationAddress(), GATEWAY);
        assertNotNull(params.getSentIntents());
        assertNull(params.getDeliveryIntents());

        SmsSubmission result = submissionManager.getSubmissionModel(SampleData.TEST_INSTANCE_ID);

        //Check if all messages are currently being sent.
        assertEquals(params.getParts().size(), result.getMessages().size());

    }

    private void writeFormToFile(String form, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(form);
        }
    }

    class StubSmsService extends SmsService {

        StubSmsService(SmsManager smsManager, SmsSubmissionManagerContract smsSubmissionManager, InstancesDao instancesDao, Context context, RxEventBus rxEventBus, FormsDao formsDao) {
            super(smsManager, smsSubmissionManager, instancesDao, context, rxEventBus, formsDao);
        }

        /**
         * Overrides the default functionality by executing the SmsSender operation
         * that normally gets run when the job is started. This allows the operations of the job
         * to take place since the Job can't be run in test environments.
         *
         * @param instanceId from instanceDao
         */
        @Override
        protected void startSendMessagesJob(String instanceId) {
            new SmsSender(RuntimeEnvironment.application, instanceId).send();
        }
    }

    private static class AppDependencyModule extends com.tehamalab.collect.android.injection.config.AppDependencyModule {

        private final InstancesDao instancesDao;
        private final FormsDao formsDao;

        AppDependencyModule(InstancesDao instancesDao, FormsDao formsDao) {
            this.instancesDao = instancesDao;
            this.formsDao = formsDao;
        }

        @Override
        public InstancesDao provideInstancesDao() {
            return instancesDao;
        }

        @Override
        public FormsDao provideFormsDao() {
            return formsDao;
        }
    }
}
