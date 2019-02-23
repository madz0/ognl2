package ognl;

import static ognl.OgnlRuntime.NotFound;
import static ognl.OgnlRuntime.SET_PREFIX;
import static ognl.OgnlRuntime.GET_PREFIX;
import static ognl.OgnlRuntime.IS_PREFIX;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.internal.ClassCache;
import ognl.internal.ClassCacheImpl;

public class OgnlCache {

  static final ClassCache _methodAccessors = new ClassCacheImpl();
  static final ClassCache _propertyAccessors = new ClassCacheImpl();
  static final ClassCache _elementsAccessors = new ClassCacheImpl();
  static final ClassCache _nullHandlers = new ClassCacheImpl();
  static final ClassCache _propertyDescriptorCache = new ClassCacheImpl();
  static final ClassCache _constructorCache = new ClassCacheImpl();
  static final ClassCache _staticMethodCache = new ClassCacheImpl();
  static final ClassCache _instanceMethodCache = new ClassCacheImpl();
  static final ClassCache _invokePermissionCache = new ClassCacheImpl();
  static final ClassCache _primitiveDefaults = new ClassCacheImpl();
  static final ClassCache _fieldCache = new ClassCacheImpl();
  static final List _superclasses = new ArrayList(); /* Used by fieldCache lookup */
  static ClassCacheInspector _cacheInspector;
  static final ClassCache[] _declaredMethods = new ClassCache[] { new ClassCacheImpl(), new ClassCacheImpl() };
  public static final List NotFoundList = new ArrayList();
  
  public OgnlCache() {
    _primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
    _primitiveDefaults.put(Boolean.class, Boolean.FALSE);
    _primitiveDefaults.put(Byte.TYPE, new Byte((byte) 0));
    _primitiveDefaults.put(Byte.class, new Byte((byte) 0));
    _primitiveDefaults.put(Short.TYPE, new Short((short) 0));
    _primitiveDefaults.put(Short.class, new Short((short) 0));
    _primitiveDefaults.put(Character.TYPE, new Character((char) 0));
    _primitiveDefaults.put(Integer.TYPE, new Integer(0));
    _primitiveDefaults.put(Long.TYPE, new Long(0L));
    _primitiveDefaults.put(Float.TYPE, new Float(0.0f));
    _primitiveDefaults.put(Double.TYPE, new Double(0.0));

    _primitiveDefaults.put(BigInteger.class, new BigInteger("0"));
    _primitiveDefaults.put(BigDecimal.class, new BigDecimal(0.0));
  }

  public void clearCache() {
    _propertyDescriptorCache.clear();
    _constructorCache.clear();
    _staticMethodCache.clear();
    _instanceMethodCache.clear();
    _invokePermissionCache.clear();
    _fieldCache.clear();
    _superclasses.clear();
    _declaredMethods[0].clear();
    _declaredMethods[1].clear();
  }

  public Map getPermission(Class mc) {
    synchronized (_invokePermissionCache) {
      Map permissions = (Map) _invokePermissionCache.get(mc);
      if (permissions == null) {
        _invokePermissionCache.put(mc, permissions = new HashMap(101));
      }
      return permissions;
    }
  }

  public Object getPrimitiveDefaultValue(Class forClass) {
    return _primitiveDefaults.get(forClass);
  }

  public List getConstructors(Class targetClass) {
    List result;
    if ((result = (List) _constructorCache.get(targetClass)) == null) {
      synchronized (_constructorCache) {
        if ((result = (List) _constructorCache.get(targetClass)) == null) {
          _constructorCache.put(targetClass, result = Arrays.asList(targetClass.getConstructors()));
        }
      }
    }
    return result;
  }

  private void collectMethods(Class c, Map result, boolean staticMethods) {
    final Method[] ma = c.getDeclaredMethods();
    for (int i = 0, icount = ma.length; i < icount; i++) {
      if (c.isInterface()) {
        if (OgnlRuntime.isDefaultMethod(ma[i]))
          addMethodToResult(result, ma[i]);
        continue;
      }

      // skip over synthetic methods
      if (!OgnlRuntime.isMethodCallable(ma[i]))
        continue;

      if (Modifier.isStatic(ma[i].getModifiers()) == staticMethods)
        addMethodToResult(result, ma[i]);
    }

    final Class superclass = c.getSuperclass();
    if (superclass != null)
      collectMethods(superclass, result, staticMethods);

    for (final Class iface : c.getInterfaces())
      collectMethods(iface, result, staticMethods);
  }

  private void addMethodToResult(Map result, Method method) {
    List ml = (List) result.get(method.getName());
    if (ml == null)
      result.put(method.getName(), ml = new ArrayList());
    ml.add(method);
  }

  public Map getMethods(Class targetClass, boolean staticMethods) {
    ClassCache cache = (staticMethods ? _staticMethodCache : _instanceMethodCache);
    Map result;

    if ((result = (Map) cache.get(targetClass)) == null) {
      synchronized (cache) {
        if ((result = (Map) cache.get(targetClass)) == null) {
          result = new HashMap(23);
          collectMethods(targetClass, result, staticMethods);
          cache.put(targetClass, result);
        }
      }
    }
    return result;
  }

  public Map getAllMethods(Class targetClass, boolean staticMethods) {
    ClassCache cache = (staticMethods ? _staticMethodCache : _instanceMethodCache);
    Map result;

    if ((result = (Map) cache.get(targetClass)) == null) {
      synchronized (cache) {
        if ((result = (Map) cache.get(targetClass)) == null) {
          result = new HashMap(23);

          for (Class c = targetClass; c != null; c = c.getSuperclass()) {
            Method[] ma = c.getMethods();

            for (int i = 0, icount = ma.length; i < icount; i++) {
              // skip over synthetic methods

              if (!OgnlRuntime.isMethodCallable(ma[i]))
                continue;

              if (Modifier.isStatic(ma[i].getModifiers()) == staticMethods) {
                List ml = (List) result.get(ma[i].getName());

                if (ml == null)
                  result.put(ma[i].getName(), ml = new ArrayList());

                ml.add(ma[i]);
              }
            }
          }
          cache.put(targetClass, result);
        }
      }
    }
    return result;
  }

  public Map getFields(Class targetClass) {
    Map result;

    if ((result = (Map) _fieldCache.get(targetClass)) == null) {
      synchronized (_fieldCache) {
        if ((result = (Map) _fieldCache.get(targetClass)) == null) {
          Field fa[];

          result = new HashMap(23);
          fa = targetClass.getDeclaredFields();
          for (int i = 0; i < fa.length; i++) {
            result.put(fa[i].getName(), fa[i]);
          }
          _fieldCache.put(targetClass, result);
        }
      }
    }
    return result;
  }

  public Field getField(Class inClass, String name) {
    Field result = null;

    Object o = getFields(inClass).get(name);
    if (o == null) {
      synchronized (_fieldCache) {
        o = getFields(inClass).get(name);

        if (o == null) {
          _superclasses.clear();
          for (Class sc = inClass; (sc != null); sc = sc.getSuperclass()) {
            if ((o = getFields(sc).get(name)) == NotFound)
              break;

            _superclasses.add(sc);

            if ((result = (Field) o) != null)
              break;
          }
          /*
           * Bubble the found value (either cache miss or actual field) to all supeclasses
           * that we saw for quicker access next time.
           */
          for (int i = 0, icount = _superclasses.size(); i < icount; i++) {
            getFields((Class) _superclasses.get(i)).put(name, (result == null) ? NotFound : result);
          }
        } else {
          if (o instanceof Field) {
            result = (Field) o;
          } else {
            if (result == NotFound)
              result = null;
          }
        }
      }
    } else {
      if (o instanceof Field) {
        result = (Field) o;
      } else {
        if (result == NotFound)
          result = null;
      }
    }
    return result;
  }

  public Map getPropertyDescriptors(Class targetClass) throws IntrospectionException, OgnlException {
    Map result;

    if ((result = (Map) _propertyDescriptorCache.get(targetClass)) == null) {
      synchronized (_propertyDescriptorCache) {
        if ((result = (Map) _propertyDescriptorCache.get(targetClass)) == null) {
          PropertyDescriptor[] pda = Introspector.getBeanInfo(targetClass).getPropertyDescriptors();

          result = new HashMap(101);
          for (int i = 0, icount = pda.length; i < icount; i++) {
            // workaround for Introspector bug 6528714 (bugs.sun.com)
            if (pda[i].getReadMethod() != null && !OgnlRuntime.isMethodCallable(pda[i].getReadMethod())) {
              pda[i].setReadMethod(OgnlRuntime.findClosestMatchingMethod(targetClass, pda[i].getReadMethod(),
                  pda[i].getName(), pda[i].getPropertyType(), true));
            }
            if (pda[i].getWriteMethod() != null && !OgnlRuntime.isMethodCallable(pda[i].getWriteMethod())) {
              pda[i].setWriteMethod(OgnlRuntime.findClosestMatchingMethod(targetClass, pda[i].getWriteMethod(),
                  pda[i].getName(), pda[i].getPropertyType(), false));
            }

            result.put(pda[i].getName(), pda[i]);
          }

          OgnlRuntime.findObjectIndexedPropertyDescriptors(targetClass, result);
          _propertyDescriptorCache.put(targetClass, result);
        }
      }
    }
    return result;
  }

  public PropertyDescriptor[] getPropertyDescriptorsArray(Class targetClass) throws IntrospectionException {
    PropertyDescriptor[] result = null;

    if (targetClass != null) {
      if ((result = (PropertyDescriptor[]) _propertyDescriptorCache.get(targetClass)) == null) {
        synchronized (_propertyDescriptorCache) {
          if ((result = (PropertyDescriptor[]) _propertyDescriptorCache.get(targetClass)) == null) {
            _propertyDescriptorCache.put(targetClass,
                result = Introspector.getBeanInfo(targetClass).getPropertyDescriptors());
          }
        }
      }
    }
    return result;
  }

  public MethodAccessor getMethodAccessor(Class cls) throws OgnlException {
    MethodAccessor answer = (MethodAccessor) getHandler(cls, _methodAccessors);
    if (answer != null)
      return answer;
    throw new OgnlException("No method accessor for " + cls);
  }

  public void setMethodAccessor(Class cls, MethodAccessor accessor) {
    synchronized (_methodAccessors) {
      _methodAccessors.put(cls, accessor);
    }
  }

  public void setPropertyAccessor(Class cls, PropertyAccessor accessor) {
    synchronized (_propertyAccessors) {
      _propertyAccessors.put(cls, accessor);
    }
  }

  public PropertyAccessor getPropertyAccessor(Class cls) throws OgnlException {
    PropertyAccessor answer = (PropertyAccessor) getHandler(cls, _propertyAccessors);
    if (answer != null)
      return answer;

    throw new OgnlException("No property accessor for class " + cls);
  }

  public ElementsAccessor getElementsAccessor(Class cls) throws OgnlException {
    ElementsAccessor answer = (ElementsAccessor) getHandler(cls, _elementsAccessors);
    if (answer != null)
      return answer;
    throw new OgnlException("No elements accessor for class " + cls);
  }

  public static void setElementsAccessor(Class cls, ElementsAccessor accessor) {
    synchronized (_elementsAccessors) {
      _elementsAccessors.put(cls, accessor);
    }
  }

  public NullHandler getNullHandler(Class cls) throws OgnlException {
    NullHandler answer = (NullHandler) getHandler(cls, _nullHandlers);
    if (answer != null)
      return answer;
    throw new OgnlException("No null handler for class " + cls);
  }

  public void setNullHandler(Class cls, NullHandler handler) {
    synchronized (_nullHandlers) {
      _nullHandlers.put(cls, handler);
    }
  }

  public static List getDeclaredMethods(Class targetClass, String propertyName, boolean findSets) {
    List result = null;
    ClassCache cache = _declaredMethods[findSets ? 0 : 1];

    Map propertyCache = (Map) cache.get(targetClass);
    if ((propertyCache == null) || ((result = (List) propertyCache.get(propertyName)) == null)) {
      synchronized (cache) {
        propertyCache = (Map) cache.get(targetClass);

        if ((propertyCache == null) || ((result = (List) propertyCache.get(propertyName)) == null)) {

          String baseName = capitalizeBeanPropertyName(propertyName);
          result = new ArrayList();
          collectAccessors(targetClass, baseName, result, findSets);

          if (propertyCache == null) {
            cache.put(targetClass, propertyCache = new HashMap(101));
          }
          propertyCache.put(propertyName, result.isEmpty() ? NotFoundList : result);

          return result.isEmpty() ? null : result;
        }
      }
    }
    return (result == NotFoundList) ? null : result;
  }

  private Object getHandler(Class forClass, ClassCache handlers) {
    Object answer = null;

    if ((answer = handlers.get(forClass)) == null) {
      synchronized (handlers) {
        if ((answer = handlers.get(forClass)) == null) {
          Class keyFound;

          if (forClass.isArray()) {
            answer = handlers.get(Object[].class);
            keyFound = null;
          } else {
            keyFound = forClass;
            outer: for (Class c = forClass; c != null; c = c.getSuperclass()) {
              answer = handlers.get(c);
              if (answer == null) {
                Class[] interfaces = c.getInterfaces();
                for (int index = 0, count = interfaces.length; index < count; ++index) {
                  Class iface = interfaces[index];

                  answer = handlers.get(iface);
                  if (answer == null) {
                    /* Try super-interfaces */
                    answer = getHandler(iface, handlers);
                  }
                  if (answer != null) {
                    keyFound = iface;
                    break outer;
                  }
                }
              } else {
                keyFound = c;
                break;
              }
            }
          }
          if (answer != null) {
            if (keyFound != forClass) {
              handlers.put(forClass, answer);
            }
          }
        }
      }
    }
    return answer;
  }

  public static void setClassCacheInspector(ClassCacheInspector inspector) {
    _cacheInspector = inspector;

    _propertyDescriptorCache.setClassInspector(_cacheInspector);
    _constructorCache.setClassInspector(_cacheInspector);
    _staticMethodCache.setClassInspector(_cacheInspector);
    _instanceMethodCache.setClassInspector(_cacheInspector);
    _invokePermissionCache.setClassInspector(_cacheInspector);
    _fieldCache.setClassInspector(_cacheInspector);
    _declaredMethods[0].setClassInspector(_cacheInspector);
    _declaredMethods[1].setClassInspector(_cacheInspector);
  }

  private static String capitalizeBeanPropertyName(String propertyName) {
    if (propertyName.length() == 1) {
      return propertyName.toUpperCase();
    }
    // don't capitalize getters/setters
    if (propertyName.startsWith(GET_PREFIX) && propertyName.endsWith("()")) {
      if (Character.isUpperCase(propertyName.substring(3, 4).charAt(0))) {
        return propertyName;
      }
    }
    if (propertyName.startsWith(SET_PREFIX) && propertyName.endsWith(")")) {
      if (Character.isUpperCase(propertyName.substring(3, 4).charAt(0))) {
        return propertyName;
      }
    }
    if (propertyName.startsWith(IS_PREFIX) && propertyName.endsWith("()")) {
      if (Character.isUpperCase(propertyName.substring(2, 3).charAt(0))) {
        return propertyName;
      }
    }
    char first = propertyName.charAt(0);
    char second = propertyName.charAt(1);
    if (Character.isLowerCase(first) && Character.isUpperCase(second)) {
      return propertyName;
    } else {
      char[] chars = propertyName.toCharArray();
      chars[0] = Character.toUpperCase(chars[0]);
      return new String(chars);
    }
  }
  
  private static void collectAccessors(Class c, String baseName, List result, boolean findSets) {
    final Method[] methods = c.getDeclaredMethods();
    for (int i = 0; i < methods.length; i++) {
      if (c.isInterface()) {
        if (OgnlRuntime.isDefaultMethod(methods[i])) {
          addIfAccessor(result, methods[i], baseName, findSets);
        }
        continue;
      }

      if (!OgnlRuntime.isMethodCallable(methods[i]))
        continue;

      addIfAccessor(result, methods[i], baseName, findSets);
    }

    final Class superclass = c.getSuperclass();
    if (superclass != null)
      collectAccessors(superclass, baseName, result, findSets);

    for (final Class iface : c.getInterfaces())
      collectAccessors(iface, baseName, result, findSets);
  }
  
  private static void addIfAccessor(List result, Method method, String baseName, boolean findSets) {
    final String ms = method.getName();
    if (ms.endsWith(baseName)) {
      boolean isSet = false, isIs = false;
      if ((isSet = ms.startsWith(SET_PREFIX)) || ms.startsWith(GET_PREFIX) || (isIs = ms.startsWith(IS_PREFIX))) {
        int prefixLength = (isIs ? 2 : 3);
        if (isSet == findSets) {
          if (baseName.length() == (ms.length() - prefixLength)) {
            result.add(method);
          }
        }
      }
    }
  }
}
