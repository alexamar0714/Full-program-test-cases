package amberdb.sql;

import javax.persistence.Column;
import javax.persistence.Entity;

import amberdb.util.NaturalSort;


@Entity
public class ListLu implements Comparable<Object>{
    @Column
    Long id;
    @Column
    String name;
    @Column
    String value;
    @Column
    String code;
    @Column
    String deleted;
    
    public ListLu(String name, String value) {
        this.name = name;
        this.value = value;
        this.code = value;
        this.deleted = "N";
    }
    
    public ListLu(String name, String code, String value) {
        this.name = name;
        this.code = code;
        this.value = value;        
        this.deleted = "N";
    }

    protected ListLu(Long id, String name, String value, String code, String deleted) {
        this.id = id;
        this.name = name;
        this.value = value;   
        if (code == null) 
            this.code = this.value;
        else
            this.code = code;
        this.deleted = deleted;
    }
    
    /**
     * Specifically used to compare lookup values
     */
    @Override
    public int compareTo(Object object) {               
        ListLu lookup = (ListLu)object;                     
        String t1 = (value == null ? "" : value);
        String t2 = (lookup.getValue() == null ? "" : lookup.getValue());
       return NaturalSort.compareNaturalIgnoreCaseAscii(t1, t2);
        
    } 
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDeleted() {
        return value;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public boolean isDeleted() {
        return (deleted != null && (deleted.equalsIgnoreCase("Y") || deleted.equalsIgnoreCase("D")));
    }
    
    public boolean isReadOnly() {
        return (deleted != null && deleted.equalsIgnoreCase("R"));
    }
}
