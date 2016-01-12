/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.monash.security;

import edu.monash.DAO.AccountDAO;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
/**
 *
 * @author zipv5_000
 */
public class SecureReceiver{
    
    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final String AES_KEY_ALGORITHM = "AES";
    private static final String AES_ENCRYPT_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String RSA_ENCRYPT_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String SIGN_ALGORITHM = "SHA1withRSA";
    
    private static final String URL_CODE = "UTF-8";
    
    private final KeyPair serverKeyPair;
    private final String serverPublKeyString;
    
    public SecureReceiver() throws NoSuchAlgorithmException{
        
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
        generator.initialize(1024);
        serverKeyPair = generator.generateKeyPair();
        serverPublKeyString = Base64.encodeBase64String(serverKeyPair.getPublic().getEncoded());
    }
    
    public String getServerPublKeyString(){
        return serverPublKeyString;
    }
    public SecretKey getSecretKey(String keyString) throws Exception{
        Cipher cipher = Cipher.getInstance(RSA_KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, serverKeyPair.getPrivate());
        return new SecretKeySpec(cipher.doFinal(Base64.decodeBase64(keyString)), AES_KEY_ALGORITHM);
    }
    public String Decrypt(String raw, SecretKey key) throws Exception{
        Cipher cipher = Cipher.getInstance(AES_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(raw)));
    }
    public String Encrypt(String raw, SecretKey key) throws Exception{
        Cipher cipher = Cipher.getInstance(AES_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64String(cipher.doFinal(raw.getBytes()));
    }
    public Boolean authenticate(Integer userid, String raw, String signature) throws Exception{
        Signature sign = Signature.getInstance(SIGN_ALGORITHM);
        sign.initVerify(getUserPublicKey(userid));
        sign.update(raw.getBytes());
        return sign.verify(Base64.decodeBase64(signature));
    }
    private PublicKey getUserPublicKey(Integer userid) throws Exception{
        String publKeyString = new AccountDAO().getClientKeyString(userid);
        KeyFactory kfc = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(publKeyString));
        return kfc.generatePublic(spec);
    }
    public String calculateMD5(String contentToEncode) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(contentToEncode.getBytes());
        return Base64.encodeBase64String(digest.digest());
    }
}
