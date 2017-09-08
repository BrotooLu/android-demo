package com.bro2.b2lib.timing;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bro2 on 2017/9/6
 */

public class AverageClerk implements ITimingClerk {

    private static class Record {
        long lastStart;
        long duration;
        int count;
        boolean enterFlag;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Count: ");
            builder.append(count);
            builder.append(" Total: ");
            builder.append(duration);
            builder.append(" Average: ");
            builder.append(duration / (double) count);
            return builder.toString();
        }
    }

    private static final Comparator<Map.Entry<Marker, Record>> sComparator =
            new Comparator<Map.Entry<Marker, Record>>() {
                @Override
                public int compare(Map.Entry<Marker, Record> o1, Map.Entry<Marker, Record> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            };

    private final Map<Marker, Record> mStatMap = new HashMap<>();

    @Override
    public void enter(Marker marker) {
        if (marker == null) {
            return;
        }

        Record record = mStatMap.get(marker);
        if (record == null) {
            record = new Record();
            mStatMap.put(marker, record);
        }

        record.lastStart = SystemClock.elapsedRealtime();
        record.enterFlag = true;
    }

    @Override
    public void leave(Marker marker) {
        if (marker == null) {
            return;
        }

        Record record = mStatMap.get(marker);
        if (record == null || !record.enterFlag) {
            return;
        }

        record.enterFlag = false;
        record.count++;
        record.duration += SystemClock.elapsedRealtime() - record.lastStart;
    }

    @Override
    public String dump() {
        StringBuilder dump = new StringBuilder();
        ArrayList<Map.Entry<Marker, Record>> entries = new ArrayList<>(mStatMap.entrySet());
        Collections.sort(entries, sComparator);
        for (Map.Entry<Marker, Record> entry : entries) {
            Marker key = entry.getKey();
            Record val = entry.getValue();
            dump.append(key);
            dump.append("\n\t");
            dump.append(val);
            dump.append("\n");
        }

        return dump.toString();
    }

    @Override
    public void clear() {
        mStatMap.clear();
    }
}
