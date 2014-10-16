package com.edifecs.epp.isc.json.testfiles;


/**
 * Holds the information about a deployment and actual data for the deployment.
 * 
 * @author hongliii
 *
 */
public interface IDeployment<T> extends IDeploymentInfo {
    T getBpmnData();
}