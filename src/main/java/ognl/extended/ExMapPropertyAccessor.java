/*
 * Decompiled with CFR 0.139.
 */
package ognl.extended;

import ognl.ASTProperty;
import ognl.Node;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ExMapPropertyAccessor
extends ExObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        Object result;
        OgnlContext ognlContext = (OgnlContext) context;
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
            this.shiftGenericParameters(ognlContext, level);
            return target;
        }
        Map map = (Map)target;
        Node currentNode = ognlContext.getCurrentNode().jjtGetParent();
        boolean indexedAccess = false;
        if (currentNode == null) {
            throw new OgnlException("node is null for '" + name + "'");
        }
        if (!(currentNode instanceof ASTProperty)) {
            currentNode = currentNode.jjtGetParent();
        }
        if (currentNode instanceof ASTProperty) {
            indexedAccess = ((ASTProperty)currentNode).isIndexedAccess();
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
                if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(ognlContext.getRoot().getClass())) {
                    this.shiftGenericParameters(ognlContext, level);
                    return target;
                }
                result = map.get(name);
                Object clsObj = this.getParameterizedType(ognlContext, level, 1);
                if (this.isNullInited(context) && result == null) {
                    if (clsObj == null) {
                        if (this.isUnknownInited(context)) {
                            result = new Object();
                            map.put(name, result);
                            return result;
                        }
                        throw new OgnlException("Could not determine type of the Map");
                    }
                    Class cls = (Class)clsObj;
                    try {
                        result = this.createProperObject(ognlContext, cls, cls.getComponentType());
                        if (cls.isArray()) {
                            this.keepArraySource(ognlContext, target, (String)name, level);
                        }
                        map.put(name, result);
                        return result;
                    }
                    catch (IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                if (result != null && result.getClass().isArray()) {
                    this.keepArraySource(ognlContext, target, (String)name, level);
                }
            }
        } else {
            result = map.get(name);
            Object clsObj = this.getParameterizedType(ognlContext, level, 1);
            if (this.isNullInited(context) && result == null) {
                if (clsObj == null) {
                    if (this.isUnknownInited(context)) {
                        result = new Object();
                        map.put(name, result);
                        return result;
                    }
                    throw new OgnlException("Could not determine type of the Map");
                }
                Class cls = (Class)clsObj;
                try {
                    result = this.createProperObject(ognlContext, cls, cls.getComponentType());
                    if (cls.isArray()) {
                        this.keepArraySource(ognlContext, target, (String)name, level);
                    }
                    map.put(name, result);
                    return result;
                }
                catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if (result != null && result.getClass().isArray()) {
                this.keepArraySource(ognlContext, target, (String)name, level);
            }
        }
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        this.incIndex(context);
        Map map = (Map)target;
        map.put(name, value);
    }

    @Override
    public int getGenericArgumentsCount() {
        return 2;
    }
}

