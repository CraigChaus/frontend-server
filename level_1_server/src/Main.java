import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        ClientChat chat = new ClientChat(new Socket("127.0.0.1",1337));

        chat.startTheChat();

        String menu = "1. Connect to the server with username\n2. Send a broadcast message\n" +
                "3.Send a private message\n4.Send a message to a group\n5.Authenticate yourself" +
                "\n6.Create a group\n7.Join a group\n8.Exit group\n9.List all clients\n10.List all groups\n" +
                "11.Send File to user" + "0. Disconnect from the server";
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
                    chat.sendFileAcknowledgement(usernameToSendFileTo);
                    break;

                case 0:
                    System.out.println("+++++++++++++++++++GOOD BYE+++++++++++++++++++");
                    chat.disconnect();
                    break;

                default:
                    System.out.println("Please, choose the number above!");
            }
        }while (choice!=0);
    }
}
