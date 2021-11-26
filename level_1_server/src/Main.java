import java.io.*;
import java.net.ContentHandler;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Chat chat = new Chat(new Socket("127.0.0.1",1337));

        chat.startTheChat();

        String menu = "1. Connect to the server with username\n2. Send a broadcast message\n9. Disconnect from the server";
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
                case 9:
                    System.out.println("+++++++++++++++++++GOOD BYE+++++++++++++++++++");
                    chat.disconnect();

                default:
                    System.out.println("Please, choose the number above!");
            }
        }while (choice!=9);
    }
}