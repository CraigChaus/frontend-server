import java.io.*;
import java.net.Socket;

public class Chat {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader serverReader;

    private final String[] commands = new String[]{"CONN","BCST","MSG","QUIT"};

    public Chat(Socket socket){
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

//                        String broadcastReceived = receivedServerMessage.replace("BCST ", "");
//                        String username = broadcastReceived.split(" ")[0];
//                        String messageItSelf = broadcastReceived.split(" ", 2)[1];
//
//                        System.out.println(username + ": " + messageItSelf);

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

        Thread writeThread = new Thread(() -> {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writeThread.start();
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
