/*
 * Decompiled with CFR 0.139.
 */
package com.github.madz0.ognl2.extended;

import com.github.madz0.ognl2.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ExMapPropertyAccessor
        extends ExObjectPropertyAccessor
        implements PropertyAccessor {
    private PropertyAccessor mapPropertyAccessor = new MapPropertyAccessor();

    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        Object result;
        OgnlContext ognlContext = (OgnlContext) context;
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
            this.shiftGenericParameters(ognlContext, level);
            return target;
        }

        if (context.get(Config.EXPRESSION_SET) == Boolean.TRUE) {
            //Ok a literal without quotation passed eg [name] instead of ['name']
            this.shiftGenericParameters(ognlContext, level);
            return name;
        }

        Map map = (Map) target;
        Node currentNode = ognlContext.getCurrentNode().jjtGetParent();
        boolean indexedAccess = false;
        if (currentNode == null) {
            throw new OgnlException("node is null for '" + name + "'");
        }
        if (!(currentNode instanceof ASTProperty)) {
            currentNode = currentNode.jjtGetParent();
        }
        if (currentNode instanceof ASTProperty) {
            indexedAccess = ((ASTProperty) currentNode).isIndexedAccess();
        }
        if (name instanceof String && !indexedAccess) {
            if ("size".equals(name)) {
                result = map.size();
            } else if ("keys".equals(name) || "keySet".equals(name)) {
                result = map.keySet();
            } else if ("values".equals(name)) {
                result = map.values();
            } else if ("isEmpty".equals(name)) {
                result = map.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
            } else {
                return putMapObject(ognlContext, level, map, name);
            }
        } else {
            return putMapObject(ognlContext, level, map, name);
        }
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        OgnlContext ognlContext = (OgnlContext) context;
        int level = this.incIndex(context);
        Class keyClsObj = (Class) this.getParameterizedType(ognlContext, level, 0);
        Class valueClsObj = (Class) this.getParameterizedType(ognlContext, level, 1);
        name = OgnlOps.convertValue(name, keyClsObj);
        value = OgnlOps.convertValue(value, valueClsObj);
        Map map = (Map) target;
        map.put(name, value);
    }

    @Override
    public int getGenericArgumentsCount() {
        return 2;
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return mapPropertyAccessor.getSourceAccessor(context, target, index);
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return mapPropertyAccessor.getSourceSetter(context, target, index);
    }

    private Object putMapObject(OgnlContext ognlContext, int level, Map map, Object name) throws OgnlException {
        Object result;
        Class keyClsObj = (Class) this.getParameterizedType(ognlContext, level, 0);
        name = OgnlOps.convertValue(name, keyClsObj);
        result = map.get(name);
        Object clsObj = this.getParameterizedType(ognlContext, level, 1);
        if (this.isNullInited(ognlContext) && result == null) {
            if (clsObj == null) {
                if (this.isUnknownInited(ognlContext)) {
                    result = new Object();
                } else {
                    throw new OgnlException("Could not determine type of the Map");
                }
            } else {
                Class cls = (Class) clsObj;
                try {
                    result = this.createProperObject(ognlContext, cls, cls.getComponentType());
                    if (cls.isArray()) {
                        this.keepArraySource(ognlContext, map, (String) name, level);
                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } else if (result != null && result.getClass().isArray()) {
            this.keepArraySource(ognlContext, map, (String) name, level);
        }
        result = processObjectForGet(ognlContext, map, null, name, result);
        map.put(name, result);
        return result;
    }
}

