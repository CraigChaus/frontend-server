import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        ClientChat chat = new ClientChat(new Socket("127.0.0.1",1337));

        chat.startTheChat();

        String menu = "1. Connect to the server with username\n2. Send a broadcast message\n3.Send a private message\n4.Send a message to a group\n5.Authenticate yourself\n6.Create a group\n7.Join a group\n8.Exit group\n9.List all clients\n10.List all groups\n0. Disconnect from the server";
        int choice;

        do {

            //The menu
            System.out.println(menu);

            Scanner scanner = new Scanner(System.in);
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Enter username");
                    scanner = new Scanner(System.in);
                    String username = scanner.nextLine();
                    chat.enterUsername(username);
                    menu = menu.replace("1. Connect to the server with username", "");
                    break;
                case 2:
                    System.out.println("Enter Broadcast message");
                    scanner = new Scanner(System.in);
                    String broadMessage = scanner.nextLine();
                    chat.sendBroadcastMessage(broadMessage);
                    break;
                case 3:
                  //  System.out.println("");
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    System.out.println("Listing all clients");
                    break;

                case 0:
                    System.out.println("+++++++++++++++++++GOOD BYE+++++++++++++++++++");
                    chat.disconnect();

                default:
                    System.out.println("Please, choose the number above!");
            }
        }while (choice!=0);
    }
}
