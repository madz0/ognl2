/*
 * Decompiled with CFR 0.139.
 */
package ognl.internal.extended;

public class MutableInt {
    int value = 0;

    public MutableInt() {
    }

    public MutableInt(int start) {
        this.value = start;
    }

    public void inc() {
        ++this.value;
    }

    public int incGet() {
        ++this.value;
        return this.value;
    }

    public void dec() {
        --this.value;
    }

    public int decGet() {
        --this.value;
        return this.value;
    }

    public int get() {
        return this.value;
    }
}

