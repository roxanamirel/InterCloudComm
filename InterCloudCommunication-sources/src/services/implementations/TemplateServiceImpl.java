/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package services.implementations;

import client.OpenNebulaClient;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import models.TemplateModel;
import org.opennebula.client.OneResponse;
import org.opennebula.client.template.Template;
import org.opennebula.client.template.TemplatePool;
import org.opennebula.client.vnet.VirtualNetwork;
import org.opennebula.client.vnet.VirtualNetworkPool;
import services.interfaces.ITemplateService;

/**
 *
 * @author oneadmin
 */
public class TemplateServiceImpl implements ITemplateService {

    @Override
    public void allocateTemplate(TemplateModel tm, List<OneResponse> oneResponses) {
        tm.setName(UUID.randomUUID().toString());
        for (int i = 0; i < oneResponses.size(); i++) {
            int imageId = Integer.parseInt(oneResponses.get(i).getMessage());
            tm.getDisks().get(i).getImage().setImageId(imageId);            
        }
        tm.getNic().setNetworkId(getFirstVirtualNetworkId());
        
        OneResponse templateResponse =
                Template.allocate(OpenNebulaClient.getInstance(), tm.toString());
        
        if (!templateResponse.isError())
        {
            TemplatePool tp = new TemplatePool(OpenNebulaClient.getInstance());
            System.out.println(
                    "Template created with ID = " 
                    + templateResponse.getIntMessage());
            tp.getById(templateResponse.getIntMessage()).instantiate();
            
        }
    }
    
    private int getFirstVirtualNetworkId() {
        VirtualNetworkPool vnp = new VirtualNetworkPool(OpenNebulaClient.getInstance());
        vnp.info();
        Iterator<VirtualNetwork> iterator= vnp.iterator();
        return Integer.parseInt(iterator.next().getId());
    }

    
}
