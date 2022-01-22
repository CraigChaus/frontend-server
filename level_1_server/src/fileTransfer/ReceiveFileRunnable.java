package fileTransfer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ReceiveFileRunnable implements Runnable{

    private Socket fileSocket;
    private String filename;
    private String checksum;

    public ReceiveFileRunnable(Socket fileSocket, String checksum, String filename) {
        this.fileSocket = fileSocket;
        this.filename = filename;
        this.checksum = checksum;
    }

    @Override
    public void run() {
        if (!fileSocket.isClosed()) {
            try {
                DataInputStream dataInputStream = new DataInputStream(fileSocket.getInputStream());

                // Reading fileBytes length from input stream
                int fileLength = dataInputStream.readInt();

                // Creating byte array of the fileLength size and reading bytes from input stream
                // (number of read bytes has to be the same as fileLength)
                byte[] fileBytes = new byte[fileLength];
                dataInputStream.readFully(fileBytes,0, fileBytes.length);

                // Check if the checksum is the same as expected
                if (compareChecksum(checksum, fileBytes)) {
                    File receivedFile = new File(filename);

                    FileOutputStream fileOutputStream = new FileOutputStream(receivedFile);
                    fileOutputStream.write(fileBytes);
                    fileOutputStream.close();

                    System.out.println("Absolute path of file: " + receivedFile.getAbsolutePath() + "\nWait for some time for file to appear");

                } else {
                    // If checksums are not the same
                    System.out.println("ALERT: The checksum check has failed! Received file has different checksum than expected! Download will not be continued!");
                }

            } catch (IOException e) {
                try {
                    fileSocket.close();
                    System.out.println("Socket closed");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public boolean compareChecksum(String expectedChecksum, byte[] fileBytes) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(fileBytes,0, fileBytes.length);

            StringBuilder result = new StringBuilder();
            for (byte b : md.digest()) {
                result.append(String.format("%02x", b));
            }

            return result.toString().equals(expectedChecksum);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return false;

    }
}
