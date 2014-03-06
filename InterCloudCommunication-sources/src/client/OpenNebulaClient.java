/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;
import utils.config.Configurations;

/**
 *
 * @author oneadmin
 */
public class OpenNebulaClient {
    
    private static Client _instance;
    
    public static Client getInstance(){
        if (_instance == null) {
            try {
                _instance = new Client(
                        Configurations.NEBULA_CREDENTIALS,
                        Configurations.NEBULA_RCP_ADDRESS);
            } catch (ClientConfigurationException ex) {
                Logger.getLogger(OpenNebulaClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return _instance;
    }
}
