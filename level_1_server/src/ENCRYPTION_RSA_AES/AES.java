package ENCRYPTION_RSA_AES;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

public class AES {
    private SecretKey sessionKey;
    private final int KEY_SIZE = 128;

    public AES() {
    }

    public void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        sessionKey = generator.generateKey();
    }

    /**
     * Method to encrypt the message during session
     * @param message the message to encrypt
     * @return string of the message
     * @throws Exception when algorithm does not exist
     */
    public  String encryptAES(String algorithm, String message, SecretKey key) throws Exception{

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
    }

    /**
     * Method to decrypt the string format of the encrypted message
     * @param encryptedMessage the string format of th encrypted message
     * @return the string format of the decrypted value
     * @throws Exception when algorithm does not exist
     */
    public  String decryptAES(String algorithm, String encryptedMessage, SecretKey key) throws Exception{

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)));
    }

    /**
     * Method to convert from byte array to string
     * @param data byte array format of the encrypted message
     * @return string format of the same message
     */
    public String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Method to convert from string to byte array
     * @param data string format of the encrypted data
     * @return byte array format of the same message
     */
    public byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    /**
     * Method to convert a key into a string
     * @param secretKey the key to converted into a string
     * @return string format of the key!!!
     * @throws NoSuchAlgorithmException thrown when algorithm doesnt exist
     */
    public String convertSecretKeyToStringAES(SecretKey secretKey) throws NoSuchAlgorithmException {
        byte[] rawData = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    /**
     * Method to change a string that was once a key into a secret key again!
     * @param encodedKey the string to change back to a key
     * @return the key version of the string
     */
    public SecretKey convertStringToSecretKeytoAES(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, 16, "AES");
    }
    public IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public SecretKey getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(SecretKey sessionKey) {
        this.sessionKey = sessionKey;
    }
}
