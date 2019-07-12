package com.github.madz0.ognl2.extended;

import com.github.madz0.ognl2.ObjectIndexedPropertyDescriptor;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class OgnlPropertyDescriptor {
    private PropertyDescriptor descriptor;
    private Object name;
    private Map<Class<? extends Annotation>, Annotation> allAnnotationsMap;

    public OgnlPropertyDescriptor(final Field field, final PropertyDescriptor descriptor) {
        this.descriptor = descriptor;
        this.name = descriptor.getName();
        allAnnotationsMap = new HashMap<>();
        Annotation[] annotations;
        if (field != null) {
            annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                allAnnotationsMap.put(annotation.annotationType(), annotation);
            }
        }
        if (descriptor.getReadMethod() != null) {
            annotations = descriptor.getReadMethod().getAnnotations();
            for (Annotation annotation : annotations) {
                allAnnotationsMap.put(annotation.annotationType(), annotation);
            }
        }
    }

    public OgnlPropertyDescriptor(Object name) {
        this.name = name;
    }

    public Method getReadMethod() {
        return descriptor == null ? null : descriptor.getReadMethod();
    }

    public Method getWriteMethod() {
        return descriptor == null ? null : descriptor.getWriteMethod();
    }

    public <T extends Annotation> T getAnnotation(Class<T> cls) {
        return allAnnotationsMap == null ? null : (T) allAnnotationsMap.get(cls);
    }

//    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
//        fields.addAll(Arrays.asList(type.getDeclaredFields()));
//        if (type.getSuperclass() != null) {
//            getAllFields(fields, type.getSuperclass());
//        }
//        return fields;
//    }

    public Method getIndexedReadMethod() {
        if (descriptor instanceof IndexedPropertyDescriptor) {
            return ((IndexedPropertyDescriptor) descriptor).getIndexedReadMethod();
        } else {
                return null;
        }
    }

    public Method getIndexedWriteMethod() {
        if (descriptor instanceof IndexedPropertyDescriptor) {
            return ((IndexedPropertyDescriptor) descriptor).getIndexedWriteMethod();
        } else {
            return null;
        }
    }

    public Boolean isIndexedPropertyDescriptor() {
        return descriptor instanceof IndexedPropertyDescriptor;
    }

    public String getPropertyName() {
        return String.valueOf(name);
    }

    public Boolean isPropertyDescriptor() {
        return descriptor != null;
    }

    @Override
    public String toString() {
        return getPropertyName();
    }
}
