package amberdb.version;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;


public class TElement {

    
    protected TId id;
    protected Map<String, Object> properties = new HashMap<>();
 
    
    TElement(TId id, Map<String, Object> properties) {
        this.id = id;
        if (properties != null) {
            this.properties = properties;
        }
    }

    
    public TId getId() {
        return id;
    }

    
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String propertyName) {
        return (T) properties.get(propertyName);
    }

    
    public Set<String> getPropertyKeys() {
        Set<String> keys = Sets.newHashSet(properties.keySet());
        return keys;
    }

    
    protected Map<String, Object> getProperties() {
        return properties;
    }
    
    
    protected void replaceProperties(Map<String, Object> newProperties) {
        if (newProperties != null){
            properties = newProperties;    
        }
    }
    
    
    public String propertiesAsString() {
        StringBuilder sb = new StringBuilder("{");
        if (properties != null && properties.size() > 0) {
            for (String key : properties.keySet()) {
                sb.append(key).append(':').append(properties.get(key)).append(", ");
            }
            sb.setLength(sb.length()-2);
        }
        sb.append('}');
        return sb.toString();
    }
}
