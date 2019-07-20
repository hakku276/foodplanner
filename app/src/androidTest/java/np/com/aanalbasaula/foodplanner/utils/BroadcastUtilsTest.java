package np.com.aanalbasaula.foodplanner.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BroadcastUtilsTest {

    private static final String TEST_ACTION = "np.com.aanalbasaula.foodplanner.TEST_ACTION";

    /**
     * Test that the broadcast receiver is triggered when a broadcast is made
     * and a receiver is registered.
     */
    @Test
    public void testBroadcastReceiver() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();
        TestReceiver receiver = new TestReceiver();

        // register broadcast
        BroadcastUtils.registerLocalBroadcastListener(context, receiver, TEST_ACTION);

        // trigger broadcast
        BroadcastUtils.sendLocalBroadcast(context, TEST_ACTION);

        // wait for some time
        Thread.sleep(1000);

        // check if the receiver was triggered
        Assert.assertTrue(receiver.triggered);
        Assert.assertEquals(TEST_ACTION, receiver.triggerAction);
    }

    /**
     * Test that the broadcast received is unregistered successfully
     */
    @Test
    public void testUnregisterBroadcastReceiver() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();
        TestReceiver receiver = new TestReceiver();

        // register broadcast
        BroadcastUtils.registerLocalBroadcastListener(context, receiver, TEST_ACTION);

        // trigger broadcast
        BroadcastUtils.sendLocalBroadcast(context, TEST_ACTION);

        // wait for some time
        Thread.sleep(1000);

        // check if the receiver was triggered
        Assert.assertTrue(receiver.triggered);
        Assert.assertEquals(TEST_ACTION, receiver.triggerAction);

        // unregister the receiver
        BroadcastUtils.unregisterLocalBroadcastListener(context, receiver);
        receiver.reset();

        // make broadcast again
        BroadcastUtils.sendLocalBroadcast(context, TEST_ACTION);

        // wait for some time
        Thread.sleep(1000);

        // verify no broadcast received
        Assert.assertFalse(receiver.triggered);

    }

    /**
     * Test Broadcast receiver
     */
    class TestReceiver extends BroadcastReceiver {

        boolean triggered = false;
        String triggerAction;

        @Override
        public void onReceive(Context context, Intent intent) {
            triggered = true;
            triggerAction = intent.getAction();
        }

        void reset() {
            triggered = false;
            triggerAction = null;
        }
    };

}
