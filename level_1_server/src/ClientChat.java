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
    private BufferedReader serverReader;

    private final String[] commands = new String[]{"CONN","BCST","MSG","QUIT"};

    public ClientChat(Socket socket){
        this.socket = socket;
    }

    public void startTheChat() {

        MessageHandler messageHandlerThread = new MessageHandler(socket);
        messageHandlerThread.start();

        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendBroadcastMessage(String clientMessage){

        if((clientMessage.contains(commands[0])) ||
                (clientMessage.contains(commands[1])) ||
                (clientMessage.contains(commands[2])) ||
                (clientMessage.contains(commands[3]))){

            System.out.println("Invalid message");

        } else {

            String message = commands[1] + " " + clientMessage;
            PrintWriter writer = new PrintWriter(outputStream);
            writer.println(message);

            writer.flush();
        }
    }

    public void enterUsername(String username){
        Pattern pattern = Pattern.compile("[- !@#$%^&*()+=|/?.>,<`~]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(username);
        boolean matchFound = matcher.find();

        if(matchFound) {
            System.out.println("Use only letters and numbers in your username");
        } else {
            String message = commands[0] + " " + username;

            PrintWriter writer = new PrintWriter(outputStream);
            writer.println(message);

            writer.flush();
        }

    }

    public void sendPong() {
        PrintWriter writer = new PrintWriter(outputStream);
        writer.println("PONG");
        writer.flush();
    }

    public String formatBroadcastMessage(String receivedServerMessage) {

        String broadcastMessageReceived = receivedServerMessage.replace("BCST ", "");
        String username = broadcastMessageReceived.split(" ")[0];
        String messageText = broadcastMessageReceived.split(" ", 2)[1];

        return username + ": " + messageText;
    }

    public void disconnect(){
        String message = commands[3];

        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(message);

        writer.flush();
    }
}
