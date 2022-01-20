import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private FileHandler fileHandler;

    // Key: Username    Value: File path
    private HashMap<String,String> usernamesRequestingAck;

    private final String[] commands = new String[]{"CONN","BCST","MSG","QUIT","GRP CRT", "GRP JOIN", "GRP LST", "GRP EXIT"};

    public ClientChat(Socket messageSocket, Socket fileSocket){
        this.messageSocket = messageSocket;
        this.fileSocket = fileSocket;
        this.usernamesRequestingAck = new HashMap<>();
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
}
