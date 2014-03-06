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
public class Network implements Serializable{
    
    private int networkId;

    public Network(int networkId) {
        this.networkId = networkId;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }    
}
