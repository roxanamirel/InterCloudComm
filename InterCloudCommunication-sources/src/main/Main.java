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
        VirtualMachine virtualMachine = virtualMachinePool.getById(424);
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
        
//        IImageService imageService = new ImageServiceImpl();
//        Image image = new Image();
//        image.setName("roxiiii_dada");
//        image.setImagePath("/var/lib/one/images/ImagineTTYBuna");
//        image.setIsPublic(true);
//        image.setDescription("this is a test");
//        System.out.println(image.toString());
//        OneResponse r =imageService.allocate(OpenNebulaClient.getInstance(), image.toString(),108);
//        
//        if(r.isError()){
//            System.out.println("An error has occured " + r.getErrorMessage());
//        }
//        else 
//            System.out.print(r.getMessage());

    }
}
