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
import java.util.Enumeration;
import java.util.Map;

public class ExEnumerationPropertyAccessor
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
        Enumeration e = (Enumeration)target;
        if (name instanceof String) {
            if ("next".equals(name) || "nextElement".equals(name)) {
                result = e.nextElement();
            } else if ("hasNext".equals(name) || "hasMoreElements".equals(name)) {
                result = e.hasMoreElements() ? Boolean.TRUE : Boolean.FALSE;
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
        throw new IllegalArgumentException("can't set property " + name + " on Enumeration");
    }
}

