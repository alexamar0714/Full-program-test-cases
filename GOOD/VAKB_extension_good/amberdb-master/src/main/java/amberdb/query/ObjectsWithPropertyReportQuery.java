package amberdb.query;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;

import com.tinkerpop.blueprints.Vertex;

import amberdb.graph.AmberGraph;
import amberdb.graph.AmberQueryBase;


public class ObjectsWithPropertyReportQuery extends AmberQueryBase {


    public ObjectsWithPropertyReportQuery(AmberGraph graph) {
        super(graph);
    }

    
    public List<Vertex> generateDuplicateAliasReport(String collectionName) {

        List<Vertex> vertices;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("DROP " + graph.getTempTableDrop() + " TABLE IF EXISTS vp; CREATE TEMPORARY TABLE vp (id BIGINT) " + graph.getTempTableEngine() + ";");
            Update q = h.createStatement(
                    "INSERT INTO vp (id) \n"
                            + " SELECT DISTINCT id \n"    
                            + " FROM work \n"              
                            + " WHERE \n"                 
                            + " type in ( 'Work', 'EADWork', 'Section', 'Page') \n"
                            + " AND collection = :collection \n"
                            + " AND alias is not null \n"  
                            + " UNION ALL \n"             
                            + " SELECT DISTINCT p.id \n"  
                            + " FROM work p, work cp, flatedge ed \n"
                            + " WHERE \n"                 
                            + " p.type = 'Copy' \n"        
                            + " AND cp.collection = :collection \n"
                            + " AND p.alias is not null"
                            + " AND p.id = ed.v_out"    
                            + " AND ed.v_in=cp.id "     
            );

            q.bind("collection", collectionName);
            q.execute();
            h.commit();

            Map<Long, Map<String, Object>> propMaps = getVertexPropertyMaps(h, "vp", "id");
            vertices = getVertices(h, graph, propMaps, "vp", "id", "id");
        }
        return vertices;
    }
    

    public List<Vertex> generateExpiryReport(Date expiryYear, String collectionName) {

        List<Vertex> vertices;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("DROP "
                    + graph.getTempTableDrop()
                    + " TABLE IF EXISTS er;CREATE TEMPORARY TABLE er (id BIGINT) "
                    + graph.getTempTableEngine() + ";");
            Update q = h
                    .createStatement("INSERT INTO er (id) \n"
                            + " SELECT DISTINCT p.id  \n"
                            + " FROM work p \n"
                            + " WHERE p.type in ('Work', 'EADWork', 'Section', 'Page') \n"
                            + " AND internalAccessConditions != 'Closed'  \n"
                            + " AND p.collection = :collection \n"
                            + " AND p.expiryDate = :expiry \n");

            q.bind("expiry", expiryYear);
            q.bind("collection", collectionName);
            q.execute();
            h.commit();

            Map<Long, Map<String, Object>> propMaps = getVertexPropertyMaps(h,
                    "er", "id");
            vertices = getVertices(h, graph, propMaps, "er", "id", "id");

        }
        return vertices;
    }

    public static long getTimeMillis(java.sql.Timestamp ts) {
        return ts.getTime();
    }
    
  
}
