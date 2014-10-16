// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.core.configuration.helper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.edifecs.core.configuration.configuration.DeploymentConfiguration;

public enum JAXBUtility {
    CONFIGURATION("DeploymentConfiguration");

    private JAXBContext context;

    JAXBUtility(String classToCreate) {
        try {
            this.context = JAXBContext.newInstance(DeploymentConfiguration.class);
        } catch (JAXBException ex) {
            throw new IllegalStateException("Unable to create JAXBContextSingleton");
        }
    }

    public JAXBContext getContext() {
        return context;
    }
    
}
