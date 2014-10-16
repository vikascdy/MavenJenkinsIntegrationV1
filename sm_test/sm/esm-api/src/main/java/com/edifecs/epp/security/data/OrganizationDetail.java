package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDetail implements Serializable {

    private static final long serialVersionUID = -8027419235645413756L;

    private Long id;

    private String text;

    private boolean leaf;

    private List<OrganizationDetail> children = new ArrayList<>();

    public List<OrganizationDetail> getChildOrganizations() {
        return children;
    }

    public void setChildOrganizations(
            List<OrganizationDetail> childOrganizations) {
        this.children = childOrganizations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<OrganizationDetail> getChildren() {
        return children;
    }

    public void setChildren(List<OrganizationDetail> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

}
