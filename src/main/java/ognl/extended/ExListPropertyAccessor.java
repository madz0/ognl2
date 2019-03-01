/*
 * Decompiled with CFR 0.139.
 */
package ognl.extended;

import ognl.*;

import java.util.*;

public class ExListPropertyAccessor
extends ExObjectPropertyAccessor
implements PropertyAccessor {
    private PropertyAccessor propertyAccessor = new ListPropertyAccessor();
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
      OgnlContext ognlContext = (OgnlContext) context;
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
            this.shiftGenericParameters(ognlContext, level);
            return target;
        }
        List list = target instanceof List? (List) target:new ArrayList((Set) target);
        if (name instanceof String) {
            Object result = null;
            if ("size".equals(name)) {
                result = list.size();
            } else if ("iterator".equals(name)) {
                result = list.iterator();
            } else if ("isEmpty".equals(name) || "empty".equals(name)) {
                result = list.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
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
        int index = -1;
        boolean isChained = this.isSetChain(context);
        boolean isExpanded = this.isExpanded(context);
        boolean isnullInited = this.isNullInited(context);
        if (name instanceof DynamicSubscript) {
            int len = list.size();
            switch (((DynamicSubscript)name).getFlag()) {
                case 0: {
                    if (!isChained) {
                        return len > 0 ? list.get(0) : null;
                    }
                    index = 0;
                }
                case 1: {
                    if (!isChained) {
                        return len > 0 ? list.get(len / 2) : null;
                    }
                    index = len / 2;
                }
                case 2: {
                    if (!isChained) {
                        return len > 0 ? list.get(len - 1) : null;
                    }
                    index = len - 1;
                    if (index < 0 && isExpanded) {
                        index = 0;
                    }
                }
                case 3: {
                    return new ArrayList(list);
                }
            }
        }
        if (name instanceof Number) {
            index = ((Number)name).intValue();
        }
        if (index > -1) {
            if (!this.isSetChain(context)) {
                return list.get(index);
            }
            Object value = null;
            if (list.size() > index) {
                value = list.get(index);
                Object clsObj = null;
                if (isnullInited) {
                    clsObj = this.getParameterizedType(ognlContext, level, 0);
                }
                if (value != null && value.getClass().isArray()) {
                    this.keepArraySource(ognlContext, target, index, level);
                }
                if (value != null || !isnullInited) {
                    value = processObject(ognlContext, target, null, name, value);
                    list.set(index, value);
                    return value;
                }
                if (clsObj == null) {
                    if (this.isUnknownInited(context)) {
                        value = processObject(ognlContext, target, null, name, new Object());
                        list.set(index, value);
                        updateTargetIfSet(target, list);
                        return value;
                    }
                    throw new OgnlException("Could not determine type of the List");
                }
                Class cls = (Class)clsObj;
                try {
                    value = this.createProperObject(ognlContext, cls, cls.getComponentType());
                    value = processObject(ognlContext, target, null, name, value);
                    if (cls.isArray()) {
                        this.keepArraySource(ognlContext, target, index, level);
                    }
                    list.set(index, value);
                    updateTargetIfSet(target, list);
                    return value;
                }
                catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    return processObject(ognlContext, target, null, name, null);
                }
            }
            if (!isExpanded) {
                return processObject(ognlContext, target, null, name, null);
            }
            for (int i = list.size(); i <= index; ++i) {
                list.add(null);
            }
            if (!isnullInited) {
                return processObject(ognlContext, target, null, name, null);
            }
            Object clsObj = this.getParameterizedType(ognlContext, level, 0);
            if (clsObj == null) {
                if (this.isUnknownInited(context)) {
                    value = processObject(ognlContext, target, null, name, new Object());
                    list.set(index, value);
                    updateTargetIfSet(target, list);
                    return value;
                }
                throw new OgnlException("Could not determine type of the List");
            }
            Class cls = (Class)clsObj;
            try {
                value = this.createProperObject(ognlContext, cls, cls.getComponentType());
                value = processObject(ognlContext, target, null, name, value);
                if (cls.isArray()) {
                    this.keepArraySource(ognlContext, target, index, level);
                }
                list.set(index, value);
                updateTargetIfSet(target, list);
                return value;
            }
            catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return processObject(ognlContext, target, null, name, null);
            }
        }
        return processObject(ognlContext, target, null, name, null);
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        if (name instanceof String && !((String)name).contains("$")) {
            super.setProperty(context, target, name, value);
            return;
        }
        this.incIndex(context);
        List list = (List)target;
        boolean isExpanded = this.isExpanded(context);
        if (name instanceof Number) {
            int index = ((Number)name).intValue();
            if (list.size() > index) {
                list.set(index, value);
                return;
            }
            if (!isExpanded) {
                return;
            }
            for (int i = list.size(); i <= index; ++i) {
                list.add(null);
            }
            list.set(index, value);
            return;
        }
        if (name instanceof DynamicSubscript) {
            int len = list.size();
            switch (((DynamicSubscript)name).getFlag()) {
                case 0: {
                    if (len > 0) {
                        list.set(0, value);
                        return;
                    }
                    if (!isExpanded) {
                        return;
                    }
                    list.add(value);
                    return;
                }
                case 1: {
                    if (len > 0) {
                        list.set(len / 2, value);
                        return;
                    }
                    if (!isExpanded) {
                        return;
                    }
                    list.add(value);
                    return;
                }
                case 2: {
                    if (len > 0) {
                        list.set(len - 1, value);
                        return;
                    }
                    if (!isExpanded) {
                        return;
                    }
                    list.add(value);
                    return;
                }
                case 3: {
                    if (!(value instanceof Collection)) {
                        throw new OgnlException("Value must be a collection");
                    }
                    list.clear();
                    list.addAll((Collection)value);
                    return;
                }
            }
            return;
        }
        throw new NoSuchPropertyException(target, name);
    }

    @Override
    public int getGenericArgumentsCount() {
        return 1;
    }

    private void updateTargetIfSet(Object target, List list) {
        if(target instanceof Set) {
            Set set = (Set) target;
            set.clear();
            set.addAll(list);
        }
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return propertyAccessor.getSourceAccessor(context, target, index);
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return propertyAccessor.getSourceSetter(context, target, index);
    }
}

