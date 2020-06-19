package amberdb.relation;

import java.util.Date;

import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.Property;

import amberdb.model.Party;

/**
 * work acknowledgement
 * @author bsingh
 *
 */

public interface Acknowledge extends Relation {
    public static final String label = "acknowledge";

    @Property("ackType")
    public String getAckType();

    @Property("ackType")
    public void setAckType(String ackType);

    @Property("kindOfSupport")
    public String getKindOfSupport();

    @Property("kindOfSupport")
    public void setKindOfSupport(String kindOfSupport);

    @Property("weighting")
    public Double getWeighting();

    @Property("weighting")
    public void setWeighting(Double weighting);

    @Property("urlToOriginal")
    public String getUrlToOriginal();

    @Property("urlToOriginal")
    public void setUrlToOriginal(String urlToOriginal);

    // date of acknowledgement
    @Property("date")
    public Date getDate();

    // date of acknowledgement
    @Property("date")
    public void setDate(Date date);
    
    @InVertex
    public Party getParty();
}