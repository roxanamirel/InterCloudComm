/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services.interfaces;

import client.OpenNebulaClient;
import java.util.List;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.image.Image;

/**
 *
 * @author oneadmin
 */
public interface IImageService {
    
    public List<Image> getAllImages();
    
    public Image getImageById(int id);
    
    public boolean contains(String imageName);
    
    public OneResponse allocate(Client client,String description);
}
