package com.bro2.util;

/**
 * Created by Brotoo on 2018/6/23.
 */
public final class BitOperator {

    public static boolean getBit(int value, int mask) {
        return (value & mask) != 0;
    }

    public static int setBit(int value, int mask, boolean bit) {
        if (bit) {
            return value | mask;
        } else {
            return value & ~mask;
        }
    }

}
