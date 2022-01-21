import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class MessageHandler extends Thread{

    private Socket socket;
    private PrintWriter writer;
    private ClientChat chat;

    long primeKey = 0;
    long rootKey = 0;
    long otherClientsPublicValue = 0;

    public MessageHandler(Socket socket, ClientChat chat) {
        this.socket = socket;
        this.chat = chat;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
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
                try {
                    System.out.println("Server stopped working!");
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
    }

    public String processReceivedMessage(String message) throws IOException, NoSuchAlgorithmException {
        String firstCommand = message.split(" ")[0];
        //The first element of the array is command, the second is message, third could be a file in the case of ACC/DEC
        String processedResponse = "";

        switch (firstCommand) {
            case "OK":
                String secondCommand = message.split(" ")[1];

                switch (secondCommand) {
                    case "CONN":
                        String username = message.split(" ")[2];
                        processedResponse = "You are successfully logged in as " + username;
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

                    case "PASS":
                        processedResponse = "Password successfully created!";
                        break;

                    case "QUIT":
                        processedResponse = "GOODBYE!";
                        break;

                }

                break;

            case "BCST":
                processedResponse = formatBroadcastMessage(message);
                break;

            case "GRP":
                switch (message.split(" ")[1]) {
                    case "BCST":
                        processedResponse = formatGroupMessage(message);
                        break;
                }
                break;

            case "PMSG":
                processedResponse = "You have got a private message:\n" + message.split(" ")[1] + ": " +
                        message.split(" ",3)[2];
                break;

            case "ACK":
                chat.addUsernameRequestingAcknowledgement(message.split(" ")[1],message.split(" ")[2]);
                processedResponse = "You have got new file transfer request!";
                break;

            case "FIL":
                secondCommand = message.split(" ")[1];

                switch (secondCommand) {
                    case "ACC":
                        System.out.println("User " + message.split(" ")[2] + " accepted your file transfer request for file: "+  message.split(" ")[3] );
                        System.out.println("Now sending the file");
                        startLoadingTheFile(message.split(" ")[2],chat.getChecksum(message.split(" ")[3]), message.split(" ")[3]);

                        break;

                    case "DEC":
                        System.out.println("User " + message.split(" ")[2] + " declined your file transfer request!");
                        System.out.println("File sending cannot be done");

                        break;
                }

                break;

                //cases for encryption
            case "ENCR":
                System.out.println("received public keys "+message.split(" ")[1]+" and "+message.split(" ")[2]);
                System.out.println();

                 primeKey = Long.parseLong(message.split(" ")[1]);
                 rootKey = Long.parseLong(message.split(" ")[2]);

                String username = message.split(" ")[3];
                long publicValue = chat.calculatePublicValue(primeKey,rootKey,chat.getPrivateKey());
                sendPublicValue(username,publicValue);
                break;

            case "PVE":
                System.out.println("received public value "+message.split(" ")[2]+ " from "+ message.split(" ")[1]);
                System.out.println();
                System.out.println("Calculating session key");

                otherClientsPublicValue = Long.parseLong(message.split(" ")[2]);

                System.out.println("Your session key is :" + chat.calculateSymmetricKey(primeKey,chat.getPrivateKey(),otherClientsPublicValue));
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

    private void startLoadingTheFile(String username,String checkSum, String filePath) {
        writer.println("FIL SND "+ username + " "+ checkSum + " " + filePath);
        writer.flush();

        chat.sendFile(filePath, username);
    }

    private void sendPublicValue(String username,long publicValue){
        writer.println("PV " + " "+ username + " "+ publicValue);
        writer.flush();
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
