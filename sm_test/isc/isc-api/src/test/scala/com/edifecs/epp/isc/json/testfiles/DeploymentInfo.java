package com.edifecs.epp.isc.json.testfiles;

import java.util.Date;

public class DeploymentInfo extends IdentifiableObject<String> implements IDeploymentInfo {
    private static final long serialVersionUID = -1375192152624557640L;

    private String name;
    private String category;
    private Date activateOn;

    private String tenantId;
    private Date deploymentTime;
    
    public DeploymentInfo() {
        // default constructor needed by the Java bean specification.
    }
    
    public DeploymentInfo(String name, String category) {
        this.setName(name);
        this.setCategory(category);
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public Date getDeploymentTime() {
        return deploymentTime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    public void setDeploymentTime(Date deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Date getActivateOn() {
        return activateOn;
    }
    
    public void setActivateOn(Date on) {
        this.activateOn = on;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
