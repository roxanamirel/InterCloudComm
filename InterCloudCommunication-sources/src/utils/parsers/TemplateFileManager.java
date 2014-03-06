/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.parsers;

import models.OS;
import models.Network;
import models.TemplateModel;
import models.Graphics;
import models.Disk;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author oneadmin
 */
public class TemplateFileManager {

    public boolean createFileFromTemplate(TemplateModel templateModel) {
        boolean created = true;


        return created;
    }

    public TemplateModel createTemplateFromFile(File templateFile) {

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        TemplateModel tm = new TemplateModel();

        try {
            documentBuilder = documentFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(templateFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("VMTEMPLATE");
            
            Node node = nodeList.item(0);            
            Element template = (Element) node;
            
            tm.setName(getValue("NAME", template));            
            tm.setCpu(Integer.parseInt(getValue("CPU", template)));
            tm.setMemory(Integer.parseInt(getValue("MEMORY", template)));
                        
            
            NodeList disksn = template.getElementsByTagName("IMAGE_ID");
            List<Disk>disks= new ArrayList<>();
            for(int i=0;i<disksn.getLength();i++){
                String val=disksn.item(i).getChildNodes().item(0).getNodeValue();
                //disks.add(new Disk(Integer.parseInt(val)));
                
            }
            Graphics graphics = new Graphics(
                                    getValue("LISTEN", template),
                                    getValue("TYPE", template));
            
            Network nic = new Network(Integer.parseInt(getValue("NETWORK_ID", template)));
            OS os = new OS(
                    getValue("ARCH",template),
                    getValue("BOOT",template));
            
            tm.setDisks(disks);
            tm.setGraphics(graphics);
            tm.setNic(nic);
            tm.setOs(os);            
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println("Error while parsing the template FILE\n" + ex.getMessage());
        }
        return tm;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }
}
