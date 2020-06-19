package amberdb.sql;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public abstract class Lookups extends Tools { 
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select distinct id, name, value, code, deleted from lookups where deleted = 'N' or deleted is null order by name, code")
    public abstract List<ListLu> findActiveLookups();
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select distinct id, name, value, code, deleted from lookups where deleted = 'D' or deleted = 'Y' order by deleted, name, code")
    public abstract List<ListLu> findDeletedLookups();
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted from lookups where name = :name and deleted = :deleted order by name, value")
    public abstract List<ListLu> findLookupsFor(@Bind("name") String name, @Bind("deleted") String deleted);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted from lookups where name = :name and deleted = :deleted order by code")
    public abstract List<ListLu> findLookupsOrderByCodeFor(@Bind("name") String name, @Bind("deleted") String deleted);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted from lookups where name = :name and (code = :code or value = :code) and (deleted is null or deleted = 'N' or deleted = 'R') order by value")
    public abstract List<ListLu> findActiveLookup(@Bind("name") String name, @Bind("code") String code);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted from lookups where name = :name and (code = :code or value = :code) and (deleted = 'D' or deleted = 'Y') order by deleted, value")
    public abstract List<ListLu> findDeletedLookup(@Bind("name")String name, @Bind("code") String code);

    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted from lookups where id = :id")
    public abstract ListLu findLookup(@Bind("id")Long id);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted "
    		+ "from lookups where name = 'accessCondition' and deleted = 'N' "
    		+ "order by (CASE code WHEN 'Unrestricted' THEN 1 "
    							+ "WHEN 'View Only'	THEN 2 "
    							+ "WHEN 'Onsite Only'THEN 3 "
    							+ "WHEN 'Metadata Only' THEN 4 "
    							+ "WHEN 'Restricted' THEN 5 "
    							+ "ELSE 100 END) ASC")
    public abstract List<ListLu> findSortedAccessConditionLookup();
    
    public ListLu findLookup(String name, String code) {
        List<ListLu> activeLookups = findActiveLookup(name, code);
        if (activeLookups != null && activeLookups.size() > 0)
            return activeLookups.get(0);
        List<ListLu> deletedLookups = findDeletedLookup(name, code);
        if (deletedLookups != null && deletedLookups.size() > 0)
            return deletedLookups.get(0);
        return null;
    }
        
    /*
     * Find a list of active values in the named lookups for reference when creating a new record
     */
    public List<ListLu> findActiveLookupsFor(String name) {
        return findActiveLookupsFor(name, false);
    }
    
    /*
     * Find a list of active values in the named lookups in specified order for reference when creating a new record
     */
    public List<ListLu> findActiveLookupsFor(String name, boolean descOrder) {
        List<ListLu> activeLookups = findLookupsFor(name, "N");
        if (activeLookups == null) return new ArrayList<>();       
        if (descOrder) {
            Collections.sort(activeLookups, Collections.reverseOrder());
        } else {
            Collections.sort(activeLookups);
        }
        return activeLookups;
    }
    
    /*
     * Find a list of active values in the named lookups ordered by 'code'
     */
    public List<ListLu> findActiveLookupsOrderedByCodeFor(String name) {
        List<ListLu> activeLookups = findLookupsOrderByCodeFor(name, "N");
        if (activeLookups == null) return new ArrayList<>();       
        return activeLookups;
    }
    
    /*
     * Find a list of active values and current referenced value (ie. inclCode) in the named lookups for reference when updating a record.
     * The inclCode is catering for reference to an suppressed lookup entry. 
     */
    public List<ListLu> findActiveLookupsFor(String name, String inclCode) {
        List<ListLu> lookupWithCode = findActiveLookup(name, inclCode);
        List<ListLu> activeLookups = findActiveLookupsFor(name);
        if (lookupWithCode == null || lookupWithCode.isEmpty()) {
            activeLookups.add(new ListLu(name, inclCode));
        }
        return activeLookups;
    }
    
    public List<ListLu> findLatestDeletedLookupsFor(String name) {
        List<ListLu> latestDeleted = findLookupsFor(name, "D");
        if (latestDeleted == null) return new ArrayList<>();
        return latestDeleted;
    }
    
    public List<ListLu> findAllDeletedLookupsFor(String name) {
        List<ListLu> deletedLookups = findLookupsFor(name, "Y");
        List<ListLu> latestDeleted = findLookupsFor(name, "D");
        if (deletedLookups == null && latestDeleted == null) return new ArrayList<>();
        List<ListLu> allDeletedLookups = new ArrayList<>();
        if (latestDeleted != null) allDeletedLookups.addAll(latestDeleted);
        if (deletedLookups != null) allDeletedLookups.addAll(deletedLookups);
        return allDeletedLookups;
    }
    
    public List<ListLu> findAllLookupsEverRecordedFor(String name) {
        List<ListLu> lookups = new ArrayList<>();
        lookups.addAll(findActiveLookupsFor(name));
        lookups.addAll(findAllDeletedLookupsFor(name));
        return lookups;
    }
    
    public ListLu findLookup(long id, String name) {
        List<ListLu> lookups = findAllLookupsEverRecordedFor(name);
        for (ListLu lookup : lookups) {
            if (lookup.getId() != null && lookup.getId() == id) {
                return lookup;
            }
        }
        throw new RuntimeException("Lookup of id " + id + " is not found.");
    }
    
    public static class ListLuMapper implements ResultSetMapper<ListLu> {
        public ListLu map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            String code = r.getString("code");
            if (code == null || code.isEmpty()) {
                String name = r.getString("name");
                if (name != null && name.equals("tools"))
                    code = r.getString("id");
                else
                    code = r.getString("value");
            }
            return new ListLu(r.getLong("id"), r.getString("name"), r.getString("value"), code, r.getString("deleted"));
        }
    }
    
    public static class CarrierAlgorithmMapper implements ResultSetMapper<CarrierAlgorithm> {
        public CarrierAlgorithm map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new CarrierAlgorithm(r.getLong("linkId"), r.getString("name"), r.getLong("carrierId"), r.getLong("algorithmId"));
        }
    }
    
   
    /**
     * Adding an new lookup entry, if the lookup code already exist previously, but
     * with lookup value modified, then an new lookup code should be generated in
     * the format of code + "_" + list.size().  This caters for cases where an 
     * currently active lookup entry has its code and value matching the input code 
     * and value, and later on, having its value modified.
     * 
     * In general, if the lookup entry is not yet reference anywhere in the app, it's
     * preferred to have the lookup entry suppressed instead of modify its value.
     * 
     * @return the id of the entry.
     */
    public Long addLookup(String name, String code, String value) {
        List<ListLu> list = findActiveLookup(name, code);
        if (!list.isEmpty()) {
            code = code + "_" + list.size();
        }
        Long newId = addLookupData(name, code, value);
        return newId;
    }
    
    public long addLookup(ListLu lu) {
        // Validate that name, code combination does not already exist and is active.
        String code = (lu.getCode() == null || lu.getCode().isEmpty())? lu.getValue() : lu.getCode();
        return addLookupData(lu.getName(), code, lu.getValue());
    }
    
    public synchronized void updateLookup(Long id, String value) {
        // check the list lu is in the database with its id, otherwise, throw an exception.
        if (id != null) {
            updLookupData(id, value);
        }
    }
    
    public synchronized void updateLookup(Long id, String value, String code) {
        // check the list lu is in the database with its id, otherwise, throw an exception.
        if (id != null) {
            updLookupData(id, value, code);
        }
    }
    
    public synchronized void deleteLookup(List<Long> id) {
        deleteLookupData(id);
    }
    
    public synchronized void undeleteLookup(List<Long> id) {
        undeleteLookupData(id);
    }
    
    @SqlQuery("select max(id) + 1 from lookups")
    protected abstract Long nextLookupId();
    
    @SqlUpdate("INSERT INTO lookups (name, code, value, deleted) VALUES"
            + "(:name, :code, :value, 'N')")
    @GetGeneratedKeys
    public abstract long addLookupData(@Bind("name") String name,
                                          @Bind("code") String code,
                                          @Bind("value") String value);
    
    @SqlUpdate("UPDATE lookups set value = :value "
            + "where id = :id "
            + "and (deleted = 'N' or deleted is null)")
    public abstract void updLookupData(@Bind("id") long id,
                                          @Bind("value") String value);
    
    @SqlUpdate("UPDATE lookups set value = :value, code = :code  "
            + "where id = :id "
            + "and (deleted = 'N' or deleted is null)")
    public abstract void updLookupData(@Bind("id") long id,
                                       @Bind("value") String value,
                                       @Bind("code") String code);
    
    @SqlBatch("UPDATE lookups SET deleted = 'D' WHERE id = :id")
    protected abstract void deleteLookupData(@Bind("id") List<Long> id);
    
    @SqlBatch("UPDATE lookups SET deleted = 'N' WHERE id = :id")
    public abstract void undeleteLookupData(@Bind("id") List<Long> id);
    
    @SqlUpdate("INSERT INTO lookups (id, name, code, value, deleted) VALUES"
            + "(:id, :name, :code, :value, :deleted)")
    protected abstract void seedLookupTable(@Bind("id") List<Long> id,
                                              @Bind("name") List<String> name,
                                              @Bind("code") List<String> code,
                                              @Bind("value") List<String> value,
                                              @Bind("deleted") List<String> deleted);
    
    
    @RegisterMapper(Lookups.CarrierAlgorithmMapper.class)
    @SqlQuery("select distinct ca.linkId, ca.name, ca.carrierId, ca.algorithmId from carrier_algorithm ca, lookups l  where ca.name = :name and ca.algorithmId = l.id  order by l.code asc")
    public abstract List<CarrierAlgorithm> findCarrierAlgorithmsByName(@Bind("name") String name);
    
    @RegisterMapper(Lookups.CarrierAlgorithmMapper.class)
    @SqlQuery("select distinct linkId, name, carrierId, algorithmId  from carrier_algorithm where name = :name and carrierId = :carrierId")
    public abstract List<CarrierAlgorithm> findCarrierAlgorithmByNameAndId(@Bind("name") String name, @Bind("carrierId")Long carrierId );
    
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select al.id, al.code, al.value, al.name, al.deleted from carrier_algorithm ca, lookups al, lookups cl  where cl.code = :name and ca.carrierId = cl.id and (al.deleted = 'N' or al.deleted is null) and al.id=ca.algorithmId")
    public abstract ListLu findDefaultAlgorithmByCarrierName(@Bind("name") String name);
    
    @SqlUpdate("INSERT INTO carrier_algorithm (name, carrierId, algorithmId) VALUES"
            + "(:name, :carrierId, :algorithmId)")
    @GetGeneratedKeys
    public abstract long addCarrierAlgorithmData(@Bind("name") String name,
                                          @Bind("carrierId") long carrierId,
                                          @Bind("algorithmId") long algorithmId);
    
    @SqlUpdate("UPDATE carrier_algorithm set algorithmId = :algorithmId "
            + "where name = :name "
            + "and  carrierId = :carrierId")
    public abstract void updCarrierAlgorithm(@Bind("algorithmId") long algorithmId,
                                       @Bind("name") String name,
                                       @Bind("carrierId") long carrierId);
    
    @SqlBatch("DELETE from carrier_algorithm where carrierId = :carrierId and name = :name")
    protected abstract void deleteCarrierAlgorithm(@Bind("carrierId") Long carrierId,
                                             @Bind("name") String name);
    
    public synchronized void seedInitialLookups() throws IOException {
        List<Path> lookupPaths = getLookupFilePaths("amberdb.lookups.", ".file");
        for (Path lookupPath : lookupPaths) {
            Map<String,List<String>> lookupData = parseLookupData(lookupPath);
            if (!lookupData.isEmpty()) {
                List<Long> ids = parseLong(lookupData.get("id"));
                List<String> names = padStringArry(lookupData.get("name"), ids.size());
                List<String> codes = padStringArry(lookupData.get("code"), ids.size());
                List<String> values = padStringArry(lookupData.get("value"), ids.size());
                List<String> deleted = padStringArry(lookupData.get("deleted"), ids.size());
                seedLookupTable(ids, names, codes, values, deleted);
            }
        }
    }
    
    public synchronized void migrate() {
    	
    	ListLu commercialStatusLu = findLookup("commercialStatus", "Noncommerc");
    	if (commercialStatusLu != null) {
    		updateLookup(commercialStatusLu.id, commercialStatusLu.getValue(), "Noncommerc");
    	}
    	
        ListLu mapSubUnitTypeSheet = findLookup("subUnitType", "Sheet");
        if (mapSubUnitTypeSheet == null) {
            addLookupData("subUnitType", "Sheet", "Sheet");
        }
        
        final String article = "Article";
        ListLu articleSubUnitTypeSheet = findLookup("subUnitType", article);
        if (articleSubUnitTypeSheet == null) {
            addLookupData("subUnitType", article, article);
        }
        
        List<ListLu> entries = findActiveLookupsFor("copyStatus");
        if (entries.isEmpty()) {
            addLookupData("copyStatus", "None", "None");
            addLookupData("copyStatus", "Draft", "Draft");
            addLookupData("copyStatus", "Corrected", "Corrected");
            addLookupData("copyStatus", "Complete", "Complete");           

        }
        
        ListLu entry = findLookup("surface", "Pthalocyanine Al T-Acetate");
        if (entry != null) {
            updLookupData(entry.id, "Pthalocyanine Al", "Pthalocyanine Al");
            addLookupData("surface", "T-Acetate", "T-Acetate");
        }

        // Add bitRate lookup
        String brName = "bitRate";
        List<ListLu> bitRateEntries = findActiveLookupsFor(brName);
        if (bitRateEntries.isEmpty()) {
            addLookupData(brName, "16", "16");
            addLookupData(brName, "24", "24");
            addLookupData(brName, "48", "48");
            addLookupData(brName, "128", "128");
            addLookupData(brName, "256", "256");
        }
        
        List<ListLu> algorithms = findActiveLookupsFor("algorithm");
        if (algorithms.isEmpty()) {
            addLookupData("algorithm", "ATRAC", "ATRAC");
            addLookupData("algorithm", "ANALOGUE", "ANALOGUE");
            addLookupData("algorithm", "PCM", "PCM");
            addLookupData("algorithm", "MP3-MPEG1Layer3", "MP3-MPEG1Layer3");
            addLookupData("algorithm", "PASC", "PASC");
        }
        
    }
    
    protected List<String> padStringArry(List<String> strArry, int length) {
        if (strArry != null && !strArry.isEmpty()) return strArry;
        return new ArrayList<>(length);
    }
    
    protected List<Long> parseLong(List<String> values) {
        List<Long> longValues = new ArrayList<>();
        if (values == null || values.isEmpty()) return longValues;
        for (String value : values) {
            if (value == null || value.isEmpty()) longValues.add(null);
            else longValues.add(Long.parseLong(value));
        }
        return longValues;
    }
    
    public List<Path> getLookupFilePaths(String prefix, String suffix) {
        List<Path> lookupPaths = new ArrayList<>();
        Properties props = System.getProperties();
        for (Object key: props.keySet()) {
            if (key.toString().startsWith(prefix) && key.toString().endsWith(suffix)) {
                Path lookupFile = Paths.get(props.getProperty((String) key).toString());
                if (lookupFile.toFile().exists())
                    lookupPaths.add(lookupFile);
            }
                
        }
        return lookupPaths;
    }
    
    protected Map<String, List<String>> parseLookupData(Path dataFile) throws IOException {
        Map<String, List<String>> lookupData = new ConcurrentHashMap<>();

        // List<String> lines = Files.readAllLines(dataFile, Charset.forName("UTF-8"));
        List<String> lines = Files.readAllLines(dataFile, Charset.forName("utf8"));
        if (lines == null || lines.isEmpty()) return lookupData;
        String[] fldNames = lines.get(0).split(",");
        int fldsTotal = fldNames.length;
        if (fldsTotal == 0) return lookupData;
        
        String fldsPattern = "\\s*(\".*\")";
        if (fldsTotal > 1) {
            fldsPattern += ",\\s*(\".*\")";
        }
        
        Pattern pattern = Pattern.compile(fldsPattern);
        boolean firstLine = true;
        for (String line : lines) {
            if (!firstLine) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    for (int i = 1; i <= fldsTotal; i++) {
                        String fld = matcher.group(i).replace("\"", "");
                        if (lookupData.get(fldNames[i]) == null) {
                            lookupData.put(fldNames[i], new ArrayList<String>());
                        }
                        List<String> flds = lookupData.get(fldNames[i]);
                        flds.add(fld);
                    }
                }
            }
            firstLine = false;
        }
        return lookupData;
    }
    
    protected void printLookupData(OutputStream out, List<List<String>> lookupData) throws IOException {
        String lookupDataJson = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(lookupData);
        out.write(lookupDataJson.getBytes(Charset.defaultCharset()));
        out.write("\n".getBytes(Charset.defaultCharset()));
        out.flush();
    }
}
