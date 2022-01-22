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

}
