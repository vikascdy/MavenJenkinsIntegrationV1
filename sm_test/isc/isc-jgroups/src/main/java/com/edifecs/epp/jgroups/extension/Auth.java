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
package com.edifecs.epp.jgroups.extension;

import java.io.InputStream;

import org.jgroups.auth.AuthToken;
import org.jgroups.protocols.AUTH;

public class Auth {

    private AUTH auth;

    private InputStream storePath = null;

    public Auth() {
        auth = new AUTH();
    }

    public Auth(InputStream path) {
        auth = new AUTH();
        this.storePath = path;
    }

    public void setAuthClass(String className) throws Exception {
        Object obj = Class.forName(className).newInstance();
        auth.setAuthToken((AuthToken) obj);
        auth.getAuthToken().setAuth(auth);
    }

    public void init() throws Exception {
        auth.init();
        AuthToken token = auth.getAuthToken();
        if (token instanceof X509Token) {
            X509Token tmp = (X509Token) token;
            tmp.setKeystore(this.storePath);
            tmp.setCertificate();
        }
        token.init();
    }

    public AuthToken getAuthToken() {
        return auth.getAuthToken();
    }

    public AUTH getAuth() {
        return auth;
    }
}
