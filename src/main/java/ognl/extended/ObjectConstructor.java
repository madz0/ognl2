package ognl.extended;

import ognl.OgnlContext;

public interface ObjectConstructor {
  Object createObject(Class<?> cls, Class<?> componentType) throws InstantiationException, IllegalAccessException;
  Object processObject(OgnlContext context, Object root, Object propertyDescriptor, Object propertyObject, MapNode node);
}
