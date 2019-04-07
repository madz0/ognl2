package ognl.extended;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultObjectConstructor implements ObjectConstructor {

    @Override
    public Object createObject(Class<?> cls, Class<?> componentType, MapNode node)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (List.class.isAssignableFrom(cls)) {
            if (LinkedList.class.isAssignableFrom(cls)) {
                return new LinkedList();
            }
            return new ArrayList();
        }
        if (Map.class.isAssignableFrom(cls)) {
            if (LinkedHashMap.class.isAssignableFrom(cls)) {
                return new LinkedHashMap();
            }
            if (TreeMap.class.isAssignableFrom(cls)) {
                return new TreeMap();
            }
            return new HashMap();
        }
        if (ConcurrentMap.class.isAssignableFrom(cls)) {
            return new ConcurrentHashMap();
        }
        if (Set.class.isAssignableFrom(cls)) {
            if (LinkedHashSet.class.isAssignableFrom(cls)) {
                return new LinkedHashSet();
            }
            else if(HashSet.class.isAssignableFrom(cls)) {
                return new HashSet();
            }
            else if(TreeSet.class.isAssignableFrom(cls)) {
                return new TreeSet();
            }
            return new LinkedHashSet();
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
                context.extend((ParameterizedType) propertyDescriptor.getReadMethod().getGenericReturnType());
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
}
