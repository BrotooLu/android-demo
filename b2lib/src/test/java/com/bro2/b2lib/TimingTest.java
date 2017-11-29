package com.bro2.b2lib;

import com.bro2.timing.AverageClerk;
import com.bro2.timing.Marker;
import com.bro2.timing.Timing;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Bro2 on 2017/9/8
 */

public class TimingTest {

    @Before
    public void setUp() {
        Timing.prepareClerk(new AverageClerk());
    }

    @Test
    public void test() {
        ArrayList<Marker> markers = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            markers.add(new Marker("step" + i, i));
        }

        for (int i = 0; i < 50; ++i) {
            for (Marker marker : markers) {
                Timing.enter(marker);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Timing.leave(marker);
                }
            }
        }

        System.out.println(Timing.dump());
    }
}
