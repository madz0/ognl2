/*
 * Decompiled with CFR 0.139.
 */
package com.github.madz0.ognl2.internal.extended;

public class ArrayUtil {
    public static int getDimensionCount(Object array) {
        if (array == null) {
            return 0;
        }
        int count = 0;
        Class<?> arrayClass = array.getClass();
        while (arrayClass.isArray()) {
            ++count;
            arrayClass = arrayClass.getComponentType();
        }
        return count;
    }

    public static int getDimensionCount(Class<?> array) {
        if (array == null) {
            return 0;
        }
        int count = 0;
        Class<?> arrayClass = array;
        while (arrayClass.isArray()) {
            ++count;
            arrayClass = arrayClass.getComponentType();
        }
        return count;
    }
}

