/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.implementations;

import helper.template.TemplateHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.DataCenter;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import services.interfaces.IInterCloudMigrationService;
import services.interfaces.IVMService;
import models.Disk;
import models.TemplateModel;
import tcp.TCPClient;
import utils.parsers.OneResponseParser;

/**
 *
 * @author oneadmin
 */
public class InterCloudMigrationServiceImpl implements IInterCloudMigrationService {

    @Override
    public void migrateToDatacenter(VirtualMachine virtualMachine, DataCenter dataCenter) {

        TemplateModel tm
                = new TemplateHelper().createTemplateModel(virtualMachine);
        System.out.println(tm.toString());

        List<String> imageNames = createImagesFromDisks(tm.getDisks(), virtualMachine);
        List<String> imagePaths = getImagesPaths(imageNames);
        
        destroyVirtualMachine(virtualMachine);
        sendImagesWithSSH(imagePaths, imageNames, dataCenter);
        
        remoteRestore(dataCenter, tm, imageNames);
    }

    private List<String> getImagesPaths(List<String> imageNames) {
        List<String> imagesPaths = new ArrayList<>();
        for (String name : imageNames) {
            String imagePath = OneResponseParser.getImagePath(name);            
            imagesPaths.add(imagePath);
            System.out.println(imagePath);
        }
        return imagesPaths;
    }

    private List<String> createImagesFromDisks(List<Disk> disks, VirtualMachine vm) {
        List<String> imageNames = new ArrayList<>();
        try {
            for (Disk disk : disks) {
                String imageName = UUID.randomUUID().toString();
                System.out.println("Saving image: " + imageName);

                OneResponse saveDiskRespone = vm.savedisk(disk.getDiskID(), imageName);
                if (saveDiskRespone.isError()) {
                    System.out.println("Error: " + saveDiskRespone.getErrorMessage());
                } else {
                    imageNames.add(imageName);
                }
                Thread.sleep(1000);
            }
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(InterCloudMigrationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imageNames;
    }

    private void destroyVirtualMachine(VirtualMachine virtualMachine) {
        String command = "onevm shutdown --hard " + virtualMachine.getId();
        System.out.println(command);
        executeCommand(command);
        virtualMachine.info();
        System.out.println("VM state is: " + virtualMachine.status());

        if (virtualMachine.status().equals("shut")) {
            try {
                waitForVMToBeDeleted(virtualMachine);
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(InterCloudMigrationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void waitForVMToBeDeleted(VirtualMachine virtualMachine) {
        IVMService ivms = new VMServiceImpl();
        System.out.println("Waiting for the machine to be shutdown...");
        while (ivms.contains(virtualMachine)) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(InterCloudMigrationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("The virtual machine has been shutdown");
    }

    private List<String> sendImagesWithSSH(
            List<String> imagePaths,
            List<String> imageNames,
            DataCenter dataCenter) {

        List<String> output = new ArrayList<>();
        for (int index = 0; index < imagePaths.size(); index++) {
            StringBuilder command = new StringBuilder();
            command.append("sshpass -p '");
            command.append(dataCenter.getPuppeteer().getPassword());
            command.append("'");
            command.append(" scp ");
            command.append("\"");
            command.append(imagePaths.get(index));
            command.append("\"");
            command.append(" \"");
            command.append(dataCenter.getPuppeteer().getImgPath());
            command.append(imageNames.get(index));
            command.append("\"");

            System.out.println("Executing: " + command.toString());
            executeCommandWithPassword(command.toString());
        }
        for (String out : output) {
            System.out.println(out);
        }

        System.out.println("Images were sent to " + dataCenter.getPuppeteer().getImgPath());
        return output;
    }

    private String executeCommand(String command) {

        StringBuilder output = new StringBuilder();

        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    private void executeCommandWithPassword(String command) {
        String[] SHELL_COMMAND = {"/bin/sh", "-c", command};
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(SHELL_COMMAND);
            process.waitFor();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("done");
    }

    private void remoteRestore(DataCenter dataCenter, TemplateModel tm, List<String> imageNames) {
        new TCPClient().restore(dataCenter, tm, imageNames);        
    }
}
