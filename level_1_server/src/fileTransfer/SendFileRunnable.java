package fileTransfer;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class SendFileRunnable implements Runnable{

    private Socket fileSocket;
    private String filePath;

    public SendFileRunnable(Socket fileSocket, String filePath) {
        this.fileSocket = fileSocket;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            File file = new File(filePath);

            if(!file.exists()||!file.isFile()){
                System.out.println("ERR File does not exist");
            } else {
                FileInputStream fileInputStream = new FileInputStream(file);

                OutputStream outputStream = fileSocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                // File bytes
                byte[] fileBytes = new byte[(int) file.length()];
                fileInputStream.read(fileBytes);
                System.out.println("File size: " + (int) file.length() + "\nFile to send bytes: " + Arrays.toString(fileBytes));

                // Sending file size and then file bytes.
                dataOutputStream.writeInt(fileBytes.length);
                dataOutputStream.write(fileBytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
