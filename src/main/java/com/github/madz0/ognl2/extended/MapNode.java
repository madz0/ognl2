package com.github.madz0.ognl2.extended;

import com.github.madz0.ognl2.Ognl;
import com.github.madz0.ognl2.Ognl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapNode {
    private Map<String, MapNode> children = new LinkedHashMap<>();
    private String name;
    private Object value;
    private Ognl.NodeType nodeType;
    private Boolean isRoot = false;
    private MapNode parent;
    private String path;
    private Boolean containsValue;
    private Boolean containsEmptyChildValue;
    private String constantName;

    public MapNode(String name, Ognl.NodeType nodeType, MapNode parent, Boolean containsEmptyChildValue) {
        if (name.startsWith("[") && !name.startsWith("['")
                && !name.startsWith("[\"") && !name.matches("\\[\\d+\\]")) {
            this.constantName = "['" + name.substring(1, name.length() - 1) + "']";
        }
        else {
            this.constantName = name;
        }
        this.name = name;
        this.nodeType = nodeType;
        this.parent = parent;
        this.containsValue = false;
        this.containsEmptyChildValue = containsEmptyChildValue;
    }

    public MapNode getMapping(String name) {
        return children.get(name);
    }

    public String getPath() {
        if (path != null) {
            return path;
        }
        if (parent == null) {
            return path = name;
        }
        return parent.getPath() + (!parent.isCollection() ? "." : "") + name;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Map<String, MapNode> getChildren() {
        return children;
    }

    public Ognl.NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(Ognl.NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Boolean isCollection() {
        return nodeType == Ognl.NodeType.COLLECTION;
    }

    public Boolean getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(Boolean root) {
        isRoot = root;
    }

    public Boolean getContainsValue() {
        return containsValue;
    }

    public void setContainsValue(Boolean containsValue) {
        this.containsValue = containsValue;
    }

    public Boolean getContainsEmptyChildValue() {
        return containsEmptyChildValue;
    }

    public String getConstantName() {
        return this.constantName;
    }
}
