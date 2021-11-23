import java.io.*;
import java.net.ContentHandler;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Chat chat = new Chat(new Socket("127.0.0.1",1337));

        chat.startTheChat();


            Scanner scanner = new Scanner(System.in);

            //The menu
            System.out.println("1. Connect to the server with username");
            System.out.println("2. Send a broadcast message");
            System.out.println("9. Disconnect from the server");

            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    System.out.println("Enter username");
                    scanner = new Scanner(System.in);
                    String username = scanner.nextLine();
                    chat.enterUsername(username);
                    break;
                case 2:
                    System.out.println("Enter Broadcast message");
                    scanner = new Scanner(System.in);
                    String broadMessage = scanner.nextLine();
                    chat.sendBroadcastMessage(broadMessage);
                    break;
                case 9:
                    chat.disconnect();
                default:
                    //receive error from server
            }

    }
}