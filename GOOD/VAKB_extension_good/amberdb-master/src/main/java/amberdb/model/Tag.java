package amberdb.model;

import amberdb.relation.Tags;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

/**
 * A named vertex that identifies/groups objects within the amberdb. Identified objects share a 'tags' edge with this vertex. 
 */

@TypeValue("Tag")
public interface Tag extends Node {

    @Property("name")
    public String getName();

    @Property("name")
    public void setName(String name);

    @Property("description")
    public String getDescription();

    @Property("description")
    public void setDescription(String description);

    @Adjacency(label = Tags.label, direction = Direction.OUT)
    public void addNode(final Node node);
    
    @Adjacency(label = Tags.label, direction = Direction.OUT)
    public void removeNode(final Node node);

    @Adjacency(label = Tags.label, direction = Direction.OUT)
    public Iterable<Node> getTaggedObjects();
}
