package com.bro2.b2lib;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.bro2.b2lib.util.DeviceInfo;
import com.bro2.b2lib.util.UriUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.bro2.b2lib.test", appContext.getPackageName());
    }

    @Test
    public void testDeviceUtil() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Point out = DeviceInfo.getScreenRealSize(appContext, null);
        assertEquals(1080, out.x);
        assertEquals(1920, out.y);

        assertEquals(-1, DeviceInfo.getRemainingBattery(appContext));
    }

    @Test
    public void testGetBundle() {
        compareBundle("http://www.a.com/web?imagetitleurl=imageurl&url=http://www.host.test%3Fkey1%3Dval1%26key2%3Dval2&ttta=222");
        compareBundle("http://www.b.com/web?key");
        compareBundle("http://www.c.com/web?&key1=val1");
        compareBundle("http://www.d.com/web?key1=&key2=val2");
    }

    private void compareBundle(String url) {
        Uri data = Uri.parse(url);
        System.out.println(UriUtil.convertQueryParameterAsBundle(data));
    }
}
