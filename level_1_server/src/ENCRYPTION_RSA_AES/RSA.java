package ENCRYPTION_RSA_AES;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class RSA {
    private PrivateKey privatekey;
    private PublicKey publicKey;

    public RSA(){

    }
    public void generateKeyPair(){
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privatekey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (Exception ignored) {}
    }

    /**
     *
     * @param data the message byte array to change into a string after encryption
     * @return the string format of the message
     */
    public String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     *
     * @param data the string format of the message before encryption
     * @return the byte format of the message
     */
    public byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

    /**
     *
     * @param message is the string format
     * @return the string format of the encrypted message
     * @throws Exception
     */
    public String encryptRSA(String message, PublicKey otherClientsPublicKey) throws Exception{
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,otherClientsPublicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    /**
     *
     * @param encryptedMessage the string format of the encrypted message
     * @return the string format of the decrypted message
     * @throws Exception
     */
    public String decryptRSA(String encryptedMessage,PrivateKey yourPrivateKey) throws Exception{
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,yourPrivateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage,"UTF8");
    }

    /**
     * Method to convert a key into a string
     * @param publicKey the key to converted into a string
     * @return string format of the key!!!
     * @throws NoSuchAlgorithmException thrown when algorithm doesnt exist
     */
    public String convertSecretKeyToStringRSA(PublicKey publicKey) throws NoSuchAlgorithmException {
        byte[] rawData = publicKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }
    /**
     * Method to change a string that was once a key into a secret key again!
     * @param encodedKey the string to change back to a key
     * @return the key version of the string
     */

    public SecretKey convertStringToSecretKeytoRSA(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "RSA");
        return originalKey;
    }

    public PrivateKey getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(PrivateKey privatekey) {
        this.privatekey = privatekey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
