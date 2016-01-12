package edu.monash.mymonashmate.client;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.util.Base64;

public class SecureSender {

	private static final String AES_KEY_ALGORITHM = "AES";
	private static final String RSA_KEY_ALGORITHM = "RSA";
	private static final String AES_ENCRYPT_ALGORITHM = "AES/ECB/PKCS5Padding";
	private static final String RSA_ENCRYPT_ALGORITHM = "RSA/ECB/PKCS1Padding";
	private static final String SIGN_ALGORITHM = "SHA1withRSA";
	
	private KeyPair clientKeyPair;
	private String clientPublKeyString;
	
	private PublicKey serverPublKey;
	
	private SecretKey secretKey;
	private String secretKeyString;
	
	public SecureSender() throws NoSuchAlgorithmException{
		
		// Generate asymmetrical key
		KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
		generator.initialize(1024);
		clientKeyPair = generator.generateKeyPair();
		clientPublKeyString = Base64.encodeToString(clientKeyPair.getPublic().getEncoded(), Base64.DEFAULT);
		
		// Generate symmetrical key
		KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_KEY_ALGORITHM);
		keyGenerator.init(128);
		secretKey = keyGenerator.generateKey();
	}
	/*
	 * Store server public key
	 */
	public void setServerPubKey(String pubKeyString) throws Exception{
		KeyFactory kfc = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(pubKeyString, Base64.DEFAULT));
		serverPublKey = kfc.generatePublic(spec);
		
		// Encrypt secret key string
		Cipher cipher = Cipher.getInstance(RSA_ENCRYPT_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, serverPublKey);
		secretKeyString = Base64.encodeToString(cipher.doFinal(secretKey.getEncoded()), Base64.DEFAULT);
	}
	/*
	 * Encrypt using symmetry key
	 */
	public String Encrypt(String raw) throws Exception{
		Cipher cipher = Cipher.getInstance(AES_ENCRYPT_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return Base64.encodeToString(cipher.doFinal(raw.getBytes()), Base64.DEFAULT);
	}
	/*
	 * Decrypt using symmetry key
	 */
	public String Decrypt(String raw) throws Exception{
		Cipher cipher = Cipher.getInstance(AES_ENCRYPT_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(Base64.decode(raw, Base64.DEFAULT)));
	}
	
	public String getSecrectKeyString(){
		return secretKeyString;
	}
	public String getClientPublKeyString(){
		return clientPublKeyString;
	}
	
	public String sign(String raw) throws Exception{
		Signature instance = Signature.getInstance(SIGN_ALGORITHM);
        instance.initSign(clientKeyPair.getPrivate());
        instance.update(raw.getBytes());
        return Base64.encodeToString(instance.sign(), Base64.DEFAULT);
	}
	public String getUTCTime() {
		SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormatUTC.format(new Date());
	}
	
    public String calculateMD5(String contentToEncode) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(contentToEncode.getBytes());
		return Base64.encodeToString(digest.digest(), Base64.DEFAULT).trim();
	}
}
