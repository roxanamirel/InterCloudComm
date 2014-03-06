/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author oneadmin
 */
public class TemplateModel implements Serializable{

    private String name;
    private int cpu;
    private int memory;
    private List<Disk> disks;
    private Graphics graphics;
    private Network nic;
    private OS os;

    public TemplateModel(String name, int cpu, int memory,
            List<Disk> disk, Graphics graphics, Network nic, OS os) {
        this.name = name;
        this.cpu = cpu;
        this.memory = memory;
        this.disks = disk;
        this.graphics = graphics;
        this.nic = nic;
        this.os = os;
    }

    public TemplateModel() {
    }
    
    public String createStringFromDisks(){
        String disksString="";
        for(Disk disk:this.disks){
            disksString = disksString+TemplateTagsEnum.DISK.getType() + "= [" 
                    +TemplateTagsEnum.DISK.getFirstChild() + " = "
                    +disk.getDiskID()+","+
                    TemplateTagsEnum.DISK.getSecondChild()+ " = "
                    +disk.getImage().getImageId()+","+
                    TemplateTagsEnum.DISK.getThirdChild()+ " = "
                    +disk.getImage().getImageType()+
                    "]\n";
                
         }
     
      return disksString;
    }

    @Override
    public String toString() {
        return TemplateTagsEnum.NAME.getType() + " = " + this.getName() + "\n"
                + TemplateTagsEnum.CPU.getType() + " = " + this.getCpu() + "\n"
                + TemplateTagsEnum.MEMORY.getType() + " = " + this.getMemory() + "\n"
                + createStringFromDisks()
                + TemplateTagsEnum.GRAPHICS.getType() + "= [ "
                + TemplateTagsEnum.GRAPHICS.getFirstChild() + " = " + this.getGraphics().getListen() + ", "
                + TemplateTagsEnum.GRAPHICS.getSecondChild() + " = " + this.getGraphics().getType() + "] \n"
                + TemplateTagsEnum.NIC.getType() + " = [ "
                + TemplateTagsEnum.NIC.getFirstChild() + " = " + this.nic.getNetworkId() + "] \n"
                + TemplateTagsEnum.OS.getType() + " = ["
                + TemplateTagsEnum.OS.getFirstChild() + " = " + this.os.getArch()+","
                + TemplateTagsEnum.OS.getSecondChild() + " = " + this.os.getBoot()
                + "]";
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public List<Disk> getDisks() {
        return disks;
    }

    public void setDisks(List<Disk> disk) {
        this.disks = disk;
    }
    
    public Graphics getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Network getNic() {
        return nic;
    }

    public void setNic(Network nic) {
        this.nic = nic;
    }

    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }
}
