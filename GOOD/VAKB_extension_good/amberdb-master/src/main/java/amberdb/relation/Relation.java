package amberdb.relation;

import amberdb.model.Node;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.OutVertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

/**
 * Relates is an edge/path representation of 
 * Relationship in Jelly repository.  It
 * provides traversal access in relationship
 * mgmt. 
 */
public interface Relation extends EdgeFrame {

	@OutVertex
    Node getTarget();
    
    @InVertex
    Node getSource();
    
    @Property("edge-order")
    public int getRelOrder();
    
    @Property("edge-order")
    public void setRelOrder(int relOrder);
    
    abstract class Impl implements Relation,JavaHandlerContext<Edge> {
    }
}
