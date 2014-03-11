/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package services.interfaces;

import java.util.List;
import models.TemplateModel;
import org.opennebula.client.OneResponse;

/**
 *
 * @author oneadmin
 */
public interface ITemplateService {
    
    public void allocateTemplate(TemplateModel tm, List<OneResponse> oneResponses);
}
