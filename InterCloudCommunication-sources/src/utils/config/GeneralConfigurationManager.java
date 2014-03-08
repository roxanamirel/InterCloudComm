/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils.config;

/**
 *
 * @author AM
 */




import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import utils.logging.ApplicationLoggingSystem;




public class GeneralConfigurationManager {

    private static Properties generalProperties;
    private static final String CONFIG_PATH="/var/lib/one/Documents/intercloudcomm17feb/InterCloudCommunication-sources/src/utils/config/config.properties";

    static {
        generalProperties = new Properties();
        try {
            generalProperties.load(new FileInputStream(CONFIG_PATH));
        } catch (IOException e) {
//            ApplicationLoggingSystem.getInstance().LogInfo(e.toString());
            System.exit(1);
        }
    }

    public static boolean isFileBasedWorkloadGenerator() {
        return false;
    }

    public static String getWorkloadGeneratorInputFile() {
        return "/media/Extra/SVN_DSRL/Software/Scheduler_Standalone/GAMES-GlobalLoop-Core/generatedSchedules/generated_schedule__31_01_11_45_.xml";
        //throw new UnsupportedOperationException("Not supported under current operation mode");
    }

    public static boolean usesWorkloadGenerator() {
        return false;
    }

    public static boolean isWSCallbackEnabled() {
        return false;
    }

    public static String getVMBridge() {
        return generalProperties.getProperty("virtualMachinesBridgeName");
    }

    public static String getOpenNebulaRCPAddress() {
        return generalProperties.getProperty("openNebulaRCPAPIAddress");
    }

    public static String getOpenNebulaCredentials() {
        return generalProperties.getProperty("openNebulaCredentials");
    }

    public static String getNodesWakeUpMechanism() {
        return generalProperties.getProperty("nodesWakeUpMechanism");
    }

    public static String getCallbackServiceURL() {
        throw new UnsupportedOperationException("Not supported under current operation mode");
    }

    public static String getCallbackServiceOperation() {
        throw new UnsupportedOperationException("Not supported under current operation mode");
    }

    public static String getCallbackServiceNamespace() {
        throw new UnsupportedOperationException("Not supported under current operation mode");
    }

    public static String getDefaultVMIP() {
        return generalProperties.getProperty("vmDefaultIP");
    }

    public static String getVMNetworkID() {
        return generalProperties.getProperty("vmNetwork");
    }

    public static String getRECSControllerIP() {
        throw new UnsupportedOperationException("Not supported under current operation mode");
    }

    public static Integer getContextEvaluationInterval() {
        throw new UnsupportedOperationException("Not supported under current operation mode");
    }

    public static String getVMMigrationMechanism() {
        return generalProperties.getProperty("vmMigrationMechanism");
    }

    public static boolean runningInClusterMode() {
        return generalProperties.containsKey("clusterMode") && generalProperties.getProperty("clusterMode").equals("yes");
    }

    public static Integer getClusterSize() {
        return Integer.parseInt(generalProperties.getProperty("clusterSize"));
    }

    public static Integer getEntropyThreshold() {
        return Integer.parseInt(generalProperties.getProperty("entropyThreshold"));
    }

    public static Integer getRMIAPIPort() {
        return Configurations.GCL_RMI_API_PORT;
    }

    public static boolean isMigrationEnabled() {
        return generalProperties.containsKey("migrationIsEnabled") && generalProperties.getProperty("migrationIsEnabled").equals("yes");
    }

    public static int getServersNo() {
        throw new UnsupportedOperationException("Not supported under current operation mode");
    }

    public static int getVMPoolingInterval() {
        throw new UnsupportedOperationException("Not supported under current operation mode");
    }

    public static boolean isDeployedAsOpenNebulaScheduler() {
        return true;
    }

    public static int getVMDefaultCPUFrequency() {
        return Integer.parseInt(generalProperties.getProperty("vmDefaultCPUFrequency"));
    }

    public static float getResourceLoadOptimalValue() {
        return Integer.parseInt(generalProperties.getProperty("resourceLoadOptimalValue"));
    }

    public static float getResourceLoadVariation() {
        return Integer.parseInt(generalProperties.getProperty("resourceLoadVariation"));
    }

    public static String getPingLocation() {
        return generalProperties.getProperty("pingLocation");
    }

    public static String getArpLocation() {
        return generalProperties.getProperty("arpLocation");
    }

    public static boolean isVMPoolingEnabled() {
        return false;
    }

    public static String getPlanningEngine() {
        return generalProperties.getProperty("planningEngine").toLowerCase();
    }

    public static boolean swrlEnabled() {
        return false;
    }

    public static String getMemoryState() {
        return generalProperties.getProperty("usingMemory").toLowerCase();
    }

    public static String getARPTableFileLocation() {
        return generalProperties.getProperty("arpTableFileLocation");
    }

    public static boolean workloadForOpenNebulaHooks() {
        return true;
    }

    public static boolean isStubServiceCenterManagement() {
        return generalProperties.containsKey("stubServiceCenterManagementEnabled") && generalProperties.getProperty("stubServiceCenterManagementEnabled").equals("yes");
    }

    public static String getSSHLocation() {
        return generalProperties.getProperty("sshLocation");
    }
    
}
