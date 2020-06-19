package amberdb.query;

import org.apache.commons.collections.CollectionUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.LongMapper;

import java.util.List;

/**
 * @see amberdb.query.PropertyQueryAssemblerTest
 *
 * Assemble a SQL using work properties (name/value pair).
 */
public class PropertyQueryAssembler {
    private static String SQL_TEMPLATE = "select id from work where ";

    private List<WorkProperty> workProperties;

    public PropertyQueryAssembler(List<WorkProperty> workProperties){
        if (CollectionUtils.isEmpty(workProperties)){
            throw new IllegalArgumentException("Work property workProperties cannot be empty");
        }
        this.workProperties = workProperties;
    }


    private String whereClauseForNameAndValuePair(){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< workProperties.size(); i++){
            WorkProperty workProperty = workProperties.get(i);
            if (i > 0) {
                sb.append("and ");
            }
            sb.append(workProperty.getName() + "=? ");
        }
        return sb.toString();
    }


    public String sql(){
        return SQL_TEMPLATE + whereClauseForNameAndValuePair();
    }

    public Query query(Handle h){
        Query q = h.createQuery(sql());
        for (int i=0; i< workProperties.size(); i++){
            q.bind(i, workProperties.get(i).getValue());
        }
        return q.map(LongMapper.FIRST);
    }


}
