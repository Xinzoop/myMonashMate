package edu.monash.testwebservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecureSender {

    private static final String AES_KEY_ALGORITHM = "AES";
    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final String AES_ENCRYPT_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String RSA_ENCRYPT_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String SIGN_ALGORITHM = "SHA1withRSA";

    private static final String URL_CODE = "UTF-8";

    private String baseURL = "http://localhost:8080";

    private KeyPair clientKeyPair;
    private String clientPublKeyString;

    private PublicKey serverPublKey;

    private SecretKey secretKey;
    private String secretKeyString;

    public SecureSender() throws NoSuchAlgorithmException {

        // Generate asymmetrical key
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
        generator.initialize(1024);
        clientKeyPair = generator.generateKeyPair();
        clientPublKeyString = Base64.encodeBase64String(clientKeyPair.getPublic().getEncoded());

        // Generate symmetrical key
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_KEY_ALGORITHM);
        keyGenerator.init(128);
        secretKey = keyGenerator.generateKey();
    }
    /*
     * Store server public key
     */

    public void setServerPubKey(String pubKeyString) throws Exception {
        KeyFactory kfc = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(pubKeyString));
        serverPublKey = kfc.generatePublic(spec);

        // Encrypt secret key string
        Cipher cipher = Cipher.getInstance(RSA_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, serverPublKey);
        secretKeyString = Base64.encodeBase64String(cipher.doFinal(secretKey.getEncoded()));
    }
    /*
     * Encrypt using symmetry key
     */

    public String Encrypt(String raw) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.encodeBase64String(cipher.doFinal(raw.getBytes()));
    }
    /*
     * Decrypt using symmetry key
     */

    public String Decrypt(String raw) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.decodeBase64(raw)));
    }

    public String getSecrectKeyString() {
        return secretKeyString;
    }

    public String getClientPublKeyString() {
        return clientPublKeyString;
    }

    public String sign(String raw) throws Exception {
        Signature instance = Signature.getInstance(SIGN_ALGORITHM);
        instance.initSign(clientKeyPair.getPrivate());
        instance.update(raw.getBytes());
        return Base64.encodeBase64String(instance.sign());
    }

    public String getUTCTime() {
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatUTC.format(new Date());
    }

    public String calculateMD5(String contentToEncode) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(contentToEncode.getBytes());
        return Base64.encodeBase64String(digest.digest()).trim();
    }
    /* Parameters
     0: URI
     1: method
     2: Accept
     3: Content-Type
     4: Header
     5: Content
    
     */

    public Object senderRequest(Object... params) {
        try {
            String uri = params[0].toString();
            String method = params[1].toString();
            URL url = new URL(baseURL + uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            // Accept
            if (params.length > 2) {
                conn.setRequestProperty("Accept", params[2].toString());
            }
            // Content-Type
            if (params.length > 3) {
                conn.setRequestProperty("Content-Type", params[3].toString());
            }
            // Header
            if (params.length > 4 && null != params[4] && params[4] instanceof HashMap<?, ?>) {
                HashMap<String, String> header = (HashMap<String, String>) params[4];
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), URLEncoder.encode(entry.getValue(), URL_CODE));
                }
            }
            // Content
            if (params.length > 5 && null != params[5]) {
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.write(params[5].toString().getBytes());
                out.flush();
            }
            // use HTTP_ACCEPTED to indicate RESTful exception
            if (conn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                throw new UnsupportedOperationException(getResponseContent(conn));
            }
            // allow return void.
            if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT){
                return "";
            }
            // handle other exceptions
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception(conn.getResponseMessage() + "(" + conn.getResponseCode() + ")");
            }

            // Acquire response content
            return getResponseContent(conn);
        } catch (Exception e) {
            return e;
        }
    }

    private String getResponseContent(HttpURLConnection conn) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
