import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        ClientChat chat = new ClientChat(new Socket("127.0.0.1",1337));

        chat.startTheChat();

        String menu = "1. Connect to the server with username\n2. Send a broadcast message\n" +
                "3. Send a private message\n4.Send a message to a group\n5. Authenticate yourself" +
                "\n6. Create a group\n7. Join a group\n8. Exit group\n9. List all clients\n10. List all groups\n" +
                "11. Send File to user\n20. See clients who tries to send you a file\n21. Create password\n0. Disconnect from the server\n";
        int choice;

        do {

            //The menu
            System.out.println(menu);
            Scanner scanner = new Scanner(System.in);

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Enter username:");
                    scanner = new Scanner(System.in);
                    String username = scanner.nextLine();
                    chat.enterUsername(username);
                    menu = menu.replace("1. Connect to the server with username", "");
                    break;

                case 2:
                    System.out.println("Enter Broadcast message:");
                    scanner = new Scanner(System.in);
                    String broadMessage = scanner.nextLine();
                    chat.sendBroadcastMessage(broadMessage);
                    break;

                case 3:
                    System.out.println("Enter username to send the message:");
                    scanner = new Scanner(System.in);
                    String usernameToSendPrivateMessage = scanner.nextLine();
                    System.out.println("Enter the message:");
                    String message = scanner.nextLine();
                    chat.sendPrivateMessage(usernameToSendPrivateMessage, message);
                    break;

                case 4:
                    System.out.println("Enter group name to send a message to:");
                    scanner = new Scanner(System.in);
                    String groupName = scanner.nextLine();
                    System.out.println("Enter Broadcast message to group:");
                    String groupMessage = scanner.nextLine();
                    chat.sendMessageToGroup(groupName, groupMessage);
                    break;

                case 5:
                    System.out.println("Enter your password for authentication:");
                    scanner = new Scanner(System.in);
                    String password = scanner.nextLine();
                    chat.authenticate(password);
                    break;

                case 6:
                    System.out.println("Enter name of the group you want to create:");
                    scanner = new Scanner(System.in);
                    String groupNameToCreate = scanner.nextLine();
                    chat.createGroup(groupNameToCreate);
                    break;

                case 7:
                    System.out.println("Enter name of the group you want to join:");
                    scanner = new Scanner(System.in);
                    String groupNameToJoin = scanner.nextLine();
                    chat.joinGroup(groupNameToJoin);
                    break;

                case 8:
                    System.out.println("Enter name of the group you want to exit:");
                    scanner = new Scanner(System.in);
                    String groupNameToExit = scanner.nextLine();
                    chat.exitGroup(groupNameToExit);
                    break;

                case 9:
                    System.out.println("Listing all clients");
                    break;

                case 10:
                    chat.listAllGroups();
                    break;

                case 11:
                    System.out.println("Enter the name of the user to send the file:");
                    scanner = new Scanner(System.in);
                    String usernameToSendFileTo = scanner.nextLine();
                    System.out.println("Enter the absolute path of the file:");
                    scanner = new Scanner(System.in);
                    String filePath = scanner.nextLine();
                    chat.sendFileAcknowledgement(usernameToSendFileTo,filePath);
                    break;

                case 20:
                    if (chat.getUsernamesRequestingAck().size() != 0) {
                        System.out.println(chat.getUsernamesRequestingAck());
                        scanner = new Scanner(System.in);
                        System.out.println("\nChoose username from list:");
                        String usernameToAcceptOrDecline = scanner.nextLine();
                        System.out.println("Do you want to accept or decline? (a/d)");
                        String decision = scanner.nextLine();
                        if (chat.getUsernamesRequestingAck().containsKey(usernameToAcceptOrDecline)) {
                            switch (decision) {
                                case "a":
                                    chat.acceptAcknowledgement(usernameToAcceptOrDecline);
                                    System.out.println("You accepted the acknowledgement for " + usernameToAcceptOrDecline);
                                    break;

                                case "d":
                                    chat.declineAcknowledgement(usernameToAcceptOrDecline);
                                    System.out.println("You declined the acknowledgement for " + usernameToAcceptOrDecline);
                                    break;

                                default:
                                    System.out.println("You made a wrong choice!");
                                    break;
                            }
                        }
                    }
                    break;

                case 21:
                    System.out.println("Enter new password:");
                    scanner = new Scanner(System.in);
                    String passwordNew = scanner.nextLine();
                    chat.createPassword(passwordNew);
                    break;

                case 0:
                    System.out.println("+++++++++++++++++++GOOD BYE+++++++++++++++++++");
                    chat.disconnect();
                    break;

                default:
                    System.out.println("Please, choose the number above!");
            }
        } while (choice != 0);
    }
}