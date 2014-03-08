/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services.interfaces;

import models.DataCenter;
import models.Image;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;

/**
 *
 * @author oneadmin
 */
public interface IInterCloudMigrationService {
    
    void migrateToDatacenter(VirtualMachine virtualMachine, DataCenter dataCenter);
    
    OneResponse allocateReceivedImage(Image image,int datastoreId);
}
