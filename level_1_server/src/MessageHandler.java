import java.io.*;
import java.net.Socket;

public class MessageHandler extends Thread{

    private Socket socket;
    private PrintWriter writer;

    public MessageHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                writer = new PrintWriter(outputStream);

                String receivedMessage = reader.readLine();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String processReceivedMessage(String message) {
        String firstCommand = message.split(" ")[0];
        //The first element of the array is command, the second is message
        String processedResponse = "";

        switch (firstCommand) {
            case "OK":
                String secondCommand = message.split(" ")[1];

                switch (secondCommand) {
                    case "CONN":
                        String username = message.split(" ")[2];
                        processedResponse = "You are successfully logged in as " + username;
                        break;

                    case "PASS":
                        processedResponse = "You password has been registered!";
                        break;

                    case "AUTH":
                        processedResponse = "You are successfully authenticated!";
                        break;

                    case "LST":
                        processedResponse = "List of connected clients: ";
                        String[] users = message.split(" ", 3)[2].split(",");

                        for (String user: users) {
                            String isAuthenticated = user.split(" ")[0];
                            String name = user.split(" ")[1];

                            if (Integer.parseInt(isAuthenticated) == 1) {
                                processedResponse += "\n*" + name;
                            } else {
                                processedResponse += "\n" + name;
                            }
                        }

                        break;

                    case "CRT":
                        processedResponse = "Group has been created!";
                        break;

                    case "GRPLST":
                        String groups = message.split(" ", 3)[2].replace(",", ", ");
                        processedResponse = "List of all groups: " + groups;
                        break;

                    case "JOIN":
                        processedResponse = "You have joined group " + message.split(" ")[2];
                        break;

                    case "EXIT":
                        processedResponse = "You left the group";
                        break;

                    case "PMSG":
                        processedResponse = "Your private message is sent!";
                        break;

                    case "QUIT":
                        processedResponse = "GOODBYE!";
                        break;

                }
        }

        return processedResponse;

    }
}
