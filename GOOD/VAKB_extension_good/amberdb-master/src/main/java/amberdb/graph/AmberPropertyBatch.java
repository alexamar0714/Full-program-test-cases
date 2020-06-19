package amberdb.graph;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AmberPropertyBatch {

    
    List<Long>   id       = new ArrayList<Long>();
    List<String> name     = new ArrayList<String>();
    List<String> type     = new ArrayList<String>();
    List<byte[]> value    = new ArrayList<byte[]>();

    
    void add(Long id, Map<String, Object> properties) {
        if (properties == null) return;
        for (String name : properties.keySet()) {

            // MySQL seems to map an empty string to null in a blob field context
            // This should eliminate this issue by simply not saving the empty str
            Object value = properties.get(name);
            if (value.equals("")) continue;

            this.id.add(id);
            this.name.add(name);
            this.type.add(DataType.forObject(value));
            this.value.add(AmberProperty.encode(value));
        }
    }


    public void clear() {
        id.clear();
        name.clear();
        type.clear();
        value.clear();
    }
}
