/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.implementations;


import services.interfaces.IServerService;
import dclink_entities.HostData;
import dclink_if.DCLink;

import exceptions.ServiceCenterAccessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.vm.VirtualMachine;
import utils.config.ARPTableManager;
import utils.config.Configurations;
import utils.config.GeneralConfigurationManager;
import utils.logging.ApplicationLoggingSystem;
import utils.parsers.OpenNebulaInfoXMLParser;
import virtualmodelsinfo.PhysicalHost;
import virtualmodelsinfo.ServerInfo;
import virtualmodelsinfo.VirtualMachineTemplate;

/**
 *
 * @author AM
 */
public class ServerServiceImpl implements IServerService {

    private static boolean SHUTDOWN_REQUESTED = false;
    private static ARPTableManager arpTableManager;

    static {
        String location = GeneralConfigurationManager.getARPTableFileLocation();
        arpTableManager = new ARPTableManager(location);
    }
    public ServerServiceImpl(){}

    @Override
    public List<ServerInfo> getDefinedServers() throws ServiceCenterAccessException {
        List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();

        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }

        HostPool hostPool = new HostPool(client);
        hostPool.info();
        Iterator<Host> hostIterator = hostPool.iterator();
        try {
            while (hostIterator.hasNext()) {
                Host host = hostIterator.next();
                try {
                    OneResponse response = host.info();
                    String data = response.getMessage();

                    ServerInfo serverInfo = OpenNebulaInfoXMLParser.parseServerInfo(data);
                    String hostname = serverInfo.getHostName();
                    if (arpTableManager.hasMAC(hostname)) {
                        serverInfo.setMacAddress(arpTableManager.getMAC(hostname));
                    } else {
                        String mac = getServerMAC(hostname);
                        arpTableManager.addMAC(hostname, mac);
                        serverInfo.setMacAddress(mac);
                    }

                    //read CPU Frequency
                    //try to read trough ssh the output of cpufreq-info -l : gives CPU frequency hardware limits
                    //if no reponse, read CPU freq from OpenNebula
                    {
                        try {

                            String cmd = "/usr/bin/ssh -t -t Node1 cpufreq-info -l";

                            //execute the wakeonlan command
                            Process proc = Runtime.getRuntime().exec(cmd);
                            OutputStream outputStream = proc.getOutputStream();
                            InputStream stdin = proc.getInputStream();
                            InputStreamReader isr = new InputStreamReader(stdin);

                            //log the wakeonlan output
                            BufferedReader br = new BufferedReader(isr);
                            String line = br.readLine();
                            if (line != null) {
                                //  ApplicationLoggingSystem.getInstance().LogInfo(line);
                                String[] values = line.split(" ");
                                String cpuFreq = values[values.length - 1];
                                if (cpuFreq.matches("\\d+")) {
                                    serverInfo.setCpuFrequency(Float.parseFloat(cpuFreq));
                                }
                            }

                            br.close();
                            isr.close();
                            stdin.close();
                            outputStream.close();
                            proc.getInputStream().close();
                            proc.getOutputStream().close();
                            proc.getErrorStream().close();
                            proc.destroy();


                        } catch (Exception ex) {
                            ApplicationLoggingSystem.getInstance().LogInfo(ex.getMessage());
                        }
                    }

                    serverInfos.add(serverInfo);
                } catch (Exception e) {
                    ApplicationLoggingSystem.getInstance().LogInfo("Error monitoring " + host.getId() + ". Probably is offline. Because incomplete data about the server was recorded by OpenNebula, it will be IGNORED.");
                }
            }

        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        } finally {
            arpTableManager.writeArpTable();
        }

        return serverInfos;
    }

    private void pingTarget(String ip) throws ServiceCenterAccessException {
        String pingCmd = GeneralConfigurationManager.getPingLocation() + " " + ip;
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            //ApplicationLoggingSystem.getInstance().LogInfo("Waiting until " + ip + " responds to ping");
            in.readLine();

            in.close();
            p.getInputStream().close();
            p.getOutputStream().close();
            p.getErrorStream().close();
            p.destroy();
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
    }

    private String getServerMAC(String ip) throws ServiceCenterAccessException {
        String mac = null;
        pingTarget(ip);
        String pingCmd = GeneralConfigurationManager.getArpLocation() + " " + ip;
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            //TODO: nu parseaza nime mac
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            in.readLine();

            mac = in.readLine();

            String[] response = mac.split(" ");
            int count = 0;
            int length = mac.length();
            while ((count < length) && (mac.charAt(count) != ' ')) {
                count++;
            }
            while ((count < length) && (mac.charAt(count) == ' ')) {
                count++;
            }
            while ((count < length) && (mac.charAt(count) != ' ')) {
                count++;
            }
            while ((count < length) && (mac.charAt(count) == ' ')) {
                count++;
            }
            int count_init = count;
            while ((count < length) && (mac.charAt(count) != ' ')) {
                count++;
            }
            String mac_final = mac.substring(count_init, count);

            mac = mac_final;
            //      ApplicationLoggingSystem.getInstance().LogInfo(mac);

            in.close();
            p.getInputStream().close();
            p.getOutputStream().close();
            p.getErrorStream().close();
            p.destroy();
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        return mac;
    }

    @Override
    public ServerInfo getServerInfo(PhysicalHost physicalHost) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        Host host = new Host(physicalHost.getId(), client);

        OneResponse response = host.info();
        if (response.isError()) {
            throw new ServiceCenterAccessException(response.getErrorMessage());
        }
        ServerInfo dto = null;
        while (dto == null || dto.getTotalMem() == 0) {
            if (SHUTDOWN_REQUESTED) {
                return new ServerInfo();
            }
            try {
                response = host.info();
                dto = OpenNebulaInfoXMLParser.parseServerInfo(response.getMessage());
                dto.setMacAddress(getServerMAC(physicalHost.getHostname()));
            } catch (Exception e) {
                throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
            }
        }
        return dto;
    }

    @Override
    public void migrateVirtualMachine(VirtualMachineTemplate taskInfo, PhysicalHost destination) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }

        //get a reference to an existing virtual machine having the OpenNebula ID = taskInfo.getId()
        VirtualMachine machine = new VirtualMachine(taskInfo.getId(), client);

        //issue a live migration or an offline migration depending on the configuration of the GCL
        OneResponse response =
                (GeneralConfigurationManager.getVMMigrationMechanism().equals("live"))
                ? machine.liveMigrate(destination.getId())
                : machine.migrate(destination.getId());

        if (response.isError()) {
            resubmitAndDeployVirtualMachine(machine, destination);
        }

        //wait for the migration to complete and the virtual machine to boot
        machine.info();
        while (machine.lcmState() != 3 && machine.lcmState() != 0 && machine.lcmState() != 14) {
            if (SHUTDOWN_REQUESTED) {
                return;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
            }
            machine.info();

        }
    }

    private void resubmitAndDeployVirtualMachine(VirtualMachine machine, PhysicalHost host) throws ServiceCenterAccessException {
//        ApplicationLoggingSystem.getInstance().LogInfo("Resubmiting and deploying " + machine.getName() + " to " + host.getHostname());
        machine.resubmit();
        OneResponse deployVirtualMachineResponse = machine.deploy(host.getId());
        if (deployVirtualMachineResponse.isError()) {
            throw new ServiceCenterAccessException(deployVirtualMachineResponse.getErrorMessage());
        }
    }

    @Override
    public PhysicalHost addHost(PhysicalHost host) throws ServiceCenterAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeHost(PhysicalHost host) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        Host h1 = new Host(host.getId(), client);
        h1.info();
        while (h1.state() != -1) {
            if (SHUTDOWN_REQUESTED) {
                return;
            }
            ApplicationLoggingSystem.getInstance().LogInfo(h1.state() + "");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
            }
            h1.info();
        }
        sendServerToSleep(host);


    }

    @Override
    public void enableHost(PhysicalHost host) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        Host ehost = new Host(host.getId(), client);
        OneResponse response = ehost.enable();
        if (response.isError()) {
            throw new ServiceCenterAccessException(response.getErrorMessage());
        }
    }

    @Override
    public void disableHost(PhysicalHost physicalHost) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        Host host = new Host(physicalHost.getId(), client);
        OneResponse response = host.disable();
        if (response.isError()) {
            throw new ServiceCenterAccessException(response.getErrorMessage());
        }
    }

    @Override
    public void wakeUpServer(PhysicalHost physicalHost) throws ServiceCenterAccessException {
        try {

            String cmd = "";
            if (GeneralConfigurationManager.getNodesWakeUpMechanism().equals("python")) {
                cmd = "./scripts/manage_recs.py -i " + GeneralConfigurationManager.getRECSControllerIP() + " -s " + physicalHost.getMac() + "";
            } else {
                cmd = GeneralConfigurationManager.getNodesWakeUpMechanism() + " " + physicalHost.getMac();
            }
            //execute the wakeonlan command
            Process proc = Runtime.getRuntime().exec(cmd);
            OutputStream outputStream = proc.getOutputStream();
            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);

            //log the wakeonlan output
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            if (line != null) {
                ApplicationLoggingSystem.getInstance().LogInfo(line);
            }

            //wait until server responds to ping
            ApplicationLoggingSystem.getInstance().LogInfo("Waiting for " + physicalHost.getHostname() + " to respond to ping");
            waitUntilTargetIsAlive(physicalHost.getHostname());

            //wait until server responds to SSH connection request
            //needed because SSH is used by OpenNebula to deploy/migrate virtual machines
            ApplicationLoggingSystem.getInstance().LogInfo("Waiting for " + physicalHost.getHostname() + " to respond to SSH");
            waitUntilSSHAvailable(physicalHost.getHostname());

            br.close();
            isr.close();
            stdin.close();
            outputStream.close();
            proc.getInputStream().close();
            proc.getOutputStream().close();
            proc.getErrorStream().close();
            proc.destroy();

        } catch (Exception ex) {
//	            //System.err.println("Error:" + ex.getMessage());
            ApplicationLoggingSystem.getInstance().LogInfo(ex.getMessage());
        }
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        Host host = new Host(physicalHost.getId(), client);
        host.enable();
    }

    @Override
    public void sendServerToSleep(PhysicalHost host) throws ServiceCenterAccessException {
        if (!checkIfAlive(host.getHostname())) {
            return;
        }

        try {
            //connect trough ssh to the target server IP = host.getHostname() and issue a shutdown command
            String cmd = "/usr/bin/ssh -t -t " + host.getHostname() + " sudo /sbin/shutdown -h now";
            Process proc = Runtime.getRuntime().exec(cmd);

//            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                GlobalLoopLogger.getLogger().info(inputLine);
//            }

            //wait until the server does not respond to ping anymore
            waitUntilTargetIsOff(host.getHostname());

            proc.getInputStream().close();
            proc.getOutputStream().close();
            proc.getErrorStream().close();
            proc.destroy();

        } catch (Exception ex) {
            ApplicationLoggingSystem.getInstance().LogInfo("Error:" + ex.getMessage());
            ApplicationLoggingSystem.getInstance().LogInfo(ex.toString());
        }
    }

    @Override
    public String getEnergyConsumptionInfo(PhysicalHost host) throws ServiceCenterAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void terminateRunningOperations() throws ServiceCenterAccessException {
        SHUTDOWN_REQUESTED = true;
    }

    @Override
    public boolean serverIsAlive(String ip) throws ServiceCenterAccessException {
        String pingCmd = GeneralConfigurationManager.getPingLocation() + " " + ip;
        boolean isAlive = false;

        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            ApplicationLoggingSystem.getInstance().LogInfo("Checking if " + ip + " responds to ping (if it is alive)");
            inputLine = in.readLine();
            inputLine = in.readLine();
            if (inputLine.toLowerCase().contains("unreachable")) {
                isAlive = false;
            } else {
                isAlive = true;
            }
            p.getInputStream().close();
            p.getOutputStream().close();
            p.getErrorStream().close();
            p.destroy();
            in.close();

        } catch (IOException e) {
            ApplicationLoggingSystem.getInstance().LogInfo(e.getMessage());;
        }

        return isAlive;
    }

    private void waitUntilTargetIsAlive(String ip) {

        String pingCmd = GeneralConfigurationManager.getPingLocation() + " " + ip;
        boolean ok = false;
        while (!ok) {
            try {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(pingCmd);

                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String inputLine;
                ApplicationLoggingSystem.getInstance().LogInfo("Waiting until " + ip + " responds to ping");
                while ((inputLine = in.readLine()) != null) {
                    if (SHUTDOWN_REQUESTED) {
                        in.close();
                        p.getInputStream().close();
                        p.getOutputStream().close();
                        p.getErrorStream().close();
                        p.destroy();
                        return;
                    }
//	                    GlobalLoopLogger.getLogger().info(inputLine);

                    if (!inputLine.toLowerCase().contains("unreachable") && !inputLine.toLowerCase().equals("")
                            && inputLine.toLowerCase().contains("64 bytes")) {
                        ok = true;
                        ApplicationLoggingSystem.getInstance().LogInfo(ip + " responded to ping");
                        in.close();
                        p.getInputStream().close();
                        p.getOutputStream().close();
                        p.getErrorStream().close();
                        p.destroy();
                        break;

                    }
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ex) {
                        ApplicationLoggingSystem.getInstance().LogInfo(ex.getMessage());
                    }

                }
                p.getInputStream().close();
                p.getOutputStream().close();
                p.getErrorStream().close();
                p.destroy();
//	                in.close();

            } catch (IOException e) {
                ApplicationLoggingSystem.getInstance().LogInfo(e.getMessage());;
            }
        }
    }

    private void waitUntilTargetIsOff(String ip) {

        String pingCmd = GeneralConfigurationManager.getPingLocation() + " " + ip;
        boolean ok = false;
        while (!ok) {
            try {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(pingCmd);

                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String inputLine;
                ApplicationLoggingSystem.getInstance().LogInfo("Waiting until " + ip + " Does not respond to ping anymore");
                while ((inputLine = in.readLine()) != null) {
                    if (SHUTDOWN_REQUESTED) {
                        in.close();
                        p.getInputStream().close();
                        p.getOutputStream().close();
                        p.getErrorStream().close();
                        p.destroy();
                        ApplicationLoggingSystem.getInstance().LogInfo("Not waiting anymore for " + ip + " to close. System shutdown issued");
                        return;
                    }
//	                    GlobalLoopLogger.getLogger().info(inputLine);

                    if (inputLine.toLowerCase().contains("unreachable") || inputLine.toLowerCase().equals("")
                            && !inputLine.toLowerCase().contains("64 bytes")) {
                        ApplicationLoggingSystem.getInstance().LogInfo(ip + " is off. Not responding to ping anymore");
                        ok = true;
                        in.close();
                        p.getInputStream().close();
                        p.getOutputStream().close();
                        p.getErrorStream().close();
                        p.destroy();
                        break;
                    }
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ex) {
                        ApplicationLoggingSystem.getInstance().LogInfo(ex.getMessage());
                    }

                }
                p.getInputStream().close();
                p.getOutputStream().close();
                p.getErrorStream().close();
                p.destroy();
//	                in.close();

            } catch (IOException e) {
                ApplicationLoggingSystem.getInstance().LogInfo(e.getMessage());;
            }
        }
    }

    private void waitUntilSSHAvailable(String ip) {
        if (SHUTDOWN_REQUESTED) {
            return;
        }
        try {
            boolean sshUp = false;
            String cmd = "/usr/bin/ssh " + ip + " echo ssh_ok";
            while (!sshUp) {
                Process proc = Runtime.getRuntime().exec(cmd);

                InputStreamReader isr = new InputStreamReader(proc.getInputStream());
                BufferedReader br = new BufferedReader(isr);
//	                GlobalLoopLogger.getLogger().info("Waiting until " + ip + " responds to SSH");
                {
                    String line = br.readLine();
                    if (line != null) {
//	                        if (!line.contains("@") & !line.contains("Pseudo-terminal")) {
                        if (!line.contains("ssh_ok")) {

                            isr.close();
                            br.close();
                            proc.getInputStream().close();
                            proc.getOutputStream().close();
                            proc.getErrorStream().close();
                            proc.destroy();
                            Thread.currentThread().sleep(10000);
//	                            GlobalLoopLogger.getLogger().info("Waiting for " + ip + " to respond to SSH");
                        } else {
                            ApplicationLoggingSystem.getInstance().LogInfo(ip + " has responded to SSH");
                            sshUp = true;
                            isr.close();
                            br.close();
                            proc.getInputStream().close();
                            proc.getOutputStream().close();
                            proc.getErrorStream().close();
                            proc.destroy();
                            break;

                        }
//	                    line = br.readLine();   Nu mai trebe citit k se apeleaza waitUntilSSHAvailable
                    } else {
                        proc.getInputStream().close();
                        proc.getOutputStream().close();
                        proc.getErrorStream().close();
                        proc.destroy();
                    }
                }
            }
            // waitUntilTargetIsAlive(newIP);
        } catch (Exception ex) {
            ApplicationLoggingSystem.getInstance().LogInfo(ex.toString());
        }
    }

    private boolean checkIfAlive(String ip) {
        String pingCmd = GeneralConfigurationManager.getPingLocation() + " " + ip;
        boolean ok = false;

        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            ApplicationLoggingSystem.getInstance().LogInfo("Checking if " + ip + " is alive");
            inputLine = in.readLine();
            inputLine = in.readLine();
            if (inputLine != null) {

                if (inputLine.toLowerCase().contains("unreachable") || inputLine.toLowerCase().equals("")
                        && !inputLine.toLowerCase().contains("64 bytes")) {
                    ApplicationLoggingSystem.getInstance().LogInfo(ip + " is off. No turn off needed");
                    in.close();
                    p.getInputStream().close();
                    p.getOutputStream().close();
                    p.getErrorStream().close();
                    p.destroy();
                    ok = false;
                } else {
                    p.getInputStream().close();
                    p.getOutputStream().close();
                    p.getErrorStream().close();
                    p.destroy();
                    ok = true;
                }

            } else {
                in.close();
                p.getInputStream().close();
                p.getOutputStream().close();
                p.getErrorStream().close();
                p.destroy();
                ok = false;
            }

        } catch (IOException e) {
            ApplicationLoggingSystem.getInstance().LogInfo(e.getMessage());
        }


        return ok;

    }

    //hardcodat
    @Override
    public List<PhysicalHost> getAllHosts() {
        DCLink dCLink = new DCLink();
        List<PhysicalHost> hosts = new ArrayList<PhysicalHost>();
        for (int i = 0; i < dCLink.getHosts().size(); i++) {
            HostData host = dCLink.getHosts().get(i);
            hosts.add(new PhysicalHost());
            hosts.get(i).setHostname(host.getName());
            hosts.get(i).setId(Integer.parseInt(host.getId()));

        }
        return hosts;
    }

    @Override
    public List<PhysicalHost> getAllRealHosts() throws ServiceCenterAccessException {
//        List<PhysicalHost>hosts=new ArrayList<PhysicalHost>();
//        Client client = null;
//        try {
//            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
//        } catch (Exception e) {
//            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
//        }
//
//        HostPool hostPool = new HostPool(client);
//        hostPool.processInfo(HostPool.info(client));
//        Iterator<Host> hostIterator = hostPool.iterator();
//
//        List<ServerInfo> definedServers = getDefinedServers();
//        int i=0;
//        while (hostIterator.hasNext()) {
//            Host host = hostIterator.next();
//            PhysicalHost phyHost=new PhysicalHost();
//            phyHost.setId(Integer.parseInt(host.getId()));
//            phyHost.setHostname(host.getName());
//            phyHost.setMac(definedServers.get(i).getMacAddress());
//            hosts.add(phyHost);
//        }
       return null;
   }
}
