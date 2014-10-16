package com.edifecs.epp.flexfields.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sandeep.kath on 5/8/2014.
 */
public class FlexGroupTreeNode {
    private FlexGroup flexGroup;

    public FlexGroupTreeNode() {
        children = new HashSet<FlexGroupTreeNode>();

    }

    public void addChild(FlexGroupTreeNode node) {
        children.add(node);
    }

    public void removeChild(FlexGroupTreeNode node) {
        children.remove(node);
    }

    public FlexGroup getFlexGroup() {
        return flexGroup;
    }

    public void setFlexGroup(FlexGroup flexGroup) {
        this.flexGroup = flexGroup;
    }

    public FlexGroupTreeNode getParent() {
        return parent;
    }

    public void setParent(FlexGroupTreeNode parent) {
        this.parent = parent;
    }

    public Set<FlexGroupTreeNode> getChildren() {
        return children;
    }

    public void setChildren(Set<FlexGroupTreeNode> children) {
        this.children = children;
    }

    private FlexGroupTreeNode parent;
    private Set<FlexGroupTreeNode> children;


}
