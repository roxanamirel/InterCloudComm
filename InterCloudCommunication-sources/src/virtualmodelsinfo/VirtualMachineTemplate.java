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
package virtualmodelsinfo;


import java.util.ArrayList;
import java.util.List;

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


public class VirtualMachineTemplate {

    private VirtualNetworkInfo virtualNetworkInfo;
    private List<VirtualDiskInfo> virtualDiskInfos;

    private double requestedCPU;
    private double requestedCores; 
    private int id;    
    private int requestedMemory;
    private String vmTemplateInfo;
    private String hostServerHostname;       
    private String name;

    public VirtualMachineTemplate(String taskName) {
        virtualDiskInfos = new ArrayList<VirtualDiskInfo>();
        this.name = taskName;
    }

    public double getRequestedCores() {
        return requestedCores;
    }

    public void setRequestedCPUCores(double requestedCores) {
        this.requestedCores = requestedCores;
    }
    
    public List<VirtualDiskInfo> getVirtualDiskInfos() {
        return virtualDiskInfos;
    }

    public String getHostServerHostname() {
        return hostServerHostname;
    }

    public void setHostServerHostname(String hostServerHostname) {
        this.hostServerHostname = hostServerHostname;
    }

    public void setVirtualDiskInfos(List<VirtualDiskInfo> virtualDiskInfos) {
        this.virtualDiskInfos = virtualDiskInfos;
    }

    public String getVmTemplateInfo() {
        return vmTemplateInfo;
    }

    public void setVmTemplateInfo(String vmTemplateInfo) {
        this.vmTemplateInfo = vmTemplateInfo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public VirtualNetworkInfo getNetworkInfo() {
        return virtualNetworkInfo;
    }

    public void setVirtualNetworkInfo(VirtualNetworkInfo virtualNetworkInfo) {
        this.virtualNetworkInfo = virtualNetworkInfo;
    }

    public double getRequestedCPU() {
        return requestedCPU;
    }

    public void setRequestedCPU(double requestedCPU) {
        this.requestedCPU = requestedCPU;
    }

    public int getRequestedMemory() {
        return requestedMemory;
    }
    
    public List<VirtualDiskInfo> getDiskDtos() {
        return virtualDiskInfos;
    }

    public void addDiskDto(VirtualDiskInfo virtualDiskInfo) {
        this.virtualDiskInfos.add(virtualDiskInfo);
    }


    public void setRequestedCPU(int requestedCPU) {
        this.requestedCPU = requestedCPU;
    }

    public void setRequestedMemory(int requestedMemory) {
        this.requestedMemory = requestedMemory;
    }

    public void setNetworkInfo(VirtualNetworkInfo virtualNetworkInfo) {
        this.virtualNetworkInfo = virtualNetworkInfo;
    }
    
    
    
}
