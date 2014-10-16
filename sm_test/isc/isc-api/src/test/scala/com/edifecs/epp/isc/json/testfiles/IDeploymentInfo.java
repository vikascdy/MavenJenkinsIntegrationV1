package com.edifecs.epp.isc.json.testfiles;

import java.io.Serializable;
import java.util.Date;

/**
 * Information about a deployment.
 * 
 * @author hongliii
 *
 */
public interface IDeploymentInfo extends Identifiable<String>, Serializable {

    String getCategory();

    Date getDeploymentTime();

    String getName();

    String getTenantId();
    
    Date getActivateOn();
}