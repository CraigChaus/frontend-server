import java.io.*;
import java.lang.invoke.StringConcatFactory;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {


        //the socket that connects to the server
        Socket socket;

        socket = new Socket("127.0.0.1", 1337);

        //code to read from the server
        Thread readThread = new Thread(() -> {

            while (true) {
                InputStream inputStream = null;
                try {
                    inputStream = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    System.out.println(reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        readThread.start();

        //code to write to the server
        Thread writeThread = new Thread(() -> {

////////////////////////////////////////////////////////////////////////////////////////
            String[] command = new String[]{"CONN","BCST","PONG","MSG","QUIT"};
            String message = "";

            Scanner scanner = new Scanner(System.in);

            //The menu
            System.out.println("1. Connect to the server with username");
            System.out.println("2. Send a broadcast message");
            System.out.println("3. Send a pong");
            System.out.println("9. Disconnect from the server");

            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    System.out.println("Enter username");
                     scanner = new Scanner(System.in);
                    String username = scanner.nextLine();
                    message = command[0] +" "+ username;
                    break;
                case 2:
                    System.out.println("Enter Broadcast message");
                    scanner = new Scanner(System.in);
                    String broadMessage = scanner.nextLine();
                    message = command[1] +" "+ broadMessage;
                    break;
                case 3:
                    message = command[2];
                    break;
                case 9:
                    message = command[3];
                default:
                    //receive error from server
            }
////////////////////////////////////////////////////////////////////////////////////////////

            OutputStream outputStream = null;
                try {
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PrintWriter writer = new PrintWriter(outputStream);
                writer.println(message);

                writer.flush();

        });
        writeThread.start();


    }
}