package com.edifecs.epp.isc.builder.auth;

import java.io.InputStream;

import org.jgroups.JChannel;
import org.jgroups.stack.Protocol;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.jgroups.extension.Auth;
import com.edifecs.epp.jgroups.extension.X509Token;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.exception.ClusterBuilderException;

public class X509TokenAuthProtocol extends AuthProtocol {

    private InputStream authenticationKeystore;
    
    private String certAlias = "jgroups";
    
    private String authValue = "chris";
    
    private String keyStorePassword = "changeit";
    
    private String cipherType = "RSA";
    
    @Override
    public void build(JChannel channel) throws ClusterBuilderException {
        
        if(authenticationKeystore == null) {
            authenticationKeystore = CommandCommunicator.class.getResourceAsStream("/" + SystemVariables.SECURITY_CERTIFICATE_FILE_NAME);
            if (authenticationKeystore == null) {
                throw new ClusterBuilderException("A keystore is required in order to use the X509TokenAuth protocol");
            }
        }
        
        Auth protocol = new Auth(authenticationKeystore);
        
        try {
            protocol.setAuthClass("com.edifecs.epp.jgroups.extension.X509Token");
        } catch (Exception e) {
            throw new ClusterBuilderException("Unable to instantiate class com.edifecs.epp.jgroups.extension.NewX509Token", e);
        }
        
        X509Token token = (X509Token) protocol.getAuthToken();
        token.setAuthValue(authValue);
        token.setKeyStorePassword(keyStorePassword);
        token.setCertAlias(certAlias);
        token.setCipherType(cipherType);
        
        try {
            protocol.init();
        } catch (Exception e) {
            throw new ClusterBuilderException("Unable to initialize the authentication protocol.", e);
        }
        
        // TODO: Investigate why we set the position manually, and not use a relative location to GMS
        Protocol protoGMS = channel.getProtocolStack().findProtocol("GMS");
        channel.getProtocolStack().insertProtocolInStack(protocol.getAuth(), protoGMS, 11);
        
    }
    
    public AuthProtocol setAuthenticationKeystore(InputStream authenticationKeystore) {
        this.authenticationKeystore = authenticationKeystore;
        return this;
    }

    public AuthProtocol setCertAlias(String certAlias) {
        this.certAlias = certAlias;
        return this;
    }

    public AuthProtocol setAuthValue(String authValue) {
        this.authValue = authValue;
        return this;
    }

    public AuthProtocol setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }

    public AuthProtocol setCipherType(String cipherType) {
        this.cipherType = cipherType;
        return this;
    }

    
    
}
