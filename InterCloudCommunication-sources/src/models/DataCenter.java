/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.List;

/**
 *
 * @author oneadmin
 */
public class DataCenter {

    private Puppeteer _puppeteer;
    private List<HostNode> _hostNodes;
    private int _id;

    public DataCenter(Puppeteer _puppeteer, List<HostNode> _hostNodes, int id) {
        this._puppeteer = _puppeteer;
        this._hostNodes = _hostNodes;
        this._id = id;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public Puppeteer getPuppeteer() {
        return _puppeteer;
    }

    public List<HostNode> getHostNodes() {
        return _hostNodes;
    }

    public void setPuppeteer(Puppeteer _puppeteer) {
        this._puppeteer = _puppeteer;
    }

    public void setHostNodes(List<HostNode> _hostNodes) {
        this._hostNodes = _hostNodes;
    }

}
