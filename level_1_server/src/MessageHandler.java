import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MessageHandler extends Thread{

    private Socket socket;
    private PrintWriter writer;
    private ClientChat chat;

    public MessageHandler(Socket socket, ClientChat chat) {
        this.socket = socket;
        this.chat = chat;
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

                if (receivedMessage.equals("PING")) {
                    sendPong();
                } else {
                    System.out.println(processReceivedMessage(receivedMessage));
                }

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

                break;

            case "BCST":
                processedResponse = formatBroadcastMessage(message);
                break;

            case "GRP BCST":
                processedResponse = formatGroupMessage(message);
                break;

            case "PMSG":
                processedResponse = "You have got a private message:\n" + message.split(" ")[1] + ": " +
                        message.split(" ",3)[2];
                break;

            case "ACK":
                chat.addUsernameRequestingAcknowledgement(message.split(" ")[1]);
                processedResponse = "You have got new file transfer request!";
                break;

            default:
                processedResponse = message;
                break;
        }

        return processedResponse;

    }

    private String formatBroadcastMessage(String receivedServerMessage) {

        String broadcastMessageReceived = receivedServerMessage.replace("BCST ", "");
        String username = broadcastMessageReceived.split(" ")[0];
        String messageText = broadcastMessageReceived.split(" ", 2)[1];

        return username + ": " + messageText;
    }

    private String formatGroupMessage(String receivedServerMessage) {
        String broadcastMessageReceived = receivedServerMessage.replace("GRP BCST ", "");
        String groupName = broadcastMessageReceived.split(" ")[0];
        String username = broadcastMessageReceived.split(" ")[1];
        String messageText = broadcastMessageReceived.split(" ", 3)[2];

        String formattedMessage = "Received message from group " + groupName + ":\n" + username + ": " + messageText;
        return formattedMessage;
    }

    private void sendPong() {
        writer.println("PONG");
        writer.flush();
    }

//    private void processTheAck(String message) {
//        System.out.println("User " + message.split(" ")[1] + " wants to send you the file. " +
//                "Do you accept it? (y/n)");
//        Scanner scanner = new Scanner(System.in);
//        String answer = scanner.nextLine();
//        String senderUsername = message.split(" ")[1];
//
//        if (answer.equalsIgnoreCase("y")) {
//            writer.println("ACC " + senderUsername);
//            System.out.println("Accepted");
//        } else {
//            writer.println("DEC " + senderUsername);
//            System.out.println("Declined");
//        }
//        writer.flush();
//    }
}
