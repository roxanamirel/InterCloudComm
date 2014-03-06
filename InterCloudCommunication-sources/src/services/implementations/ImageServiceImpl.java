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
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.image.Image;
import org.opennebula.client.image.ImagePool;
import services.interfaces.IImageService;

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
        Iterator<Image> imageIterator =  imagePool.iterator();
        
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
        Iterator<Image> imageIterator =  imagePool.iterator();
        
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
    public OneResponse allocate(Client client, String description) {
         Image image = new Image(256,client);
        return image.allocate(client, description);
        
        
    }
    
    
}
