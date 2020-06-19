package amberdb.model;

import amberdb.relation.ExistsOn;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Page")
public interface Page extends Work {

    @Adjacency(label = ExistsOn.label, direction = Direction.IN)
    public Iterable<Section> getSections();
}
