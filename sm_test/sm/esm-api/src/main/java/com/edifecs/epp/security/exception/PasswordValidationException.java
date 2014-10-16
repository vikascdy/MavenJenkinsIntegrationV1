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
package com.edifecs.epp.security.exception;

import com.edifecs.epp.security.data.PasswordPolicy;

/**
 * This is a test Exception that tests how we can break out exceptions to use a
 * restrictive well define format.
 * 
 * @author willclem
 * 
 */
public class PasswordValidationException extends SecurityManagerException {

    @Override
    public String getMessage() {
        return "Password does not match the configured password policy: {0}";
    }

    public PasswordValidationException(PasswordPolicy policy) {
        super(new String[] {policy.getPasswdRegexDesc()});
    }
    public PasswordValidationException(PasswordPolicy policy, Throwable cause) {
        super(new String[] {policy.getPasswdRegexDesc()}, cause);
    }
}
