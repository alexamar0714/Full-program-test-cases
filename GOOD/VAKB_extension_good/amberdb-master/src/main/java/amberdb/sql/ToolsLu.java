package amberdb.sql;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ToolsLu {
    @Column
    Long id;
    @Column
    String name;
    @Column
    String code;
    @Column
    String resolution;
    @Column
    String serialNumber;
    @Column
    String notes;
    @Column
    Long materialTypeId;
    @Column
    String materialType;
    @Column 
    Long toolTypeId;
    @Column
    String toolType;
    @Column
    Long toolCategoryId;
    @Column
    String toolCategory;
    @Column
    Long commitTime;
    @Column
    String commitUser;
    @Column
    boolean deleted;
    
    public ToolsLu(String commitUser) {
        this.commitUser = commitUser;
        deleted = false;
    }
    
    protected ToolsLu(Long id, String name, String resolution, String serialNumber, String notes,
                   Long materialTypeId, String materialType, 
                   Long toolTypeId, String toolType, 
                   Long toolCategoryId, String toolCategory, 
                   Long commitTime, String commitUser, String deleted) {
        this.id = id;
        this.code = id.toString();
        this.name = name;
        this.resolution = resolution;
        this.serialNumber = serialNumber;
        this.notes = notes;
        this.materialTypeId = materialTypeId;
        this.materialType = materialType;
        this.toolTypeId = toolTypeId;
        this.toolType = toolType;
        this.toolCategoryId = toolCategoryId;
        this.toolCategory = toolCategory;
        this.commitTime = commitTime;
        this.commitUser = commitUser;
        this.deleted = (deleted == null)? false : (deleted.equalsIgnoreCase("Y") || deleted.equalsIgnoreCase("D"));
    }

    public Long getId() {
        return id;
    }

    public Long getToolTypeId() {
        return toolTypeId;
    }
    
    public void setToolTypeId(Long toolTypeId) {
        this.toolTypeId = toolTypeId;
    }
    
    public Long getMaterialTypeId() {
        return materialTypeId;
    }
    
    public void setMaterialTypeId(Long materialTypeId) {
        this.materialTypeId = materialTypeId;
    }
    
    public Long getToolCategoryId() {
        return toolCategoryId;
    }
    
    public void setToolCategoryId(Long toolCategoryId) {
        this.toolCategoryId = toolCategoryId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getResolution() {
        return resolution;
    }
    
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMaterialType() {
        return materialType;
    }

    public String getToolType() {
        return toolType;
    }
    
    public String getToolCategory() {
        return toolCategory;
    }
    
    public Long getCommitTime() {
        return commitTime;
    }
    
    public String getFormatedCommitTime(String fmt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
        return dateFormat.format(new Date(getCommitTime())); 
    }
    
    public String getCommitUser() {
        return commitUser;
    }
    
    public void setCommitUser(String commitUser) {
        this.commitUser = commitUser;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
    
    public boolean isEmptyRecord() {
        return (isEmpty(name) && isEmpty(resolution) && isEmpty(serialNumber) && isEmpty(notes)
                && isEmpty(toolType) && isEmpty(toolCategory) && isEmpty(materialType));
    }
    
    private boolean isEmpty(String str) {
        return (str == null || str.isEmpty());
    }
}
