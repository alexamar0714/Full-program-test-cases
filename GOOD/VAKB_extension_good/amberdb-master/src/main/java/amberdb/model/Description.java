package amberdb.model;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeField;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import java.util.Set;

@TypeField("type")
@TypeValue("Description")
public interface Description extends VertexFrame{
    @Property("type")
    public String getType();

    @JavaHandler
    abstract public long getId();

    // TODO amuller This may need to be implemented to return the set of properties that cannot be set.
    // I haven't done this because I couldn't think of a reasonable way of doing this that did not involve
    // reflection and messyness.
    /**
     * Get the set of keys for properties that are set on this object.
     *
     * This does not return the set of properties that can be set on this model.
     *
     * @return the keyset of properties currently set on this object.
     */
    @JavaHandler
    public Set<String> getPropertyKeySet();

    public abstract class Impl implements JavaHandlerContext<Vertex>, Description {
        @Override
        public Set<String> getPropertyKeySet() {
            return this.asVertex().getPropertyKeys();
        }

        @Override
        public long getId() {
            return toLong(asVertex().getId());
        }

        private long toLong(Object x) {
            // tinkergraph converts ids to strings
            if (x instanceof String) {
                return Long.parseLong((String) x);
            }
            return (long) x;
        }
    }
}
