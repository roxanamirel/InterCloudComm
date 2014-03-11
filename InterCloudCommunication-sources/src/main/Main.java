package main;

import client.OpenNebulaClient;
import models.DataCenter;
import models.Image;
import models.Puppeteer;
import org.opennebula.client.OneResponse;
import org.opennebula.client.Pool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import services.implementations.ImageServiceImpl;
import services.implementations.InterCloudMigrationServiceImpl;
import services.interfaces.IImageService;
import services.interfaces.IInterCloudMigrationService;
import tcp.TCPServer;

public class Main {

    public static void main(String[] args) {
        
        TCPServer server = new TCPServer();
        server.setDaemon(true);
        server.start();
        
        
        
        VirtualMachinePool virtualMachinePool
                = new VirtualMachinePool(OpenNebulaClient.getInstance());
        virtualMachinePool.info();
        VirtualMachine virtualMachine = virtualMachinePool.getById(455);
        String imagePath = "/var/lib/one/migratedImages/";
        Puppeteer puppeteer = new Puppeteer(
                "oneadmin",
                "password",
                imagePath,
                "192.168.1.30",
                6789);
        DataCenter dataCenter = new DataCenter(puppeteer, null, 1);

        IInterCloudMigrationService iicms
                = new InterCloudMigrationServiceImpl();

        iicms.migrateToDatacenter(virtualMachine, dataCenter);
        



    }
}
