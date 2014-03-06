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

public class Main {

    public static void main(String[] args) {
        System.out.println("main");
        VirtualMachinePool virtualMachinePool
                = new VirtualMachinePool(OpenNebulaClient.getInstance());
        virtualMachinePool.info();
        VirtualMachine virtualMachine = virtualMachinePool.getById(410);
        String imagePath = "/var/lib/one/testImages/";
        Puppeteer puppeteer = new Puppeteer(
                "oneadmin",
                "password",
                imagePath,
                "192.168.1.30",
                6789);
        DataCenter dataCenter = new DataCenter(puppeteer, null, 1);

        IInterCloudMigrationService iicms
                = new InterCloudMigrationServiceImpl();

        //iicms.migrateToDatacenter(virtualMachine, dataCenter);
        
        IImageService imageService = new ImageServiceImpl();
        Image image = new Image();
        image.setName("rox");
        image.setImagePath("/var/lib/one/datastores/108/2c662c7fa1588ce6f95627dcd7e87707");
        image.setIsPublic(true);
        image.setDescription("this is a test");
        org.opennebula.client.image.Image i =imageService.getAllImages().get(0);
        i.info();
        i.getName();
        System.out.println(image.toString());
        OneResponse r =imageService.allocate(OpenNebulaClient.getInstance(), image.toString());
        
        if(r.isError()){
            System.out.println("An error has occured " + r.getErrorMessage());
        }
        else 
            System.out.print(r.getMessage());

    }
}
