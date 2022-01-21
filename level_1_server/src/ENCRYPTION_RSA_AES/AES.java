package ENCRYPTION_RSA_AES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AES {
    private SecretKey sessionKey;
    private final int KEY_SIZE = 128;
    private final int T_LEN = 128;
    private Cipher encryptionCipher;

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
     * @throws Exception
     */
    public String encryptAES(String message, SecretKey yourSessionKey) throws Exception {
        byte[] messageInBytes = message.getBytes();
        encryptionCipher = Cipher.getInstance("AES");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, yourSessionKey);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    /**
     * Method to decrypt the string format of the encrypted message
     * @param encryptedMessage the string format of th encrypted message
     * @return the string format of the decrypted value
     * @throws Exception when algorithm does not exist
     */
    public String decryptAES(String encryptedMessage,SecretKey yourSessionKey) throws Exception {
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, encryptionCipher.getIV());
        decryptionCipher.init(Cipher.DECRYPT_MODE, yourSessionKey, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
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
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    public SecretKey getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(SecretKey sessionKey) {
        this.sessionKey = sessionKey;
    }
}
