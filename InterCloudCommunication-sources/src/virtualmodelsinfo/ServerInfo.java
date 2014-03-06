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

public class ServerInfo {

    public static final int STATE_PEND = 0;

    private float cpuFrequency;
    private int totalCpu;//coreNO*100
    private float usedCpu;
    private int totalMem;
    private float usedMem;
    private int totalDisk;
    private float usedDisk;
    private int state; //
    private int cloudID;
    private String macAddress;
    private String hostName;

    public int getCloudID() {
        return cloudID;
    }

    public float getCpuFrequency() {
        return cpuFrequency;
    }

    public void setCpuFrequency(float cpuFrequency) {
        this.cpuFrequency = cpuFrequency;
    }

    public void setCloudID(int cloudID) {
        this.cloudID = cloudID;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getTotalCpu() {
        return totalCpu;
    }

    public void setTotalCpu(int totalCpu) {
        this.totalCpu = totalCpu;
    }

    public float getUsedCpu() {
        return usedCpu;
    }

    public void setUsedCpu(float usedCpu) {
        this.usedCpu = usedCpu;
    }

    public int getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(int totalMem) {
        this.totalMem = totalMem;
    }

    public float getUsedMem() {
        return usedMem;
    }

    public void setUsedMem(float usedMem) {
        this.usedMem = usedMem;
    }

    public int getTotalDisk() {
        return totalDisk;
    }

    public void setTotalDisk(int totalDisk) {
        this.totalDisk = totalDisk;
    }

    public float getUsedDisk() {
        return usedDisk;
    }

    public void setUsedDisk(float usedDisk) {
        this.usedDisk = usedDisk;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
