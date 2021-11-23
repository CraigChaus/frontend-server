import java.io.*;
import java.net.Socket;

public class Chat {
     private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private BufferedReader serverReader;

    private final String[] commands = new String[]{"CONN","BCST","MSG","QUIT"};

    public Chat(Socket socket) throws IOException {
        this.socket = socket;
    }

    public void startTheChat() {

        Thread readThread = new Thread(() -> {
            while (true) {
                try {
                    inputStream = socket.getInputStream();
                    serverReader = new BufferedReader(new InputStreamReader(inputStream));

                    String receivedMessage = serverReader.readLine();

                    if(receivedMessage.equals("PING")) {
                        PrintWriter writer = new PrintWriter(outputStream);
                        writer.println("PONG");
                        writer.flush();
                    }else {
                        System.out.println(receivedMessage);
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
//                while (serverReader.readLine().equals("PING")){
//                    PrintWriter writer = new PrintWriter(outputStream);
//                    writer.println("PONG");
//                    writer.flush();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        writeThread.start();
    }

    public void sendBroadcastMessage(String clientMessage){

        String message = commands[1] + " " + clientMessage;

        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(message);

        writer.flush();
    }

    public void enterUsername(String clientMessage){

        String message = commands[0] + " " + clientMessage;

        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(message);

        writer.flush();
    }

    public void disconnect(){
        String message = commands[3];

        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(message);

        writer.flush();
    }




}
