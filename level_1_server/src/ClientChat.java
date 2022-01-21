import ENCRYPTION_RSA_AES.AES;
import ENCRYPTION_RSA_AES.RSA;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*

    Notes from teacher
1. Make the thread starting easier. Don't have startChat method, but extend ClientChat class from Thread class.
2. Simplify if else block by parsing the received message

*/
public class ClientChat {
    private final Socket messageSocket;
    private final Socket fileSocket;
    private OutputStream outputStream;
    private static PrintWriter writer;
    private boolean isSessionKeyGrasped;

    private PrivateKey privatekey;
    private PublicKey publicKey;
    private SecretKey sessionKey;

    private RSA rsa;
    private AES aes;

    private FileHandler fileHandler;

    // Key: Username    Value: File path
    private HashMap<String,String> usernamesRequestingAck;

    private final String[] commands = new String[]{"CONN","BCST","MSG","QUIT","GRP CRT", "GRP JOIN", "GRP LST", "GRP EXIT"};

    public ClientChat(Socket messageSocket, Socket fileSocket){
        this.messageSocket = messageSocket;
        this.fileSocket = fileSocket;
        this.usernamesRequestingAck = new HashMap<>();
       this.isSessionKeyGrasped = false;
       this.publicKey = null;
       this.privatekey = null;
       this.sessionKey = null;
    }

    public void startTheChat() {
        try {
            outputStream = messageSocket.getOutputStream();
            writer = new PrintWriter(outputStream);

            MessageHandler messageHandlerThread = new MessageHandler(messageSocket, this);
            messageHandlerThread.start();

            fileHandler = new FileHandler(fileSocket);
            fileHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void enterUsername(String username){
        boolean validationPassed = validateNamesAndMessagesByCommands(username) && validateNamesBySpecialCharacters(username);

        if(!validationPassed) {
            System.out.println("Use only letters and numbers in your username. Don't use commands!");
        } else {
            String message = commands[0] + " " + username;

            writer.println(message);

            writer.flush();
        }

    }

    public void sendBroadcastMessage(String clientMessage){
        boolean validationPassed = validateNamesAndMessagesByCommands(clientMessage);

        if(!validationPassed){

            System.out.println("Invalid message");

        } else {

            String message = commands[1] + " " + clientMessage;
            writer.println(message);

            writer.flush();
        }
    }

    public void sendPrivateMessage(String username, String message) {
        String messageForServer = "PMSG " + username + " " + message;
        writer.println(messageForServer);
        writer.flush();
    }

    public void createPassword(String password) {
        writer.println("PASS " + password);
        writer.flush();
    }

    public void authenticate(String password) {
        writer.println("AUTH " + password);
        writer.flush();
    }

    public void createGroup(String groupName) {
        boolean validationPassed = validateNamesAndMessagesByCommands(groupName) && validateNamesBySpecialCharacters(groupName);

        if (validationPassed) {
            String createGroupMessage = "GRP CRT " + groupName;
            writer.println(createGroupMessage);
            writer.flush();
        } else {
            System.out.println("Group name cannot contain commands!");
        }

    }

    public void joinGroup(String groupName) {
        boolean validationPassed = validateNamesAndMessagesByCommands(groupName);

        if (validationPassed) {
            String joinGroupMessage = "GRP JOIN " + groupName;
            writer.println(joinGroupMessage);
            writer.flush();
        } else {
            System.out.println("Group name cannot contain commands!");
        }

    }

    public void sendMessageToGroup(String groupName, String message) {
        boolean validationPassed = validateNamesAndMessagesByCommands(groupName) && validateNamesAndMessagesByCommands(message);

        if (validationPassed) {
            String groupBroadcastMessage = "GRP BCST " + groupName + " " + message;
            writer.println(groupBroadcastMessage);
            writer.flush();
        } else {
            System.out.println("Group name and message cannot contain commands!");
        }
    }

    public void listAllGroups() {
        writer.println("GRP LST");
        writer.flush();
    }

    public void exitGroup(String groupName) {
        boolean validationPassed = validateNamesAndMessagesByCommands(groupName);

        if (validationPassed) {
            String message = "GRP EXIT " + groupName;
            writer.println(message);
            writer.flush();
        } else {
            System.out.println("Group name cannot contain commands!");
        }

    }

    public void sendFileAcknowledgement(String receiver,String filePath) {
        System.out.println("Sending FIL ACK");

            writer.println("FIL ACK " + receiver + " "+ filePath);
            writer.flush();
    }

    public void acceptAcknowledgement(String username) {
        String filepath = usernamesRequestingAck.get(username);
        writer.println("ACC " + username+ " "+ filepath);
        writer.flush();
    }

    public void declineAcknowledgement(String username) {
        writer.println("DEC " + username+ " "+ usernamesRequestingAck.get(username));
        writer.flush();
    }

    public void sendFile(String filepath, String receiver) {
        fileHandler.sendFile(filepath, receiver);
    }

    public void disconnect(){
        String message = commands[3];

        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(message);

        writer.flush();
    }

    private boolean validateNamesAndMessagesByCommands(String message) {
        boolean isMessageOk = true;

        for (String command: commands) {
            if (message.contains(command)) {
                isMessageOk = false;
            }
        }

        return isMessageOk;
    }

    private boolean validateNamesBySpecialCharacters(String name) {
        Pattern pattern = Pattern.compile("[- !@#$%^&*()+=|/?.>,<`~]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        boolean matchFound = matcher.find();

        return !matchFound;
    }

    public void addUsernameRequestingAcknowledgement(String name,String filePath) {
        usernamesRequestingAck.put(name,filePath);
    }

    public HashMap<String,String > getUsernamesRequestingAck() {
        return usernamesRequestingAck;
    }

    public void processTheAck(String message) {
        System.out.println("User " + message.split(" ")[1] + " wants to send you the file. " +
                "Do you accept it? (y/n)");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        String senderUsername = message.split(" ")[1];

        if (answer.equalsIgnoreCase("y")) {
            writer.println("ACC " + senderUsername);
            System.out.println("Accepted");
        } else {
            writer.println("DEC " + senderUsername);
            System.out.println("Declined");
        }
        writer.flush();
    }
    public String getChecksum(String filepath) throws IOException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        // DigestInputStream is better, but you also can hash file like this.
        try (InputStream fis = new FileInputStream(filepath)) {
            byte[] buffer = new byte[1024];
            int readNo;
            while ((readNo = fis.read(buffer)) != -1) {
                md.update(buffer, 0, readNo);
            }
        }
        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    //All these methods only deal with encryption

    /**
     * Method to send the RSA public key to the receiver
     * @param usernameReceiver username of the receiver
     */
    public void sendPublicKey(String usernameReceiver) throws NoSuchAlgorithmException {
        //First generate the keys
        rsa = new RSA();
        rsa.generateKeyPair();

        //Store public and private keys
        this.publicKey = rsa.getPublicKey();
        this.privatekey = rsa.getPrivatekey();

        //Change the RSA public key into a string before sending
        String publicKeyStringFormat = rsa.convertSecretKeyToStringRSA(publicKey);

        // Finally, send the command!
        writer.println("ENC " + usernameReceiver + " "+ publicKeyStringFormat);
        writer.flush();
    }

    /**
     * Method to generate the aes session key then encrypt it before sending it to other client
     * @param usernameSender name of the sender
     * @param sendersPublicKey the public key of the sender
     * @throws Exception thrown when algorithm does not exist
     */
      public void generateSessionKeyThenEncrypt(String usernameSender,String sendersPublicKey) throws Exception {
            aes = new AES();

          try {
              aes.generateKey();
          } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
          }

          //obtain the secret key
          this.sessionKey = aes.getSessionKey();
          this.isSessionKeyGrasped = true;

          //change the session key into a string format before encrypting
            String sessionKeyString = aes.convertSecretKeyToStringAES(sessionKey);

         //Change the senders public key from string to public key
          //before it can be used to encrypt the session key
          PublicKey otherClientsPublicKey = (PublicKey) rsa.convertStringToSecretKeytoRSA(sendersPublicKey);

          //use the decoded public key to encrypt the session key
          String encryptedSessionKey = rsa.encryptRSA(sessionKeyString,otherClientsPublicKey);

          //Finally, send back the encrypted key

          writer.println("ENCSK " + usernameSender + " "+ encryptedSessionKey);
          writer.flush();

          System.out.println("Session key generated, Session now secure");
      }

    /**
     * Method to decrypt the session key obtained to begin the session
     * @param sessionkeyToDecrypt the sssion key to decrypt
     * @throws Exception thrown when the algorithm does not exist
     */
      public void decryptAndObtainSessionKey(String sessionkeyToDecrypt) throws Exception {

          //first decrypt the session key
          String decryptedSessionKey = rsa.decryptRSA(sessionkeyToDecrypt,privatekey);

          //change the decrypted session key into a type of Secret key;
          this.sessionKey = aes.convertStringToSecretKeytoAES(decryptedSessionKey);

          //let client know it has the key so that the session can begin
          this.isSessionKeyGrasped = true;
          System.out.println("Session key generated, Session now secure");
      }

    /**
     * Method to encrypt the message then sending to the receiving client
     * @param usernameReceiver name of receiver
     * @param messageToEncrypt message to be encrypted
     * @throws Exception thrown when algorithm does not exist
     */
      public void encryptAESMessageThenSend(String usernameReceiver,String messageToEncrypt) throws Exception {
          aes = new AES();

          //encrypt the message
          String encryptedMessage = aes.encryptAES(messageToEncrypt,sessionKey);
          //TODO: for testing purposes only, please delete when working perfectly
          System.out.println(encryptedMessage);

          //send the message
          writer.println("ENCM "+ usernameReceiver+ " "+ encryptedMessage);
          writer.flush();
          System.out.println("Secure message sent");
      }

    /**
     * Method to retrieve the message, decrypt it then display it
     * @param messageToDecrypt the message to decrypt
     * @return string format of message
     * @throws Exception thrown when algorithm does not exist
     */
      public String decryptMessageThenDisplayIt(String messageToDecrypt) throws Exception {
          aes = new AES();

          //decrypt the message then return it
          return aes.decryptAES(messageToDecrypt,sessionKey);
      }

    public boolean isSessionKeyGrasped() {
        return isSessionKeyGrasped;
    }

    public void setSessionKeyGrasped(boolean sessionKeyGrasped) {
        isSessionKeyGrasped = sessionKeyGrasped;
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

    public SecretKey getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(SecretKey sessionKey) {
        this.sessionKey = sessionKey;
    }
}
