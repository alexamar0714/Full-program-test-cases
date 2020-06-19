package amberdb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.AmberSession;
import amberdb.sql.ListLu;
import amberdb.sql.Lookups;
import amberdb.sql.LookupsSchema;

public class LookupRefresh {
    static final Logger log = LoggerFactory.getLogger(LookupRefresh.class);
    public static List<ListLu> synchronizeLookups(AmberSession db) {
        Lookups destLookups = db.getLookups();
        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:cache;DATABASE_TO_UPPER=false", "store", "collection");
        DBI lookupsDbi = new DBI(ds);
        LookupsSchema luSchema = lookupsDbi.onDemand(LookupsSchema.class);
        if (!luSchema.schemaTablesExist()) {
            luSchema.createLookupsSchema();
        }
        
        if (!luSchema.carrierAlgorithmTableExist()) {
            luSchema.createCarrierAlgorithmTable();
        }
        
        List<ListLu> fromLu = lookupsDbi.onDemand(Lookups.class).findActiveLookups();
        Map<String, ListLu> fromMap = indexLookups(fromLu);
        Map<String, ListLu> toMap = indexLookups(destLookups.findActiveLookups());
        for (String nameCode : fromMap.keySet()) {
            if (toMap.get(nameCode) == null) {               
                String name = nameCode.substring(0, nameCode.indexOf('_') - 1);
                String code = nameCode.substring(nameCode.indexOf('_') + 1);
                String value = fromMap.get(nameCode).getValue();
                log.debug("adding lookups for name: " + name + ", code: " + code + ", value: " + value);
                
                // add new lookup entry
                destLookups.addLookup(fromMap.get(nameCode));
            } else {
                // update an existing lookup entry
                Long id = toMap.get(nameCode).getId();
                String value = fromMap.get(nameCode).getValue();
                log.debug("adding lookups for name_code: " + nameCode + ", value: " + value);
                destLookups.updateLookup(id, fromMap.get(nameCode).getValue());
            }
        }
        destLookups.commit();
        return fromLu;
    }

    protected static Map<String, ListLu> indexLookups(List<ListLu> lookups) {
        Map<String, ListLu> index = new HashMap<>();
        if (lookups == null) return index;
        for (ListLu entry : lookups) {
            index.put(entry.getName() + "_" + entry.getCode(), entry);
        }
        return index;
    }
}
