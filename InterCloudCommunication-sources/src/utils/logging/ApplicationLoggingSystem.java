/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import utils.config.Configurations;

/**
 *
 * @author oneadmin
 */
public class ApplicationLoggingSystem {
    
    private static ApplicationLoggingSystem instance = null;
    private final File log;
    
    protected ApplicationLoggingSystem() {
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = dateFormat.format(new Date());        
        String filePath = Configurations.LogPath + dateTime + ".txt";         
        log = new File(filePath);        
    }
    
    public static ApplicationLoggingSystem getInstance() {
        if (instance == null)
        {
            instance = new ApplicationLoggingSystem();
        }
        return instance;
    }
    
    public void LogInfo(String message) {
        try {
            FileWriter fileWriter = new FileWriter(log);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append(message + "\n");
            System.out.println(message);
            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
