/**********************************************************************************************************
 * Copyright 2012, Distributed Systems Research Laboratory, Technical University of Cluj-Napoca, Romania 
 * http://dsrl.coned.utcluj.ro/
 *  
 * Licensed under the EUPL V.1.1 
 * 
 * European Union Public Licence V. 1.1  
 * You may obtain a copy of this license at
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *  
 **********************************************************************************************************/
package utils.parsers;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import utils.config.GeneralConfigurationManager;
import virtualmodelsinfo.ServerInfo;
import virtualmodelsinfo.VirtualDiskInfo;
import virtualmodelsinfo.VirtualNetworkInfo;
import virtualmodelsinfo.VirtualMachineTemplate;


/**
 * <br/>
 * <br/>
 * <p><strong>GAMES Project - Global Control Loop</strong></p>
 * <p><strong>@Author: Technical University of Cluj-Napoca</strong></p>
 * <br/>
 * <a href=http://dsrl.coned.utcluj.ro/ > Distributed Systems research Laboratory </a>
 * <br/>Coordinator: Prof. dr. ing. Ioan Salomie E-mail: Ioan.Salomie@cs.utcluj.ro
 * <br/>
 * <br/>Developers:
 * <br/>Daniel Moldovan(Daniel.Moldovan@cs.utcluj.ro)
 * <br/>Georgiana Copil(Georgiana.Copil@cs.utcluj.ro)
 */
public class OpenNebulaInfoXMLParser {

    private OpenNebulaInfoXMLParser() {
    }

    public static ServerInfo parseServerInfo(String info) throws Exception {

        InputStream stream = new ByteArrayInputStream(info.getBytes());
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(stream);

        Node state = document.getElementsByTagName("STATE").item(0);
        Node name = document.getElementsByTagName("NAME").item(0);
        Node id = document.getElementsByTagName("ID").item(0);

        Node memUsage = document.getElementsByTagName("USED_MEM").item(0);
        Node cpuUsage = document.getElementsByTagName("USED_CPU").item(0);
        Node diskUsage = document.getElementsByTagName("USED_DISK").item(0);
//
//        Node memUsage = document.getElementsByTagName("MEM_USAGE").item(0);
//        Node cpuUsage = document.getElementsByTagName("CPU_USAGE").item(0);
//        Node diskUsage = document.getElementsByTagName("DISK_USAGE").item(0);

//        Node memMax = document.getElementsByTagName("MAX_MEM").item(0);
//        Node cpuMax = document.getElementsByTagName("MAX_CPU").item(0);
//        Node diskMax = document.getElementsByTagName("MAX_DISK").item(0);

        Node currentCPUSpeed = document.getElementsByTagName("CPUSPEED").item(0);


        Node cpuMAX = document.getElementsByTagName("MAX_CPU").item(0);
        Node cpuMAXFrequency = document.getElementsByTagName("CPUSPEED").item(0);
        Node memMAX = document.getElementsByTagName("MAX_MEM").item(0);
        Node diskMAX = document.getElementsByTagName("MAX_DISK").item(0);


        ServerInfo dto = new ServerInfo();

        dto.setCloudID(Integer.parseInt(id.getTextContent()));

        dto.setHostName(name.getTextContent());

        dto.setState(Integer.parseInt(state.getTextContent()));

        dto.setCpuFrequency(Float.parseFloat(currentCPUSpeed.getTextContent()));

        dto.setTotalCpu((Integer.parseInt(cpuMAX.getTextContent()) / 100)
                * Integer.parseInt(cpuMAXFrequency.getTextContent()));
        dto.setUsedCpu(Integer.parseInt(cpuUsage.getTextContent()));

        dto.setTotalMem(Integer.parseInt(memMAX.getTextContent()));
        dto.setUsedMem(Integer.parseInt(memUsage.getTextContent()) / 1000);

        dto.setTotalDisk(Integer.parseInt(diskMAX.getTextContent()));
        dto.setUsedDisk(Integer.parseInt(diskUsage.getTextContent()));

        return dto;
    }

    public static VirtualMachineTemplate parseVirtualMachineInfo(String vmInfo) throws Exception {
        InputStream stream = new ByteArrayInputStream(vmInfo.getBytes());
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(stream);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        XPathExpression idExpr = xpath.compile("//ID");
        XPathExpression stateExpr = xpath.compile("//STATE");
        XPathExpression nameExpr = xpath.compile("//NAME");
        XPathExpression memoryExpr = xpath.compile("//TEMPLATE/MEMORY");
        XPathExpression cpuExpr = xpath.compile("//TEMPLATE/CPU");



        XPathExpression networkExpr = xpath.compile("//TEMPLATE/NIC");
        XPathExpression networkExprIP = xpath.compile("//IP");
        XPathExpression networkExprMAC = xpath.compile("//MAC");
        XPathExpression networkExprNetworkID = xpath.compile("//NETWORK_ID");


        XPathExpression diskExpr = xpath.compile("//TEMPLATE/DISK");
        XPathExpression diskSizeExpr = xpath.compile("//SIZE");
        XPathExpression diskTypeExpr = xpath.compile("//TYPE");
        XPathExpression diskTargetExpr = xpath.compile("//TARGET");
        XPathExpression diskFormatExpr = xpath.compile("//FORMAT");

        XPathExpression hostnameExpr = xpath.compile("//HOSTNAME");
        XPathExpression cpuFreqRequirements = xpath.compile("//REQUIREMENTS");


        String id = idExpr.evaluate(new InputSource(new StringReader(vmInfo)));
        String state = stateExpr.evaluate(new InputSource(new StringReader(vmInfo)));
        String name = nameExpr.evaluate(new InputSource(new StringReader(vmInfo)));
        String requestedCPU = cpuExpr.evaluate(new InputSource(new StringReader(vmInfo)));
        String requestedMEM = memoryExpr.evaluate(new InputSource(new StringReader(vmInfo)));
        String requiredCPUFreq = cpuFreqRequirements.evaluate(new InputSource(new StringReader(vmInfo)));
        String hostname = hostnameExpr.evaluate(new InputSource(new StringReader(vmInfo)));
        
        if (requiredCPUFreq.contains("=")) {
            requiredCPUFreq = requiredCPUFreq.split("=")[1].trim();
        } //last options maintained for backward compatibility with regular VM specification
        else if (requiredCPUFreq.contains(">")) {
            requiredCPUFreq = requiredCPUFreq.split(">")[1].trim();
        } else if (requiredCPUFreq.contains("<")) {
            requiredCPUFreq = requiredCPUFreq.split("<")[1].trim();
        }

        Object disksInfo = diskExpr.evaluate(new InputSource(new StringReader(vmInfo)), XPathConstants.NODESET);
        NodeList nodes = (NodeList) disksInfo;
        List<VirtualDiskInfo> virtualDisks = new ArrayList<VirtualDiskInfo>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            VirtualDiskInfo virtualDiskInfo = new VirtualDiskInfo();
            //virtualDiskInfo.setSize(Integer.parseInt(diskSizeExpr.evaluate(node)));
            virtualDiskInfo.setType(diskTypeExpr.evaluate(node));
            virtualDiskInfo.setFormat(diskFormatExpr.evaluate(node));
            virtualDisks.add(virtualDiskInfo);
        }
        VirtualMachineTemplate virtualTaskInfo = new VirtualMachineTemplate(name);
        virtualTaskInfo.setId(Integer.parseInt(id));
        
        //vm template does not specify cpu freq
        if (requiredCPUFreq.length() == 0) {
            requiredCPUFreq = "" + GeneralConfigurationManager.getVMDefaultCPUFrequency();
        }
        
        virtualTaskInfo.setRequestedCPUCores(Float.parseFloat(requestedCPU));
        virtualTaskInfo.setRequestedCPU(Float.parseFloat(requiredCPUFreq));
        virtualTaskInfo.setRequestedMemory(Integer.parseInt(requestedMEM));
        virtualTaskInfo.setVirtualDiskInfos(virtualDisks);
        String stateLowerCase = state.toLowerCase();
        
        //when a VM is resubmitted the hostname remains. So it is not redeployed.
        //1 = PENDING, 7 = FAILED
        if(!stateLowerCase.contains("1") && !stateLowerCase.contains("7")){
            virtualTaskInfo.setHostServerHostname(hostname);
        }
        
        //reading network info
        Object networkInfo = networkExpr.evaluate(new InputSource(new StringReader(vmInfo)), XPathConstants.NODESET);
        //NodeList nodes = (NodeList) networkInfo;
        //List<VirtualNetwork> virtualDisks = new ArrayList<VirtualNetwork>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            VirtualNetworkInfo virtualNetworkInfo = new VirtualNetworkInfo();
            String ipContent = networkExprIP.evaluate(node);
            String macContent = networkExprMAC.evaluate(node);
//            String nicContent = networkExprNetworkID.evaluate(node);

            if (!ipContent.isEmpty()) {
                virtualNetworkInfo.setIp(ipContent);
            } else if (!macContent.isEmpty()) {
                String[] macDigits = macContent.split(":");
                String ip = "";
                for (int j = 2; j < macDigits.length; j++) {
                    String digit = macDigits[j];
                    ip += Integer.parseInt(digit, 16) + ".";
                }
                ip = ip.substring(0, ip.length() - 1);
                virtualNetworkInfo.setIp(ip);
            }
            virtualTaskInfo.setVirtualNetworkInfo(virtualNetworkInfo);
            break;

        }


//        virtualTaskInfo.setVirtualDiskInfos(virtualDisks);

        return virtualTaskInfo;
    }
}