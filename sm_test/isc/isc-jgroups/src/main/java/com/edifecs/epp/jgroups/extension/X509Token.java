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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jgroups.Message;
import org.jgroups.annotations.Property;
import org.jgroups.auth.AuthToken;
import org.jgroups.util.Util;

public class X509Token extends AuthToken {

    public static final String KEYSTORE_TYPE = "keystore_type";
    public static final String KEYSTORE_PATH = "keystore_path";
    public static final String KEYSTORE_PASSWORD = "keystore_password";
    public static final String CERT_ALIAS = "cert_alias";
    public static final String CERT_PASSWORD = "cert_password";
    public static final String TOKEN_ATTR = "auth_value";
    public static final String CIPHER_TYPE = "cipher_type";

    private boolean valueSet = false;

    private String keystoreType = "JKS";
    
    private String certAlias = null;
    private InputStream keystore = null;
    private String authValue = null;
    private String cipherType = "RSA";
    
    private byte[] encryptedToken = null;

    private char[] certPassword = null;
    private char[] keystorePassword = null;

    private Cipher cipher = null;
    private PrivateKey certPrivateKey = null;
    private X509Certificate certificate = null;

    public static String getKeystoreType() {
        return KEYSTORE_TYPE;
    }

    public String getCertAlias() {
        return certAlias;
    }

    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias;
    }

    public InputStream getKeystore() {
        return keystore;
    }

    public void setKeystore(InputStream keystore) {
        this.keystore = keystore;
    }

    public String getAuthValue() {
        return authValue;
    }

    public void setAuthValue(String authValue) {
        this.authValue = authValue;
    }

    public String getCipherType() {
        return cipherType;
    }

    public void setCipherType(String cipherType) {
        this.cipherType = cipherType;
    }

    public X509Token() {
        // need an empty constructor
    }

    @Property(name = "cert_password")
    public void setCertPassword(String pwd) {
        this.certPassword = pwd.toCharArray();
    }

    @Property(name = "keystore_password")
    public void setKeyStorePassword(String pwd) {
        this.keystorePassword = pwd.toCharArray();
        if (certPassword == null) {
            certPassword = keystorePassword;
        }
    }

    @Override
    public String getName() {
        return "org.jgroups.auth.NewX509Token";
    }

    @Override
    public boolean authenticate(AuthToken token, Message msg) {
        if (!this.valueSet) {
            if (log.isErrorEnabled()) {
                log.error("NewX509Token not setup correctly - check token attrs");
            }
            return false;
        }

        if ((token != null) && (token instanceof X509Token)) {
            // got a valid X509 token object
            X509Token serverToken = (X509Token) token;
            if (!serverToken.valueSet) {
                if (log.isErrorEnabled()) {
                    log.error("X509Token - recieved token not valid");
                }
                return false;
            }

            try {
                if (log.isDebugEnabled()) {
                    log.debug("setting cipher to decrypt mode");
                }
                this.cipher.init(Cipher.DECRYPT_MODE, this.certPrivateKey);
                String serverBytes = new String(this.cipher.doFinal(serverToken.encryptedToken));
                if ((serverBytes.equalsIgnoreCase(this.authValue))) {
                    if (log.isDebugEnabled()) {
                        log.debug("X509 authentication passed");
                    }
                    return true;
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.toString());
                }
            }
        }
        // if(log.isWarnEnabled()){
        // log.warn("X509 authentication failed");
        // }
        return false;
    }

    @Override
    public void writeTo(DataOutput out) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("X509Token writeTo()");
        }
        Util.writeByteBuffer(this.encryptedToken, out);
    }

    @Override
    public void readFrom(DataInput in) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("X509Token readFrom()");
        }
        this.encryptedToken = Util.readByteBuffer(in);
        this.valueSet = true;
    }

    /**
     * Used during setup to get the certification from the keystore and encrypt the auth_value with
     * the private key
     * 
     * @return true if the certificate was found and the string encypted correctly otherwise returns
     *         false
     */
    public void setCertificate() throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnrecoverableEntryException {
        KeyStore store = KeyStore.getInstance(this.keystoreType);
        store.load(keystore, this.keystorePassword);

        this.cipher = Cipher.getInstance(this.cipherType);
        this.certificate = (X509Certificate) store.getCertificate(this.certAlias);

        if (log.isDebugEnabled()) {
            log.debug("certificate = " + this.certificate.toString());
        }

        this.cipher.init(Cipher.ENCRYPT_MODE, this.certificate);
        this.encryptedToken = this.cipher.doFinal(this.authValue.getBytes());

        if (log.isDebugEnabled()) {
            log.debug("encryptedToken = " + this.encryptedToken);
        }

        KeyStore.PrivateKeyEntry privateKey = (KeyStore.PrivateKeyEntry) store.getEntry(
                this.certAlias, new KeyStore.PasswordProtection(this.certPassword));
        this.certPrivateKey = privateKey.getPrivateKey();

        this.valueSet = true;

        if (log.isDebugEnabled()) {
            log.debug("certPrivateKey = " + this.certPrivateKey.toString());
        }
    }

    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }
}
