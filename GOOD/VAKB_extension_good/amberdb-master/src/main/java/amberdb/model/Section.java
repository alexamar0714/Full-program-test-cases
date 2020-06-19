package amberdb.model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import amberdb.relation.ExistsOn;

/**
 * A section (article, chapter etc) of a printed work. May exist on multiple
 * pages and may span multiple pages.
 */
@TypeValue("Section")
public interface Section extends Work {
        @Property("metsId")
        public String getMetsId();
    
        @Property("metsId")
        public void setMetsId(String metsId);

	@Adjacency(label = ExistsOn.label, direction = Direction.OUT)
	public Iterable<Page> getExistsOnPages();

	@Adjacency(label = ExistsOn.label, direction = Direction.OUT)
	public void addPage(final Page page);
	   
	@GremlinGroovy("it.outE.has('label', 'existsOn').has('relOrder', idx).inV")
	public Page getPage(@GremlinParam("idx") int idx);
	
	@GremlinGroovy("it.outE.has('label', 'existsOn').inV.loop(3){true}{true}.has('subType', subType)")
    public Iterable<Work> getLeafs(@GremlinParam("subType") String subType);
	   
	@GremlinGroovy(value="it.out('existsOn')[p-1].inE.id.toList()[0].toLong()", frame=false)
	public long getExistsOnRef(@GremlinParam("p") int position);
	
	@JavaHandler
	public int countExistsOns();

    /**
     * This method handles the JSON deserialisation of the captions Property
     */
    @JavaHandler
    List<String> getCaptions();

    /**
     * This method handles the JSON serialisation of the captions Property
     */
    @JavaHandler
    void setCaptions(List<String> list);
    
    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getCaptions to get this property
     */
	@Property("captions")
	public String getJSONCaptions();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setCaptions to set this property
     */
	@Property("captions")
	public void setJSONCaptions(String captions);

	@Property("advertising")
	public Boolean isAdvertising();

	@Property("advertising")
	public void setAdvertising(Boolean advertising);

	@Property("illustrated")
	public Boolean isIllustrated();

	@Property("illustrated")
	public void setIllustrated(Boolean illustrated);

	@Property("printedPageNumber")
	public String getPrintedPageNumber();

	@Property("printedPageNumber")
	public void setPrintedPageNumber(String printedPageNumber);
	
	abstract class Impl extends Work.Impl implements JavaHandlerContext<Vertex>, Section {
	    private static final Logger log = LoggerFactory.getLogger(Section.Impl.class);
	    static ObjectMapper mapper = new ObjectMapper();
	    
        public int countExistsOns() {
            return (existsOns() == null)? 0 : existsOns().size();
        }
	    
        private List<Edge> existsOns() {
            return (gremlin().outE(ExistsOn.label) == null)? null: gremlin().outE(ExistsOn.label).toList();
        }

        @Override
        public List<String> getCaptions() {
            return deserialiseJSONString(getJSONCaptions());
        }

        @Override
        public void setCaptions(List<String> list) {
            try {
                setJSONCaptions(serialiseToJSON(list));
            } catch (JsonProcessingException e) {
                log.error("Error setting captions for section: " + getObjId(), e);
            }
        }

	}
}
