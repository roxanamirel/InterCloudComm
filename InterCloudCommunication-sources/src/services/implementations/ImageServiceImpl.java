/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.implementations;

import client.OpenNebulaClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Disk;
import models.ImageState;
import models.TemplateModel;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.image.Image;
import org.opennebula.client.image.ImagePool;
import services.interfaces.IImageService;
import utils.config.GeneralConfigurationManager;

/**
 *
 * @author oneadmin
 */
public class ImageServiceImpl implements IImageService {

    @Override
    public List<Image> getAllImages() {

        ImagePool imagePool = new ImagePool(OpenNebulaClient.getInstance());
        imagePool.info();
        List<Image> images = new ArrayList<>();
        Iterator<Image> imageIterator = imagePool.iterator();

        while (imageIterator.hasNext()) {
            images.add(imageIterator.next());
        }
        return images;
    }

    @Override
    public Image getImageById(int id) {

        ImagePool imagePool = new ImagePool(OpenNebulaClient.getInstance());
        imagePool.info();

        return imagePool.getById(id);
    }

    @Override
    public boolean contains(String imageName) {

        ImagePool imagePool = new ImagePool(OpenNebulaClient.getInstance());
        imagePool.info();
        Iterator<Image> imageIterator = imagePool.iterator();

        while (imageIterator.hasNext()) {
            Image image = imageIterator.next();
            OneResponse or = image.info();
            if (image.getName().equals(imageName) && image.isEnabled()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public OneResponse allocate(Client client, String description, int datastoreId) {

        return Image.allocate(client, description, datastoreId);


    }

    @Override
    public List<OneResponse> allocateImages(TemplateModel tm) {
        List<OneResponse> oneResponses = new ArrayList<>();
        IImageService imageService = new ImageServiceImpl();
        for (Disk disk : tm.getDisks()) {
            models.Image image = disk.getImage();
            image.setDescription("this is a test");
            image.setImagePath(GeneralConfigurationManager.getIMAGE_PATH_LOCATION() + image.getName());
            OneResponse r = imageService.allocate(OpenNebulaClient.getInstance(), image.toString(), 108);
            
            if (r.isError()) {
                System.out.println("An error has occured " + r.getErrorMessage());
            } else {
                System.out.print(r.getMessage());
            }
            oneResponses.add(r);
        }
        for(OneResponse oneResponse:oneResponses){
            Image image = imageService.getImageById(oneResponse.getIntMessage());
            image.info();
            while(!image.stateString().equals(ImageState.READY.getValue())){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ImageServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
                image.info();
            }
            
        }
        return oneResponses;
    }
}
