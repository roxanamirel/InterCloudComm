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

public class PhysicalHost {

    public static final String IM_KVM = "im_kvm";
    public static final String IM_XEN = "im_xen";
    public static final String VMM_KVM = "vmm_kvm";
    public static final String VMM_XEN = "vmm_xen";
    public static final String TM_SSH = "tm_ssh";
    public static final String TM_NFS = "tm_nfs";

    private int id;
    private String hostname;
    private String mac;
    private String im;
    private String vmm;
    private String tm;

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getVmm() {
        return vmm;
    }

    public void setVmm(String vmm) {
        this.vmm = vmm;
    }

    public String getTm() {
        return tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }
}
