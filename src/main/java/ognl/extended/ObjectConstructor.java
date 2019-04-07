package ognl.extended;

import ognl.OgnlContext;

import java.lang.reflect.InvocationTargetException;

public interface ObjectConstructor {
  Object createObject(Class<?> cls, Class<?> componentType, MapNode node) throws InstantiationException, IllegalAccessException, InvocationTargetException;
  Object processObjectForGet(OgnlContext context, Object root, OgnlPropertyDescriptor propertyDescriptor, Object propertyObject, MapNode node);
  Object processObjectForSet(OgnlContext context, Object root, OgnlPropertyDescriptor propertyDescriptor, Object propertyObject, MapNode node) throws PropertySetIgnoreException;
}
