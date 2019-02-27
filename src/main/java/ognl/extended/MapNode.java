package ognl.extended;

import ognl.Ognl;

import java.util.HashMap;
import java.util.Map;

public class MapNode {
    private Map<String, MapNode> children = new HashMap<>();
    private String name;
    private String value;
    private Ognl.NodeType nodeType;
    private Boolean isRoot = false;

    public MapNode(String name, Ognl.NodeType nodeType) {
        this.name = name;
        this.nodeType = nodeType;
    }

    public MapNode getMapping(String name) {
        return children.get(name);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
}
