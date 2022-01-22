import fileTransfer.ReceiveFileRunnable;
import fileTransfer.SendFileRunnable;

import java.io.*;
import java.net.Socket;

public class MessageHandler extends Thread{

    private Socket socket;
    private PrintWriter writer;
    private ClientChat chat;
    private Socket fileSocket;

    long primeKey = 0;
    long rootKey = 0;
    long otherClientsPublicValue = 0;

    public MessageHandler(Socket socket, Socket fileSocket, ClientChat chat) {
        this.socket = socket;
        this.chat = chat;
        this.fileSocket = fileSocket;
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
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public String processReceivedMessage(String message) throws Exception {
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
                        socket.close();
                        fileSocket.close();
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

            case "INC":
                // Start receiving the file
                System.out.println("Downloading file " + message.split(" ")[3] + "...");
                new Thread(new ReceiveFileRunnable(fileSocket, message.split(" ")[2], message.split(" ")[3])).start();

                break;

                //cases for encryption
            case "ENC":
                chat.generateSessionKeyThenEncrypt(message.split(" ")[1],message.split(" ")[2]);
                break;
            case "ENCSK":
                chat.decryptAndObtainSessionKey(message.split(" ")[2]);
                break;

            case "ENCM":
                processedResponse = "You got a new secure message\n"+  message.split(" ")[1] + ": "+ chat.decryptMessageThenDisplayIt(message.split(" ")[2]);
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
        String[] filePathParts = filePath.split("/");
        String filename = filePathParts[filePathParts.length-1];
        writer.println("FIL SND "+ username + " "+ checkSum + " " + filename);
        writer.flush();

        new Thread(new SendFileRunnable(fileSocket, filePath)).start();
    }

    private void sendPong() {
        writer.println("PONG");
        writer.flush();
    }

}
