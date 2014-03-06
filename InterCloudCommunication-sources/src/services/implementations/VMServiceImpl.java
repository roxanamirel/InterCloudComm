/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.implementations;

import client.OpenNebulaClient;
import java.util.List;

import exceptions.ServiceCenterAccessException;
import java.util.ArrayList;
import java.util.Iterator;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.template.Template;
import org.opennebula.client.template.TemplatePool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.opennebula.client.vnet.VirtualNetwork;
import models.Disk;
import utils.config.Configurations;
import utils.config.GeneralConfigurationManager;
import utils.parsers.OpenNebulaInfoXMLParser;
import utils.parsers.OpennebulaUtils;
import services.interfaces.IVMService;
import virtualmodelsinfo.PhysicalHost;
import virtualmodelsinfo.VirtualNetworkInfo;
import virtualmodelsinfo.VirtualMachineTemplate;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author oneadmin
 */
public class VMServiceImpl implements IVMService {
    
    @Override
    public boolean contains(VirtualMachine virtualMachine) {
        VirtualMachinePool vmp = 
                new VirtualMachinePool(OpenNebulaClient.getInstance());
        vmp.info();
        
        return vmp.getById(Integer.parseInt(virtualMachine.getId())) != null;
    }
    
    @Override
    public VirtualMachineTemplate getVMInfo(Integer vmID) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }

        VirtualMachine virtualMachine = new VirtualMachine(vmID, client);
        OneResponse response = virtualMachine.info();
        String data = response.getMessage();
        VirtualMachineTemplate virtualTaskInfo = null;

        try {
            virtualTaskInfo = OpenNebulaInfoXMLParser.parseVirtualMachineInfo(data);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }

        return virtualTaskInfo;
    }

    @Override
    public List<VirtualMachineTemplate> getPendingVirtualMachines() throws ServiceCenterAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<VirtualMachineTemplate> getAllVirtualMachines() throws ServiceCenterAccessException {
        List<VirtualMachineTemplate> runningVMs = new ArrayList<VirtualMachineTemplate>();

        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }

        VirtualMachinePool pool = new VirtualMachinePool(client);
        
        //pool.processInfo(VirtualMachinePool.info(client, -2));
        Iterator<VirtualMachine> hostIterator = pool.iterator();
        try {
            while (hostIterator.hasNext()) {
                VirtualMachine virtualMachine = hostIterator.next();
                OneResponse response = virtualMachine.info();
                String data = response.getMessage();
                runningVMs.add(OpenNebulaInfoXMLParser.parseVirtualMachineInfo(data));
            }
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.toString(), e.getCause());
        }
        return runningVMs;
    }

    @Override
    public VirtualMachineTemplate createAndDeployVirtualMachine(VirtualMachineTemplate infoVirtual, PhysicalHost physicalHost) throws ServiceCenterAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VirtualMachineTemplate createVirtualMachine(VirtualMachineTemplate infoVirtual) throws ServiceCenterAccessException {
        //create a connection to the OpenNebula manager
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }

        VirtualNetworkInfo virtualNetworkInfo = infoVirtual.getNetworkInfo();
        String vmTemplate = infoVirtual.getVmTemplateInfo();
        vmTemplate = OpennebulaUtils.configureVMTemplate(vmTemplate, infoVirtual.getName(),
                infoVirtual.getRequestedCPU() / 3000, infoVirtual.getRequestedMemory(),
                GeneralConfigurationManager.getVMNetworkID(), virtualNetworkInfo.getIp(),
                Integer.parseInt(virtualNetworkInfo.getIp().substring(virtualNetworkInfo.getIp().lastIndexOf(".") + 1)),
                virtualNetworkInfo.getVncPassword());

        //create virtual machine for OpenNebula
        OneResponse createVirtualMachineResponse = VirtualMachine.allocate(client, vmTemplate);

        //verify virtual machine creation result
        if (createVirtualMachineResponse.isError()) {
            VirtualNetwork.delete(client, infoVirtual.getNetworkInfo().getId());
            throw new ServiceCenterAccessException(createVirtualMachineResponse.getErrorMessage());
        } else {
            infoVirtual.setId(Integer.parseInt(createVirtualMachineResponse.getMessage()));
        }
        VirtualMachine vm = new VirtualMachine(infoVirtual.getId(), client);
        vm.info();
        while (!vm.lcmStateStr().toLowerCase().contains("boot") && !vm.lcmStateStr().toLowerCase().contains("runn")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
            }
            vm.info();

        }
        return infoVirtual;
    }

    @Override
    public void removeVirtualMachine(VirtualMachineTemplate infoVirtual) throws ServiceCenterAccessException {
        //create a connection to the OpenNebula manager
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        VirtualMachine vm = new VirtualMachine(infoVirtual.getId(), client);
        OneResponse finalizeResponse = vm.finalizeVM();
        if (finalizeResponse.isError()) {
            throw new ServiceCenterAccessException(finalizeResponse.getErrorMessage());
        }
    }

    @Override
    public VirtualMachineTemplate deployVirtualMachine(VirtualMachineTemplate infoVirtual, PhysicalHost physicalHost) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }

        VirtualMachine virtualMachine = new VirtualMachine(infoVirtual.getId(), client);

        //deploy the virtual machine onto the server having the OpenNebula ID =  physicalHost.getId()
        OneResponse deployVirtualMachineResponse = virtualMachine.deploy(physicalHost.getId());
        VirtualMachineTemplate toBeReturned = null;
        try {
            toBeReturned = OpenNebulaInfoXMLParser.parseVirtualMachineInfo(virtualMachine.info().getMessage());
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.toString());
        }
        return toBeReturned;
    }

    @Override
    public void startVirtualMachine(VirtualMachineTemplate taskInfo) throws ServiceCenterAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopVirtualMachine(VirtualMachineTemplate infoVirtual) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        VirtualMachine machine = new VirtualMachine(infoVirtual.getId(), client);
        OneResponse response = machine.stop();
        if (response.isError()) {
            throw new ServiceCenterAccessException(response.getErrorMessage());
        }
    }

    @Override
    public void deleteVirtualMachine(VirtualMachineTemplate infoVirtual) throws ServiceCenterAccessException {
        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        VirtualMachine machine = new VirtualMachine(infoVirtual.getId(), client);
        OneResponse machineDeleteResponse = machine.finalizeVM();
        if (machineDeleteResponse.isError()) {
            throw new ServiceCenterAccessException(machineDeleteResponse.getErrorMessage());
        }
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
        OneResponse response
                = (GeneralConfigurationManager.getVMMigrationMechanism().equals("live"))
                ? machine.liveMigrate(destination.getId())
                : machine.migrate(destination.getId());

    }

    @Override
    public void restartInitialVM(String newIP) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VirtualMachine createVM(int templateId) throws ServiceCenterAccessException {

        Client client = null;
        try {
            client = new Client(Configurations.NEBULA_CREDENTIALS, Configurations.NEBULA_RCP_ADDRESS);
        } catch (Exception e) {
            throw new ServiceCenterAccessException(e.getMessage(), e.getCause());
        }
        TemplatePool tempPool = new TemplatePool(client);
        tempPool.info();
        Template template = tempPool.getById(templateId);
        OneResponse orsp = template.info();
        String msg = orsp.getMessage();

        OneResponse or = template.instantiate();
        return new VirtualMachine(Integer.parseInt(or.getMessage()), client);
    }

    @Override
    public List<String> snapshot(List<Disk> disks, int vmId, String vmName) {

        List<String> output = new ArrayList<>();
        String comm = "su oneadmin -c password";

        //executeCommand(comm);
        for (int i = 0; i < disks.size(); i++) {
            String command = "onevm disk-snapshot --live ";
            command = command + vmId + " " + i + " " + vmName;
            System.out.println(command);
            output.add(executeCommand(command));
        }
        return output;
    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    

}
