package com.bro2.b2lib;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bro2 on 2017/9/6
 */

public class SortTest {

    private int[] arr;
    private int[] sorted;

    @Before

    public void setUp() {
        arr = new int[]{10, 5, 2, 1, 4, 4, 0, 0, 6, 7, 0, 77};
//        arr = new int[]{10, 0, 0, 10};

        sorted = new int[arr.length];
        System.arraycopy(arr, 0, sorted, 0, arr.length);
        Arrays.sort(sorted);

        System.out.println("arr: " + Arrays.toString(arr));
        System.out.println("sorted: " + Arrays.toString(sorted));
    }

    @After
    public void verify() {
        System.out.println("after arr: " + Arrays.toString(arr));
        for (int i = 0; i < arr.length; ++i) {
            assertEquals(arr[i], sorted[i]);
        }
    }

    private void swap(int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    @Test
    public void bubble() {
        for (int i = 0; i < arr.length - 1; ++i) {
            for (int j = 0; j < arr.length - i - 1; ++j) {
                if (arr[j] > arr[j + 1]) {
                    swap(j, j + 1);
                }
            }
        }
    }

    @Test
    public void insert() {
        for (int i = 1; i < arr.length; ++i) {
            int tmp = arr[i];
            int j = i;
            for (; j > 0 && arr[j - 1] > tmp; --j) {
                arr[j] = arr[j - 1];
            }

            arr[j] = tmp;
        }
    }

    @Test
    public void select() {

        for (int i = 0; i < arr.length; ++i) {
            int m = i;
            for (int j = i + 1; j < arr.length; ++j) {
                if (arr[j] < arr[m]) {
                    m = j;
                }
            }

            if (m != i) {
                swap(m, i);
            }
        }

    }

    private void quickSort(int start, int end) {
        if (start >= end) {
            return;
        }

        int left = start;
        int right = end;

        int value = arr[left];

        while (left < right) {
            while (left < right && arr[right] >= value)
                --right;

            if (left < right) {
                arr[left++] = arr[right];
            }

            while (left < right && arr[left] <= value)
                ++left;

            if (left < right) {
                arr[right--] = arr[left];
            }

        }

        arr[left] = value;

        quickSort(start, left - 1);
        quickSort(left + 1, end);
    }


    @Test
    public void quick() {
        quickSort(0, arr.length - 1);
    }

}
