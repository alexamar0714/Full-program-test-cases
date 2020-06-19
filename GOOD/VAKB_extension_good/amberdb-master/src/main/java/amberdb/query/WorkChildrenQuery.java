package amberdb.query;

import amberdb.AmberSession;
import amberdb.PIUtil;
import amberdb.enums.BibLevel;
import amberdb.enums.CopyRole;
import amberdb.graph.AmberQueryBase;
import amberdb.model.Section;
import amberdb.model.Work;
import amberdb.model.sort.SortField;
import amberdb.model.sort.WorkComparator;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.IntegerMapper;
import org.skife.jdbi.v2.util.LongMapper;
import org.skife.jdbi.v2.util.StringMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorkChildrenQuery extends AmberQueryBase {

    private AmberSession sess;

    static final int TEMP_TABLE_SORT_FIELD_LENGTH = 256;

    public WorkChildrenQuery(AmberSession sess) {
        super(sess.getAmberGraph());
        this.sess = sess;
    }

    private static String workNotSectionInList;
    private static String fileInList;
    private static String descInList;
    static {
        workNotSectionInList = "('" + StringUtils.join(new String[] { "Work", "Page", "EADWork" }, "', '") + "')";
        fileInList = "('" + StringUtils.join(new String[] { "File", "ImageFile", "SoundFile", "MovingImageFile" }, "', '") + "')";
        descInList = "('" + StringUtils.join(new String[] { "Description", "CameraData", "EADEntity", "EADFeature", "GeoCoding", "IPTC" }, "', '") + "')";
    }
    
    public List<Work> getChildRangeSorted(Long workId, int start, int num, String sortPropertyName, boolean sortForward) {
        if (StringUtils.isBlank(sortPropertyName) || SortField.fromString(sortPropertyName) == null) {
            return getChildRange(workId, start, num);
        }
        List<Work> children = getChildren(getAddChildrenWorkSortBySql(workId, start, num, sortPropertyName, sortForward));
        Collections.sort(children, new WorkComparator(sortPropertyName, sortForward));    
        return children;
    }
    
    public List<Work> getChildRange(Long workId, int start, int num){
        return getChildren(getAddChildrenWorksSql(workId, start, num));
    }
    
    private List<Work> getChildren(String addChildrenWorksSql) {

        StringBuilder s = new StringBuilder();
        List<Work> children =  new ArrayList<>();
        String tDrop = graph.getTempTableDrop();
        String tEngine = graph.getTempTableEngine();
        String charset = graph.getTempTableCharSet();
        // create double buffered temp tables because mysql
        // can't open the same temp table twice in a query
        s.append(
        "DROP " + tDrop + " TABLE IF EXISTS v1; " +
                "DROP " + tDrop + " TABLE IF EXISTS v2; " +
                "CREATE TEMPORARY TABLE v1 (id BIGINT, obj_type CHAR(1), ord BIGINT, nullOrder BIGINT, sortField VARCHAR(" + TEMP_TABLE_SORT_FIELD_LENGTH + ")) " + tEngine + " " + charset + ";" +
                "CREATE TEMPORARY TABLE v2 (id BIGINT, obj_type CHAR(1), ord BIGINT, nullOrder BIGINT, sortField VARCHAR(" + TEMP_TABLE_SORT_FIELD_LENGTH + ")) " + tEngine + " " + charset + ";");
        
        // add children Works excluding Sections with the limits specified on the range returned
        s.append(addChildrenWorksSql);
        
        // get their copies
        s.append(
            "INSERT INTO v2 (id, obj_type) " +
            "SELECT DISTINCT v.id, 'C' " +
            "FROM node v, flatedge e, work p, v1 " +
            "WHERE e.v_in = v1.id " +
            " AND e.v_out = v.id " +
            " AND e.label = 'isCopyOf' " +
            " AND p.id = v.id " +
            " AND p.type = 'Copy'; ");
        
        // get files
        s.append(
            "INSERT INTO v1 (id, obj_type) " +
            "SELECT DISTINCT v.id, 'F' " +
            "FROM node v, flatedge e, work p, v2 " +
            "WHERE e.v_in = v2.id " +
            " AND e.v_out = v.id " +
            " AND e.label = 'isFileOf' " +
            " AND p.id = v.id " +
            " AND p.type IN " + fileInList + "; ");

        // move everything in v2 to v1
        s.append(
            "INSERT INTO v1 (id, obj_type) " +
            "SELECT v2.id, v2.obj_type " +
            "FROM v2; " +
            "DELETE FROM v2; ");
 
        // get descriptions
        s.append(
            "INSERT INTO v2 (id, obj_type) " +
            "SELECT DISTINCT v.id, 'D' " +
            "FROM node v, flatedge e, work p, v1 " +
            "WHERE e.v_in = v1.id " +
            " AND e.v_out = v.id " +
            " AND e.label = 'descriptionOf' " +
            " AND p.id = v.id " +
            " AND p.type IN " + descInList + "; ");

        // finally move all the descriptions into v1 also
        s.append(
            "INSERT INTO v1 (id, obj_type) " +
            "SELECT v2.id, v2.obj_type " +
            "FROM v2; ");

        List<Vertex> vertices = null;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.createStatement(s.toString())
                    .bind("workVal", "Work")
                    .bind("pageVal", "Page")
                    .bind("eadWorkVal", "EADWork")
                    .execute();
            h.commit();
            
            Map<Long, Map<String, Object>> propMaps = getVertexPropertyMaps(h, "v1", "id");
            vertices = getVertices(h, graph, propMaps, "v1", "id", "ord");
            
            for (Vertex v : vertices) {
                String type = v.getProperty("type");
                if (type.matches("(Work|Page|EADWork)")) {
                    children.add(sess.getGraph().frame(v, Work.class));
                }
            }
        }
        return children;
    }

    private String getAddChildrenWorksSql(Long workId, int start, int num) {
        return "INSERT INTO v1 (id, obj_type, ord) " +
        "SELECT DISTINCT v.id, 'W', e.edge_order " +
        "FROM node v, flatedge e, work p " +
        "WHERE e.v_in = " + workId + " " +
        " AND e.v_out = v.id " +
        " AND e.label = 'isPartOf' " +
        " AND p.id = v.id " +
        " AND p.type IN " + workNotSectionInList + " " +
        " ORDER BY e.edge_order " +
        " LIMIT " + start + "," + num + "; ";
    }
    
    private String getAddChildrenWorkSortBySql(Long workId, int start, int num, String sortBy, boolean asc) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO v1 (id, obj_type, ord, nullOrder, sortField) "
                + " SELECT DISTINCT v.id, 'W' obj_type, e.edge_order, case when p1." + sortBy + " is null then 1 else 0 end, substring(p1." + sortBy + ", 1, " + TEMP_TABLE_SORT_FIELD_LENGTH + ")"
                + " FROM node v "
                + " INNER JOIN work p1 "
                + " ON p1.id = v.id AND p1.type IN " + workNotSectionInList
                + " INNER JOIN flatedge e "
                + " ON e.v_out = v.id AND e.label = 'isPartOf' "
                + " WHERE e.v_in = " + workId
                + " ORDER BY case when p1." + sortBy + " is null then 1 else 0 end, substring(p1." + sortBy + ", 1, " + TEMP_TABLE_SORT_FIELD_LENGTH + ") "
        ); //null always last whether asc or desc
        if (!asc){
            sb.append(" desc ");
        }
        sb.append(" LIMIT " + start + "," + num + "; ");
        return sb.toString();
    }

    public List<Section> getSections(Long workId) {

        StringBuilder s = new StringBuilder();
        List<Section> sections =  new ArrayList<>();
        String tDrop = graph.getTempTableDrop();
        String tEngine = graph.getTempTableEngine();

        s.append(
            "DROP " + tDrop + " TABLE IF EXISTS v1; \n" +
            "CREATE TEMPORARY TABLE v1 (id BIGINT, obj_type CHAR(1), ord BIGINT)" + tEngine + "; \n");

        // add children Sections
        s.append(
            "INSERT INTO v1 (id, obj_type, ord) " +
            "SELECT DISTINCT v.id, 'W', e.edge_order " +
            "FROM node v, flatedge e, work p " +
            "WHERE e.v_in = " + workId +
            " AND e.v_out = v.id " +
            " AND e.label = 'isPartOf' " +
            " AND p.id = v.id " +
            " AND p.type = 'Section' " +
            " ORDER BY e.edge_order ");

        List<Vertex> vertices = null;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.createStatement(s.toString()).execute();
            h.commit();

            Map<Long, Map<String, Object>> propMaps = getVertexPropertyMaps(h, "v1", "id");
            vertices = getVertices(h, graph, propMaps, "v1", "id", "ord");

            for (Vertex v : vertices) {
                sections.add(sess.getGraph().frame(v, Section.class));
            }
        }
        return sections;
    }
    
    public Integer getTotalChildCount(Long workId) {
        Integer numChildren = new Integer(0);
        try (Handle h = graph.dbi().open()) {
            numChildren = h.createQuery(
                    "SELECT COUNT(v_out) " +
                    "FROM node v, flatedge e, work p " +
                    "WHERE e.v_in = :workId " +
                    " AND e.v_out = v.id " +
                    " AND e.label = 'isPartOf' " +
                    " AND p.id = v.id " +
                    " AND p.type IN ('Work', 'Page', 'EADWork'); ")
                    .bind("workId", workId)
                    .map(IntegerMapper.FIRST).first();
        }
        return numChildren;
    }
    
    public List<CopyRole> getAllChildCopyRoles(Long workId) {
        List<String> copyRoleCodes = new ArrayList<>();
        try (Handle h = graph.dbi().open()) {
            copyRoleCodes = h.createQuery(
                    "SELECT DISTINCT p2.copyRole " +
                    "FROM node v, flatedge e, flatedge e2, work p, work p2 " +
                    "WHERE e.v_in = :workId " +
                    " AND e.v_out = v.id " +
                    " AND e.label = 'isPartOf' " +
                    " AND p.id = v.id " +
                    " AND p.type IN ('Work', 'Page', 'EADWork')" +
                    " AND e2.v_in = p.id " +
                    " AND e2.v_out = p2.id " +
                    " AND e2.label = 'isCopyOf' ")
                    .bind("workId", workId)
                    .map(StringMapper.FIRST).list();
        }

        List<CopyRole> copyRoles = new ArrayList<CopyRole>();
        for (String code : copyRoleCodes) {
            copyRoles.add(CopyRole.fromString(code));
        }
        return copyRoles;
    }

    public List<CopyRole> getAllCopyRoles(List<Long> workIds){
        List<CopyRole> copyRoles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(workIds)){
            List<String> copyRoleCodes;
            try (Handle h = graph.dbi().open()) {
                copyRoleCodes = h.createQuery(
                        "select distinct copyRole " +
                                "from work p, flatedge e, node v " +
                                "where p.id = e.v_out and v.id = p.id " +
                                "and e.label = 'isCopyOf' and e.v_in in (" + Joiner.on(",").join(workIds) + ")")
                        .map(StringMapper.FIRST).list();
            }
            for (String code : copyRoleCodes) {
                CopyRole copyRole = CopyRole.fromString(code);
                if (copyRole != null){
                    copyRoles.add(copyRole);
                }
            }
        }
        return copyRoles;
    }

    /**
     * Retrieve children Ids of a list of parent Ids by the specified bib level and specified sub type
     * @param parentIds a list of parent Ids
     * @param bibLevel bibLevel of the child
     * @param subTypes subtype of the child, can be null
     * @return a list of children Ids with the specified bib level and specified sub type
     */
    public List<Long> getChildrenIdsByBibLevelSubType(List<Long> parentIds, BibLevel bibLevel, List<String> subTypes){
        if (CollectionUtils.isNotEmpty(parentIds)){
            try (Handle h = graph.dbi().open()) {
                Query query = getChildrenIdsByBibLevelSubTypeQuery(h, parentIds, bibLevel, subTypes);
                return query.list();
            }
        }
        return new ArrayList<>();
    }

    private Query getChildrenIdsByBibLevelSubTypeQuery(Handle h, List<Long> parentIds, BibLevel bibLevel, List<String> subTypes) {
        if (CollectionUtils.isNotEmpty(subTypes)){
            Function<String,String> addQuotes = new Function<String,String>() {
                @Override public String apply(String s) {
                    return new StringBuilder(s.length()+2).append("'").append(s).append("'").toString();
                }
            };
            String subTypeStr = Joiner.on(", ").join(Iterables.transform(subTypes, addQuotes));
            return h.createQuery(
                    "select distinct v.id from work p1, work p2, flatedge e, node v " +
                            "where p1.id = e.v_out and p2.id = e.v_out and v.id = p1.id and v.id = p2.id " +
                            "and e.label = 'isPartOf' " +
                            "and p1.bibLevel = :bibLevel " +
                            "and p2.subType in ("+ subTypeStr + ") " +
                            "and e.v_in in (" + Joiner.on(",").join(parentIds) + ")")
                    .bind("bibLevel", bibLevel.code())
                    .map(LongMapper.FIRST);
        }
        return h.createQuery(
                "select distinct v.id from work p1, flatedge e, node v " +
                        "where p1.id = e.v_out and v.id = p1.id " +
                        "and e.label = 'isPartOf' " +
                        "and p1.bibLevel = :bibLevel " +
                        "and e.v_in in (" + Joiner.on(",").join(parentIds) + ")")
                .bind("bibLevel", bibLevel.code())
                .map(LongMapper.FIRST);
    }

    /**
     * Retrieve the max edge order of the children of the specified parent Id.
     * If the parent has no children, return 0
     */
    public int getMaxEdgeOrder(Long parentId){
        try (Handle h = graph.dbi().open()) {
            List<Integer> maxEdgeOrder = h.createQuery(
                    "select max(e.edge_order) from flatedge e, node v " +
                            "where v.id = e.v_out " +
                            "and e.v_in = :parentId ")
                    .bind("parentId", parentId)
                    .map(IntegerMapper.FIRST).list();
            if (CollectionUtils.isNotEmpty(maxEdgeOrder)){
                return maxEdgeOrder.get(0);
            }
        }
        return 0;
    }
    
    public List<String> getChildPIs(String workId) {
        try (Handle h = graph.dbi().open()) {
        	List<Long> objIds = h.createQuery("select v_out from flatedge where v_in = :workId and label = 'isPartOf'")
        		.bind("workId", PIUtil.parse(workId))
        		.map(LongMapper.FIRST)
        		.list();
        	List<String> strings = new ArrayList<>(objIds.size());
        	for (Long objId: objIds) {
        		strings.add(PIUtil.format(objId));
        	}
        	return strings;
        }
    }

}
