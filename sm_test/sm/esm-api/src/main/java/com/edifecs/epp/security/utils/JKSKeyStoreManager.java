package com.edifecs.epp.security.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class JKSKeyStoreManager {
    private static final String keystoreType = "JKS";
    
    private final KeyStore store;
    private final char[] password;
    
    public JKSKeyStoreManager(String path) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchPaddingException, InvalidKeyException, UnrecoverableEntryException, IllegalBlockSizeException, BadPaddingException {
        this(new FileInputStream(path), "changeit".toCharArray());
    }

    public JKSKeyStoreManager(InputStream inputStream) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchPaddingException, InvalidKeyException, UnrecoverableEntryException, IllegalBlockSizeException, BadPaddingException {
        this(inputStream, "changeit".toCharArray());
    }

    public JKSKeyStoreManager(String path, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchPaddingException, InvalidKeyException, UnrecoverableEntryException, IllegalBlockSizeException, BadPaddingException {
        this(new FileInputStream(path), password.toCharArray());
    }
    
    public JKSKeyStoreManager(InputStream inputStream, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchPaddingException, InvalidKeyException, UnrecoverableEntryException, IllegalBlockSizeException, BadPaddingException {
        this(inputStream, password.toCharArray());
    }
    
    public JKSKeyStoreManager(InputStream inputStream, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchPaddingException, InvalidKeyException, UnrecoverableEntryException, IllegalBlockSizeException, BadPaddingException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream cannot be null.");
        }
        
        // Load the KeyStore
        store = KeyStore.getInstance(keystoreType);
        store.load(inputStream, password);
        this.password = password;
    }
    
    public X509Certificate getX509Certificate(String certAlias) throws KeyStoreException {
        return (X509Certificate) store.getCertificate(certAlias);
    }
    
    public Cipher getCipher(String cipherType) throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance(cipherType);
    }
    
    public byte[] getRSAEncodedKey(String certAlias) throws InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnrecoverableEntryException {
        return getEncodedKey(certAlias, password);
    }
    
    public byte[] getEncodedKey(String certAlias, char[] certPassword) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnrecoverableEntryException, InvalidKeyException {
        KeyStore.PrivateKeyEntry privateKey = (KeyStore.PrivateKeyEntry) store.getEntry(
                certAlias, new KeyStore.PasswordProtection(certPassword));
        return privateKey.getPrivateKey().getEncoded();
    }
    
}
