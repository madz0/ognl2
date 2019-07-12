package com.github.madz0.ognl2.extended;

import com.github.madz0.ognl2.OgnlException;
import com.github.madz0.ognl2.Ognl;
import com.github.madz0.ognl2.OgnlContext;
import com.github.madz0.ognl2.OgnlException;
import com.github.madz0.ognl2.OgnlRuntime;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MapNodeObjectConstructor extends DefaultObjectConstructor {
    @Override
    public Object processObjectForGet(OgnlContext context, Object root, OgnlPropertyDescriptor propertyDescriptor, Object propertyObject, MapNode node) {
        if (node != null && node.isCollection() && propertyDescriptor.isPropertyDescriptor()) {
            Class genericClazz = (Class) ((ParameterizedType) propertyDescriptor.getReadMethod().getGenericReturnType()).getActualTypeArguments()[0];
            List dest = propertyObject instanceof List ? (List) propertyObject :
                    new ArrayList((Collection) propertyObject);
            for (MapNode collectionNode : node.getChildren().values()) {
                final OgnlContext contextFinal = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
                contextFinal.extend();
                contextFinal.setObjectConstructor(this);

                try {
                    int index = getIndexOfNode(collectionNode);
                    Object item = dest.size() > index ? dest.get(index) : null;
                    if (item == null) {
                        item = OgnlRuntime.createProperObject(contextFinal, genericClazz, genericClazz.getComponentType(), node);
                        expandList(dest, index);
                        dest.set(index, item);
                    }
                    if (!collectionNode.getContainsValue() || collectionNode.getValue() != null) {
                        try {
                            Ognl.getValue(collectionNode, contextFinal, item);
                        } catch (OgnlException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(propertyObject instanceof Set) {
                ((Set) propertyObject).clear();
                ((Set) propertyObject).addAll(dest);
            }
            return propertyObject;
        } else {
            return super.processObjectForGet(context, root, propertyDescriptor, propertyObject, node);
        }
    }

    private int getIndexOfNode(MapNode collectionNode) {
        return Integer.valueOf(collectionNode.getName().substring(1, collectionNode.getName().length() - 1));
    }

    private void expandList(List list, int index) {
        for (int i = list.size(); i <= index; ++i) {
            list.add(null);
        }
    }
}
