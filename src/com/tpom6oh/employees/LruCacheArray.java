package com.tpom6oh.employees;

import android.util.SparseIntArray;

public class LruCacheArray {

    private SparseIntArray data;
    private int[] keyTracker;
    private int index;
    private int capacity;

    public LruCacheArray(int capacity) {
        data = new SparseIntArray(capacity);
        this.capacity = capacity;
        keyTracker = new int[capacity];

        for (int i = 0; i < capacity; i++) {
            keyTracker[i] = -1;
        }
    }

    public void put(int key, int value) {
        if (index > capacity) {
            index = 0;
        }
        data.delete(keyTracker[index]);
        keyTracker[index] = key;
        data.put(key, value);
    }

    public int get(int key, int valueIfKeyNotFound) {
        return data.get(key, valueIfKeyNotFound);
    }
}
