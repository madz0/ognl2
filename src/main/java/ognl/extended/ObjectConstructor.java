package ognl.extended;

import ognl.Ognl;
import ognl.OgnlContext;

import java.beans.PropertyDescriptor;
import java.util.Map;

public interface ObjectConstructor {
  Object createObject(Class<?> cls, Class<?> componentType) throws InstantiationException, IllegalAccessException;
  Object processObject(OgnlContext context, Object root, Object propertyDescriptor, Object propertyObject, Ognl.MyNode node);
}
