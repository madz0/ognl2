package ognl.extended;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

public class DefaultObjectConstructor implements ObjectConstructor {

    @Override
    public Object createObject(Class<?> cls, Class<?> componentType)
            throws InstantiationException, IllegalAccessException {
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
            return new HashSet();
        }
        if (cls.isArray()) {
            return Array.newInstance(componentType, 1);
        }
        if (OgnlRuntime.isPrimitiveOrWrapper(cls)) {
            return OgnlRuntime.getPrimitivesDefult(cls);
        }
        return cls.newInstance();
    }

    @Override
    public Object processObject(OgnlContext context, Object root, Object propertyDescriptor,
                                Object propertyObject, MapNode node) {
        context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        if(node.isCollection()) {
            context.extend((ParameterizedType) ((PropertyDescriptor)propertyDescriptor).getReadMethod().getGenericReturnType());
        }
        else {
            context.extend();
        }
        try {
            Ognl.getValue(node, context, propertyObject);
        } catch (OgnlException e) {
            e.printStackTrace();
        }
        return propertyObject;
    }
}
