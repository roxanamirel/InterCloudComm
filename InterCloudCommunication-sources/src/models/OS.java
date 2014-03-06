/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;

/**
 *
 * @author oneadmin
 */
public class OS implements Serializable{
    
    private String arch;
    private String boot;

    public OS(String arch, String boot) {
        this.arch = arch;
        this.boot = boot;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getBoot() {
        return boot;
    }

    public void setBoot(String boot) {
        this.boot = boot;
    }  
}
