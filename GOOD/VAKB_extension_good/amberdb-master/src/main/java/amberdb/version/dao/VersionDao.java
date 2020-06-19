package amberdb.version.dao;


import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;


public interface VersionDao extends Transactional<VersionDao> {

    
    @SqlQuery(
            "SELECT id "
            + "FROM transaction " 
            + "WHERE time > :time "
            + "ORDER BY time, id")
    List<Long> getTransactionsSince(
            @Bind("time") Long time);

    @SqlQuery("SELECT id "
             + "FROM transaction "
             + "WHERE time > :startTime and time < :endTime "
             + "ORDER BY time, id ")
    List<Long> getTransactionsBetween(@Bind("startTime") Long startTime, @Bind("endTime") Long endTime);
    
    
    void close();
}

