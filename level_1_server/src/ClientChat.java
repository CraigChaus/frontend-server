import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*

    Notes from teacher
1. Make the thread starting easier. Don't have startChat method, but extend ClientChat class from Thread class.
2. Simplify if else block by parsing the received message

*/
public class ClientChat {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private PrintWriter writer;

    private final String[] commands = new String[]{"CONN","BCST","MSG","QUIT", "GRP CRT", "GRP JOIN", "GRP LST", "GRP EXIT"};

    public ClientChat(Socket socket){
        this.socket = socket;
    }

    public void startTheChat() {
        try {
            outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream);

            MessageHandler messageHandlerThread = new MessageHandler(socket);
            messageHandlerThread.start();
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

        if(validationPassed){

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
}
