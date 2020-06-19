package amberdb.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class AliasItem {
    
    String pi;
    String type;
    String title;
    
    public AliasItem(String pi, String type, String title){
        this.pi = pi;
        this.type = type;
        this.title = title;
    }
    
    public String getPi() {
        return pi;
    }
    public void setPi(String pi) {
        this.pi = pi;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). 
            append(pi).
            append(type).
            append(title).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof AliasItem)){
            return false;
       }
        if (obj == this){
            return true;
        }
        AliasItem rhs = (AliasItem) obj;
        return new EqualsBuilder().
            append(pi, rhs.pi).
            append(type, rhs.type).
            append(title, rhs.title).
            isEquals();
    }
    
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append(" pi: " + pi);
        result.append(" type: " + type );
        result.append(" title: " + title );
        return result.toString();
    }

}
