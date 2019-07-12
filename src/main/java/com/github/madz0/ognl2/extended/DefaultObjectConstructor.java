package com.github.madz0.ognl2.extended;

import com.github.madz0.ognl2.OgnlException;
import com.github.madz0.ognl2.Ognl;
import com.github.madz0.ognl2.OgnlContext;
import com.github.madz0.ognl2.OgnlException;
import com.github.madz0.ognl2.OgnlRuntime;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultObjectConstructor implements ObjectConstructor {

    @Override
    public Object createObject(Class<?> cls, Class<?> componentType, MapNode node)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (List.class.isAssignableFrom(cls)) {
            if (LinkedList.class == cls) {
                return new LinkedList();
            } else if (ArrayList.class == cls) {
                return new ArrayList();
            } else if (List.class == cls) {
                return new ArrayList();
            }
        }
        if (Map.class.isAssignableFrom(cls)) {
            if (LinkedHashMap.class == cls) {
                return new LinkedHashMap();
            } else if (TreeMap.class == cls) {
                return new TreeMap();
            } else if (HashMap.class == cls) {
                return new HashMap<>();
            } else if (Map.class == cls) {
                return new LinkedHashMap();
            }
        }
        if (ConcurrentMap.class.isAssignableFrom(cls)) {
            return new ConcurrentHashMap();
        }
        if (Set.class.isAssignableFrom(cls)) {
            if (LinkedHashSet.class == cls) {
                return new LinkedHashSet();
            } else if (HashSet.class == cls) {
                return new HashSet();
            } else if (TreeSet.class == cls) {
                return new TreeSet();
            } else if (Set.class == cls) {
                return new LinkedHashSet();
            }
        }
        if (cls.isArray()) {
            return Array.newInstance(componentType, 1);
        }
        if (OgnlRuntime.isPrimitiveOrWrapper(cls)) {
            return OgnlRuntime.getPrimitivesDefult(cls);
        }
        return OgnlRuntime.getDefaultConstructor(cls).newInstance();
    }

    @Override
    public Object processObjectForGet(OgnlContext context, Object root, OgnlPropertyDescriptor propertyDescriptor,
                                      Object propertyObject, MapNode node) {
        if (node != null) {
            context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
            if (node.isCollection() && propertyDescriptor.isPropertyDescriptor()) {
                context.extend(findParameterizedType(propertyDescriptor.getReadMethod().getGenericReturnType()));
            } else {
                context.extend();
            }
            context.setObjectConstructor(this);
            try {
                Ognl.getValue(node, context, propertyObject);
            } catch (OgnlException e) {
                e.printStackTrace();
            }
        }
        return propertyObject;
    }

    @Override
    public Object processObjectForSet(OgnlContext context, Object root, OgnlPropertyDescriptor propertyDescriptor, Object propertyObject, MapNode node) throws PropertySetIgnoreException {
        return propertyObject;
    }

    protected static ParameterizedType findParameterizedType(Type type) {
        while (type != null && !(type instanceof ParameterizedType)) {
            type = ((Class) type).getGenericSuperclass();
        }
        return (ParameterizedType) type;
    }
}
