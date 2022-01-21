import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class FileHandler extends Thread {
    private String fileToReceive = "test.txt";
    private Socket socket;

    public FileHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                int fileLength = dataInputStream.readInt();
                System.out.println("Received file length: " + fileLength);

                if (fileLength > 0) {
                    byte[] fileBytes = new byte[fileLength];
                    dataInputStream.readFully(fileBytes,0, fileBytes.length);
                    System.out.println("Read file of length: " + fileBytes.length);
                    File receivedFile = new File("hello.txt");

                    FileOutputStream fileOutputStream = new FileOutputStream(receivedFile);

                    fileOutputStream.write(fileBytes);

                    System.out.println("Absolute path of received file: " + receivedFile.getAbsolutePath());
                    fileOutputStream.close();
                } else
                    System.out.println("Received file length is 0");

            } catch (IOException e) {
                try {
                    socket.close();
                    System.out.println("Socket closed");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public void sendFile(String filePath, String receiver) {

        try {
            File file = new File(filePath);

            if(!file.exists()||!file.isFile()){
                System.out.println("ERR File does not exist");
            }

            FileInputStream fileInputStream = new FileInputStream(file);

            OutputStream outputStream = socket.getOutputStream();

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            byte[] receiverBytes = receiver.getBytes();
            System.out.println("Receiver size: " + receiverBytes.length + "\nReceiver name to send bytes: " + Arrays.toString(receiverBytes));

            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);

            System.out.println("File size: " + (int) file.length() + "\nFile to send bytes: " + Arrays.toString(fileBytes));

            // Sending receiver name bytes length and the receiver name bytes themself
            dataOutputStream.writeInt(receiverBytes.length);
            dataOutputStream.write(receiverBytes);

            // Sending file size and then file bytes.
            dataOutputStream.writeInt(fileBytes.length);
            dataOutputStream.write(fileBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void sendFile(String filePath) throws IOException {
//        Socket s = new Socket(System.getProperty("localhost"),1338);
//
//        System.out.println("The file path: " + filePath);
//        File file = new File(filePath);
//
//        if(!file.exists()||!file.isFile()){
//            System.out.println("ERR File does not exist");
//        }
//
//        FileInputStream fileInputStream;
//        BufferedInputStream bufferedInputStream;
//        BufferedOutputStream bufferedOutputStream1;
//
//        byte[] buffer = new byte[8192];
//        try {
//            fileInputStream = new FileInputStream(file);
//            bufferedInputStream = new BufferedInputStream(fileInputStream);
//            bufferedOutputStream1 = new BufferedOutputStream(s.getOutputStream());
//            int count;
//            while ((count = bufferedInputStream.read(buffer)) > 0) {
//                bufferedOutputStream1.write(buffer, 0, count);
//            }
//            System.out.println("FILE SENT "+ filePath);
//            bufferedOutputStream1.close();
//            fileInputStream.close();
//            bufferedInputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
