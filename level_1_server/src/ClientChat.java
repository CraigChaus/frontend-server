import java.io.*;
import java.net.Socket;


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

        Thread readThread = new Thread(() -> {
            while (true) {
                try {
                    inputStream = socket.getInputStream();
                    serverReader = new BufferedReader(new InputStreamReader(inputStream));

                    String receivedServerMessage = serverReader.readLine();

                    // Simple information for client about response of their operations
                    if (receivedServerMessage.equals("PING")) {

                        sendPong();

                    } else if (receivedServerMessage.startsWith("OK BCST")) {

                        System.out.println("Message successfully broadcasted");

                    } else if (receivedServerMessage.startsWith("BCST")) {

                        String messageToShow = formatBroadcastMessage(receivedServerMessage);

                        System.out.println(messageToShow);

                    } else if (receivedServerMessage.startsWith("OK")) {

                        System.out.println("You are successfully logged in as " + receivedServerMessage.substring(3));

                    } else if (receivedServerMessage.startsWith("DCSN")) {

                        System.out.println("You are no longer connected to the server");


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        readThread.start();

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

    public void enterUsername(String clientMessage){

        String message = commands[0] + " " + clientMessage;

        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(message);

        writer.flush();
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
