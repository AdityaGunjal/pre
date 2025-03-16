/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;

/**
 *
 * @author Lenovo
 */
public class DRIVE_Network {

    private FTPClient client;
    private FileInputStream fis;
    private boolean status;

    public DRIVE_Network() {
        client = new FTPClient();
    }

    public boolean upload(File file, String pack) {
        int maxRetries = 3;
        int retries = 0;

        while (retries < maxRetries) {
            try {
                System.out.println("Connecting to the FTP server...");
                client.connect("ftp.drivehq.com");
                System.out.println("Connected.");

                System.out.println("Logging in...");
                client.login("UserName", "Password");

                System.out.println("Login successful.");

                client.enterLocalPassiveMode();

                // Check if the file exists and is a regular file
                if (!file.exists() || !file.isFile()) {
                    System.out.println("File does not exist or is not a regular file.");
                    return false;
                }

                System.out.println("Uploading file: " + file.getAbsolutePath());
                fis = new FileInputStream(file);
                status = client.storeFile(pack + file.getName(), fis);

                if (status) {
                    System.out.println("File uploaded successfully.");
                } else {
                    System.out.println("File upload failed. Server response: " + client.getReplyString());
                }

                break; // Break out of the loop if upload is successful

            } catch (FTPConnectionClosedException e) {
                System.out.println("FTP Connection Closed Exception. Server response: " + client.getReplyString());

                // Retry connecting after a delay
                try {
                    Thread.sleep(5000); // 5 seconds delay
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                retries++;

            } catch (IOException e) {
                e.printStackTrace();
                break; // Break out of the loop if another IOException occurs
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (client.isConnected()) {
                        System.out.println("Logging out and disconnecting from the FTP server...");
                        client.logout();
                        client.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return status;
    }
}
