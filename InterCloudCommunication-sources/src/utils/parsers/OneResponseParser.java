/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.parsers;

import database.ConnectDB;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import services.implementations.InterCloudMigrationServiceImpl;
import models.Disk;
import models.Image;
import models.ImageType;
import services.implementations.ImageServiceImpl;
import services.interfaces.IImageService;

/**
 *
 * @author oneadmin
 */
public class OneResponseParser {

    public static String getImagePath(String imageName) {
        String path = "";
        String imageBody = new ConnectDB().getImageBody(imageName);
        if (!imageBody.equals("")) {
            try {
                Document document = OneResponseParser.loadXMLFromString(imageBody);
                NodeList nodeList = document.getElementsByTagName("IMAGE");

                Node node = nodeList.item(0);
                Element templateElement = (Element) node;
                path = OneResponseParser.getValues("SOURCE", templateElement).get(0);
            } catch (Exception ex) {
                Logger.getLogger(InterCloudMigrationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Could not retrieve image body");
        }
        return path;

    }

    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public static List<String> getValues(String tag, Element element) {
        List<String> values = new ArrayList<>();
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = (Node) nodes.item(i);
            values.add(node.getNodeValue());
        }
        return values;
    }

    public static List<Disk> getDisksWithImages(String tag, Element element) {

        NodeList imagesn = element.getElementsByTagName(tag);
        List<Disk> disks = new ArrayList<>();
        for (int i = 0; i < imagesn.getLength(); i++) {
            int imageId
                    = Integer.parseInt(imagesn.item(i).getChildNodes().item(0).getNodeValue());
            IImageService iis = new ImageServiceImpl();
            org.opennebula.client.image.Image openNebulaImage
                    = iis.getImageById(imageId);
            openNebulaImage.info();
            Image image
                    = new Image(
                            imageId,
                            ImageType.values()[openNebulaImage.type()]);
            disks.add(new Disk(image, i));
        }

        return disks;
    }
}
