package ognl.extended;

import ognl.OgnlContext;

public interface ObjectConstructor {
  Object createObject(Class<?> cls, Class<?> componentType, MapNode node) throws InstantiationException, IllegalAccessException;
  Object processObject(OgnlContext context, Object root, OgnlPropertyDescriptor propertyDescriptor, Object propertyObject, MapNode node);
}
