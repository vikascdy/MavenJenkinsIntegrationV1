package com.edifecs.epp.isc.json.testfiles;


public class Deployment<T> extends DeploymentInfo implements IDeployment<T> {
    private static final long serialVersionUID = 1371527377051335202L;

    private T bpmnData;
    
    public Deployment() {
    	
    }
    
    public Deployment(String name, String category, T bpmnData) {
        super(name, category);
        this.bpmnData = bpmnData;
    }

    @Override
    public T getBpmnData() {
        return bpmnData;
    }
    
    public void setBpmnData(T data) {
    	this.bpmnData = data;
    }
}
