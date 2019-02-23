/*
 * Decompiled with CFR 0.139.
 */
package ognl.extended;

import ognl.NoSuchPropertyException;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;
import java.util.Iterator;
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
        if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
            this.shiftGenericParameters(ognlContext, level);
            return target;
        }
        throw new NoSuchPropertyException(target, name);
    }
}

