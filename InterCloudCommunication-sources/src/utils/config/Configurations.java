/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils.config;

/**
 *
 * @author oneadmin
 */
public class Configurations {
    public static final String TEMPLATE_PATH="/home/oneadmin/Documents/ptRoxana/";
    public static final String LogPath = "Logs/Logs";
    public static final int GCL_RMI_API_PORT = 1333;
    public final static String NEBULA_RCP_ADDRESS = GeneralConfigurationManager.getOpenNebulaRCPAddress();
    public final static String NEBULA_CREDENTIALS = GeneralConfigurationManager.getOpenNebulaCredentials();
}
