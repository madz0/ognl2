/*
 * Decompiled with CFR 0.139.
 */
package com.github.madz0.ognl2.extended;

import com.github.madz0.ognl2.*;
import com.github.madz0.ognl2.internal.extended.ArraySourceContainer;

import java.beans.IntrospectionException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.madz0.ognl2.extended.Config.ARRAR_SOURCE_PREFIX_KEY;
import static com.github.madz0.ognl2.extended.Config.EXPANDED_ARRAY_KEY;

public class ExArrayPropertyAccessor
        extends ExObjectPropertyAccessor
        implements PropertyAccessor {
    private PropertyAccessor arrayPropertyAccessor = new ArrayPropertyAccessor();

    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        OgnlContext ognlContext = (OgnlContext) context;
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && ognlContext.getRoot().getClass().isArray()) {
            return target;
        }
        Object result = null;
        if (name instanceof String) {
            if ("length".equals(name)) {
                result = Array.getLength(target);
            } else if (level == 1 && this.isFirstUnknownIgnored(context) && ognlContext.getRoot().getClass().isArray()) {
                result = target;
            } else {
                this.decIndex(context);
                result = super.getProperty(context, target, name);
            }
        } else {
            Object index = name;
            if (index instanceof DynamicSubscript) {
                int len = Array.getLength(target);
                switch (((DynamicSubscript) index).getFlag()) {
                    case 3: {
                        result = Array.newInstance(target.getClass().getComponentType(), len);
                        System.arraycopy(target, 0, result, 0, len);
                        break;
                    }
                    case 0: {
                        index = len > 0 ? 0 : -1;
                        break;
                    }
                    case 1: {
                        index = len > 0 ? len / 2 : -1;
                        break;
                    }
                    case 2: {
                        index = len > 0 ? len - 1 : -1;
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } else if (index instanceof Number && ((Number) index).intValue() < 0) {
                throw new NoSuchPropertyException(target, index);
            }
            if (result == null) {
                if (index instanceof Number) {
                    int i = ((Number) index).intValue();
                    if (!this.isSetChain(context)) {
                        return i >= 0 ? Array.get(target, i) : null;
                    }
                    i = i < 0 ? 0 : i;
                    int len = Array.getLength(target);
                    boolean nullInited = this.isNullInited(context);
                    if (len > i) {
                        Object value = null;
                        value = Array.get(target, i);
                        this.setExpandedArray(ognlContext, target, i, level, false);
                        if (value != null || !nullInited) {
                            return value;
                        }
                        Class<?> cls = target.getClass().getComponentType();
                        if (cls == null) {
                            if (this.isUnknownInited(context)) {
                                value = new Object();
                                Array.set(target, i, value);
                                return value;
                            }
                            throw new OgnlException("Could not determine component type of the Array");
                        }
                        try {
                            value = this.createProperObject(ognlContext, cls, cls.getComponentType());
                            Array.set(target, i, value);
                            return value;
                        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    if (!this.isExpanded(context)) {
                        return null;
                    }
                    Class<?> component_type = target.getClass().getComponentType();
                    if (component_type == null) {
                        if (this.isUnknownInited(context)) {
                            result = new Object();
                            Array.set(target, i, result);
                            Object new_array = Array.newInstance(component_type, i + 1);
                            System.arraycopy(target, 0, new_array, 0, len);
                            Array.set(new_array, i, result);
                            this.setExpandedArray(ognlContext, new_array, i, level, true);
                            return result;
                        }
                        throw new OgnlException("Could not determine component type of the Array");
                    }
                    Object new_array = Array.newInstance(component_type, i + 1);
                    System.arraycopy(target, 0, new_array, 0, len);
                    try {
                        result = this.createProperObject(ognlContext, component_type, component_type.getComponentType());
                        Array.set(new_array, i, result);
                        this.setExpandedArray(ognlContext, new_array, i, level, true);
                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                throw new NoSuchPropertyException(target, index);
            }
        }
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        OgnlContext ognlContext = (OgnlContext) context;
        int level = this.incIndex(context);
        boolean isNumber = name instanceof Number;
        if (isNumber || name instanceof DynamicSubscript) {
            TypeConverter converter = ognlContext.getTypeConverter();
            Object convertedValue = converter.convertValue(context, target, null, name.toString(), value, target.getClass().getComponentType());
            int len = Array.getLength(target);
            boolean isExpanded = this.isExpanded(context);
            if (isNumber) {
                int i = ((Number) name).intValue();
                if (len > i && i >= 0) {
                    Array.set(target, i, convertedValue);
                    return;
                }
                if (!isExpanded) {
                    return;
                }
                Class<?> component_type = target.getClass().getComponentType();
                Object[] array = (Object[]) target;
                Object[] new_array = (Object[]) Array.newInstance(component_type, i + 1);
                System.arraycopy(array, 0, new_array, 0, len);
                Array.set(new_array, i, value);
                this.setExpandedArray(ognlContext, new_array, i, level, true);
                return;
            }
            switch (((DynamicSubscript) name).getFlag()) {
                case 3: {
                    System.arraycopy(target, 0, convertedValue, 0, len);
                    return;
                }
            }
        } else if (name instanceof String) {
            this.decIndex(context);
            super.setProperty(context, target, name, value);
        } else {
            throw new NoSuchPropertyException(target, name);
        }
    }

    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return arrayPropertyAccessor.getSourceAccessor(context, target, index);
    }

    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return arrayPropertyAccessor.getSourceSetter(context, target, index);
    }

    private void setExpandedArray(OgnlContext context, Object array, int index, int level, boolean can_save) {
        StringBuffer key = new StringBuffer();
        key.append(ARRAR_SOURCE_PREFIX_KEY).append(level);
        ArraySourceContainer setterContainer = (ArraySourceContainer) context.get(key.toString());
        if (setterContainer != null) {
            Object source = setterContainer.getSourece();
            if (source != null) {
                if (source.getClass().isArray()) {
                    Array.set(source, (Integer) setterContainer.getIndex(), array);
                    setterContainer.setSource(array);
                    setterContainer.setIndex(index);
                } else if (source instanceof List) {
                    ((List) source).set((Integer) setterContainer.getIndex(), array);
                } else if (source instanceof Map) {
                    ((Map) source).put((String) setterContainer.getIndex(), array);
                } else if (source instanceof Set) {
                    ((Set) source).clear();
                    ((Set) source).add(array);
                }
                setterContainer.setIndex(index);
                setterContainer.setSource(array);
            } else {
                try {
                    if (can_save) {
                        OgnlRuntime.setMethodValue(context, setterContainer.getTarget(), setterContainer.getSetterName(), array, true);
                    }
                    setterContainer.setSource(array);
                    setterContainer.setIndex(index);
                } catch (OgnlException | IntrospectionException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            key = new StringBuffer();
            key.append(ARRAR_SOURCE_PREFIX_KEY).append(String.valueOf(level + 1));
            context.put(key.toString(), (Object) setterContainer);
        } else if (context.getRoot() != null && context.getRoot().getClass().isArray()) {
            context.put(EXPANDED_ARRAY_KEY, array);
            ArraySourceContainer a = new ArraySourceContainer();
            a.setSource(array);
            a.setIndex(index);
            context.put(key.toString(), a);
        }
    }
}

