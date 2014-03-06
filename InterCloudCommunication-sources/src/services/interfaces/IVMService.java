/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services.interfaces;

import exceptions.ServiceCenterAccessException;
import java.util.List;
import org.opennebula.client.vm.VirtualMachine;
import models.Disk;
import virtualmodelsinfo.PhysicalHost;
import virtualmodelsinfo.VirtualMachineTemplate;

/**
 *
 * @author oneadmin
 */
public interface IVMService {
    
    public VirtualMachineTemplate getVMInfo(Integer vmID) throws ServiceCenterAccessException; 

    public List<VirtualMachineTemplate> getPendingVirtualMachines() throws ServiceCenterAccessException; 

    public List<VirtualMachineTemplate> getAllVirtualMachines() throws ServiceCenterAccessException; 
    
    public VirtualMachineTemplate createAndDeployVirtualMachine(VirtualMachineTemplate infoVirtual, PhysicalHost physicalHost) throws ServiceCenterAccessException; 

    public VirtualMachineTemplate createVirtualMachine(VirtualMachineTemplate infoVirtual) throws ServiceCenterAccessException; 

    public void removeVirtualMachine(VirtualMachineTemplate infoVirtual) throws ServiceCenterAccessException;

    public VirtualMachineTemplate deployVirtualMachine(VirtualMachineTemplate infoVirtual, PhysicalHost physicalHost) throws ServiceCenterAccessException;

    public void startVirtualMachine(VirtualMachineTemplate taskInfo) throws ServiceCenterAccessException;

    public void stopVirtualMachine(VirtualMachineTemplate infoVirtual) throws ServiceCenterAccessException;

    public void deleteVirtualMachine(VirtualMachineTemplate infoVirtual) throws ServiceCenterAccessException;

    public void migrateVirtualMachine(VirtualMachineTemplate taskInfo, PhysicalHost destination) throws ServiceCenterAccessException;
    
    public void restartInitialVM(String newIP);
    
    public VirtualMachine createVM(int id)  throws ServiceCenterAccessException;
    
    public List<String> snapshot(List<Disk> disks,int vmId,String vmName);
    
    public boolean contains(VirtualMachine virtualMachine);
        
}
