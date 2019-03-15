/*
 * Decompiled with CFR 0.139.
 */
package ognl.extended;

import ognl.NoSuchPropertyException;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExSetPropertyAccessor
extends ExObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
      OgnlContext ognlContext = (OgnlContext) context;  
      int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
            this.shiftGenericParameters(ognlContext, level);
            return target;
        }
        Set set = (Set)target;
        if (name instanceof String) {
            Object result;
            if ("size".equals(name)) {
                result = set.size();
            } else if ("iterator".equals(name)) {
                result = set.iterator();
            } else if ("isEmpty".equals(name)) {
                result = set.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
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
        else if (name instanceof Number) {
            this.decIndex(context);
            List setArr = new ArrayList(set);
            ExListPropertyAccessor exListPropertyAccessor = new ExListPropertyAccessor();
            Object obj = exListPropertyAccessor.getProperty(context, setArr, name);
            set.clear();
            set.addAll(setArr);
            return obj;
        }
        if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
            this.shiftGenericParameters(ognlContext, level);
            return target;
        }
        throw new NoSuchPropertyException(target, name);
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        int level = this.incIndex(context);
        Set set = (Set) target;
        if (name instanceof Number) {
            this.decIndex(context);
            ExListPropertyAccessor exListPropertyAccessor = new ExListPropertyAccessor();
            List setArr = new ArrayList(set);
            exListPropertyAccessor.setProperty(context, setArr, name, value);
            set.clear();
            set.addAll(setArr);
        }
        else {
            set.add(value);
        }
    }
}

