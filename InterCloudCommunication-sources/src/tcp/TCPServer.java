/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp;

import client.OpenNebulaClient;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.DataCenter;
import models.Disk;
import models.Image;
import models.Puppeteer;
import models.TemplateModel;
import org.opennebula.client.OneResponse;
import services.implementations.ImageServiceImpl;
import services.implementations.InterCloudMigrationServiceImpl;
import services.interfaces.IImageService;
import services.interfaces.IInterCloudMigrationService;
import utils.config.GeneralConfigurationManager;

/**
 *
 * @author oneadmin
 */
public class TCPServer {

    static String clientSentence;
    static String capitalizedSentence;

    public static void main(String... args) {

        listen();
    }

    public static void listen() {
        TemplateModel tm = null;
        boolean isListening = true;
        while (isListening) {
            ServerSocket welcomeSocket;
            try {
                System.out.println("Listening on port 6789");
                welcomeSocket = new ServerSocket(6789);
                Socket connectionSocket = welcomeSocket.accept();
                if (connectionSocket.isConnected()) {
                    System.out.println(
                            "Accepted a connection from "
                            + connectionSocket.getRemoteSocketAddress().toString());
                    InputStream is = connectionSocket.getInputStream();
                    ObjectInputStream obj = new ObjectInputStream(is);
                    try {
                        tm = (TemplateModel) obj.readObject();
                        for (Disk disk : tm.getDisks()) {
                            Image image = disk.getImage();
                            image.setDescription("this is a test");
                            image.setImagePath(GeneralConfigurationManager.getIMAGE_PATH_LOCATION()+image.getName());
                            IImageService imageService = new ImageServiceImpl(); 
                            OneResponse r =imageService.allocate(OpenNebulaClient.getInstance(), image.toString(),108);
                            
                            if (r.isError()) {
                                System.out.println("An error has occured " + r.getErrorMessage());
                            } else {
                                System.out.print(r.getMessage());
                            }

                        }

                        System.out.println(tm.toString());
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    connectionSocket.close();
                    isListening = false;
                }
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
