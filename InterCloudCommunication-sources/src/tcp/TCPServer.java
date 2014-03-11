/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.TemplateModel;
import org.opennebula.client.OneResponse;
import services.implementations.ImageServiceImpl;
import services.implementations.TemplateServiceImpl;
import services.interfaces.IImageService;
import services.interfaces.ITemplateService;

/**
 *
 * @author oneadmin
 */
public class TCPServer  {

    static String clientSentence;
    static String capitalizedSentence;

    public static void main(String... args) {

        listen();
    }


    public static void listen() {
        TemplateModel tm = null;
        Socket connectionSocket = null;
        boolean isListening = true;
        while (isListening) {
            ServerSocket welcomeSocket;
            try {
                System.out.println("Listening on port 6789");
                welcomeSocket = new ServerSocket(6789);
                connectionSocket = welcomeSocket.accept();
                if (connectionSocket.isConnected()) {
                    System.out.println(
                            "Accepted a connection from "
                            + connectionSocket.getRemoteSocketAddress().toString());
                    InputStream is = connectionSocket.getInputStream();
                    ObjectInputStream obj = new ObjectInputStream(is);
                    try {
                        tm = (TemplateModel) obj.readObject();
                        System.out.println("\n" + tm.toString());

                        IImageService imageService = new ImageServiceImpl();
                        ITemplateService templateService = new TemplateServiceImpl();

                        List<OneResponse> oneResponses = imageService.allocateImages(tm);

                        templateService.allocateTemplate(tm, oneResponses);
                        if (!isListening) {
                            connectionSocket.close();
                        }

                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
