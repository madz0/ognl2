/*
 * Decompiled with CFR 0.139.
 */
package com.github.madz0.ognl2.extended;

import com.github.madz0.ognl2.OgnlException;
import com.github.madz0.ognl2.PropertyAccessor;
import com.github.madz0.ognl2.OgnlContext;
import com.github.madz0.ognl2.OgnlException;
import com.github.madz0.ognl2.PropertyAccessor;
import com.github.madz0.ognl2.extended.ExObjectPropertyAccessor;
import java.util.Iterator;
import java.util.Map;

public class ExIteratorPropertyAccessor
extends ExObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        Object result;
        OgnlContext ognlContext = (OgnlContext) context;
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
            this.shiftGenericParameters(ognlContext, level);
            return target;
        }
        Iterator iterator = (Iterator)target;
        if (name instanceof String) {
            if ("next".equals(name)) {
                result = iterator.next();
            } else if ("hasNext".equals(name)) {
                result = iterator.hasNext() ? Boolean.TRUE : Boolean.FALSE;
            } else {
                if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
                    this.shiftGenericParameters(ognlContext, level);
                    return target;
                }
                this.decIndex(context);
                result = super.getProperty(context, target, name);
            }
        } else {
            if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
                this.shiftGenericParameters(ognlContext, level);
                return target;
            }
            this.decIndex(context);
            result = super.getProperty(context, target, name);
        }
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        throw new IllegalArgumentException("can't set property " + name + " on Iterator");
    }
}

