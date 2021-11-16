import java.io.*;
import java.net.Socket;

public class Client{

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;

    public void startConnection() {
        {
            try {
                socket = new Socket("127.0.0.1", 1337);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFromServer() {
        {
            try {
                inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                System.out.println(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage() {
        {
            try {
                outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream);
                writer.println("Hello world");

                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        Client client = new Client();
            client.startConnection();
            client.readFromServer();
            client.sendMessage();
        while (true) {
        }
    }
}

