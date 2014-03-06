/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services.interfaces;

import exceptions.ServiceCenterAccessException;
import java.util.List;
import virtualmodelsinfo.PhysicalHost;
import virtualmodelsinfo.ServerInfo;
import virtualmodelsinfo.VirtualMachineTemplate;

/**
 *
 * @author AM
 */
public interface IServerService {

    List<PhysicalHost> getAllHosts();

    List<ServerInfo> getDefinedServers() throws ServiceCenterAccessException;

    ServerInfo getServerInfo(PhysicalHost physicalHost) throws ServiceCenterAccessException;
    
    void migrateVirtualMachine(VirtualMachineTemplate info, PhysicalHost destination) throws ServiceCenterAccessException;

    PhysicalHost addHost(PhysicalHost host) throws ServiceCenterAccessException;

    void removeHost(PhysicalHost host) throws ServiceCenterAccessException;

    void enableHost(PhysicalHost host) throws ServiceCenterAccessException;

    void disableHost(PhysicalHost host) throws ServiceCenterAccessException;

    void wakeUpServer(PhysicalHost host) throws ServiceCenterAccessException;

    void sendServerToSleep(PhysicalHost host) throws ServiceCenterAccessException;

    String getEnergyConsumptionInfo(PhysicalHost host) throws ServiceCenterAccessException;
    
    void terminateRunningOperations() throws ServiceCenterAccessException;

    boolean serverIsAlive(String ip) throws ServiceCenterAccessException;
    List<PhysicalHost> getAllRealHosts() throws ServiceCenterAccessException;

  
}
