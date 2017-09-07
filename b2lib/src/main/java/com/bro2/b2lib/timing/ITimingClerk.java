package com.bro2.b2lib.timing;

/**
 * Created by Bro2 on 2017/9/6
 */

public interface ITimingClerk {

    void enter(Marker marker);

    void leave(Marker marker);

    String dump();

    void clear();

}
