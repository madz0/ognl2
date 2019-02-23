/*
 * Decompiled with CFR 0.139.
 */
package ognl.internal.extended;

public class ArraySourceContainer {
    private String setterName;
    private Object target;
    private Object source;
    private Integer index;

    public String getSetterName() {
        return this.setterName;
    }

    public void setSetterName(String setterName) {
        this.setterName = setterName;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getSourece() {
        return this.source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getIndex() {
        return this.index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}

