/* JDBCAddressDAO.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright Ⓒ 2014-2015 Universiteit Gent
 * 
 * This file is part of the Degage Web Application
 * 
 * Corresponding author (see also AUTHORS.txt)
 * 
 * Kris Coolsaet
 * Department of Applied Mathematics, Computer Science and Statistics
 * Ghent University 
 * Krijgslaan 281-S9
 * B-9000 GENT Belgium
 * 
 * The Degage Web Application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Degage Web Application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with the Degage Web Application (file LICENSE.txt in the
 * distribution).  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ugent.degage.db.jdbc;

import be.ugent.degage.db.DataAccessException;
import be.ugent.degage.db.dao.AddressDAO;
import be.ugent.degage.db.models.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBC implementation of {@link AddressDAO}
 */
class JDBCAddressDAO extends AbstractDAO implements AddressDAO {

    public JDBCAddressDAO(JDBCDataAccessContext context){
        super (context);
    }

    // TODO: avoid these
    static Address populateAddress(ResultSet rs) throws SQLException {
        if(rs.getObject("address_id") == null)
            return null;
        else
            return new Address(
                    rs.getInt("address_id"),
                    rs.getString("address_country"),
                    rs.getString("address_zipcode"),
                    rs.getString("address_city"),
                    rs.getString("address_street"),
                    rs.getString("address_number"),
                    rs.getFloat("address_latitude"),
                    rs.getFloat("address_longitude")
            );
    }

    // TODO: avoid these
    static Address populateAddress(ResultSet rs, String tableName) throws SQLException {
        if(rs.getObject(tableName + ".address_id") == null)
            return null;
        else
            return new Address(
                    rs.getInt(tableName + ".address_id"),
                    rs.getString(tableName + ".address_country"),
                    rs.getString(tableName + ".address_zipcode"),
                    rs.getString(tableName + ".address_city"),
                    rs.getString(tableName + ".address_street"),
                    rs.getString(tableName + ".address_number"),
                    rs.getFloat(tableName + ".address_latitude"),
                    rs.getFloat(tableName + ".address_longitude")
            );
    }

    public static final String ADDRESS_FIELDS =
            "address_id, address_city, address_zipcode, address_street, address_number, address_country, address_latitude, address_longitude ";

    private LazyStatement getAddressStatement = new LazyStatement(
            "SELECT " + ADDRESS_FIELDS + "FROM addresses WHERE address_id = ?");

    @Override
    public Address getAddress(int id) throws DataAccessException {
        try {
            PreparedStatement ps = getAddressStatement.value();   // reused so should not be auto-closed
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return populateAddress(rs);
                } else
                    return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Could not fetch address by id.", ex);
        }
    }

    private LazyStatement createAddressStatement = new LazyStatement(
            "INSERT INTO addresses(address_city, address_zipcode, address_street, address_number, address_country, address_latitude, address_longitude) " +
                    "VALUES (?,?,?,?,?)",
            "address_id"
    );

    @Override
    public Address createAddress(String country, String zip, String city, String street, String num, float lat, float lng) throws DataAccessException {
        try {
            PreparedStatement ps = createAddressStatement.value(); // reused so should not be auto-closed
            ps.setString(1, city);
            ps.setString(2, zip);
            ps.setString(3, street);
            ps.setString(4, num);
            ps.setString(5, country);
            ps.setFloat(6, lat);
            ps.setFloat(7, lng);

            if(ps.executeUpdate() == 0)
                throw new DataAccessException("No rows were affected when creating address.");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next(); //if this fails we want an exception anyway

                return new Address(keys.getInt(1), country, zip, city, street, num, lat, lng);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create address.", ex);
        }
    }
	
    private LazyStatement deleteAddressStatement = new LazyStatement(
            "DELETE FROM addresses WHERE address_id = ?"
    );

    @Override
    public void deleteAddress(int addressId) throws DataAccessException {
        try {
            PreparedStatement ps = deleteAddressStatement.value(); // reused so should not be auto-closed
            ps.setInt(1, addressId);
            if(ps.executeUpdate() == 0)
                throw new DataAccessException("No rows were affected when deleting address with ID=" + addressId);

        } catch(SQLException ex){
            throw new DataAccessException("Failed to execute address deletion query.", ex);
        }
    }

    // used to update addresses as part of updates of other tables

    static void updateLocation(Connection conn, String joinSQL, String idName, int id, Address location) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE addresses " + joinSQL +
                " SET address_city = ?, address_zipcode = ?, address_street = ?, address_number = ?, address_country=?, address_latitude=?, address_longitude=? " +
                "WHERE " + idName + " = ?"
        )) {
            ps.setString(1, location.getCity());
            ps.setString(2, location.getZip());
            ps.setString(3, location.getStreet());
            ps.setString(4, location.getNum());
            ps.setString(5, location.getCountry());
            ps.setFloat(6, location.getLat());
            ps.setFloat(7, location.getLng());

            ps.setInt(8, id);

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update location.", ex);
        }
    }


    private LazyStatement updateAddressStatement = new LazyStatement(
            "UPDATE addresses SET address_city = ?, address_zipcode = ?, address_street = ?, " +
                    "address_number = ?, address_country=?, " + "address_latitude = ?, address_longitude=? " +
                    "WHERE address_id = ?"
    );

    @Override
    public void updateAddress(Address address) throws DataAccessException {
        try {
            PreparedStatement ps = updateAddressStatement.value();   // reused so should not be auto-closed
            ps.setString(1, address.getCity());
            ps.setString(2, address.getZip());
            ps.setString(3, address.getStreet());
            ps.setString(4, address.getNum());
            ps.setString(5, address.getCountry());
            ps.setFloat(6, address.getLat());
            ps.setFloat(7, address.getLng());

            ps.setInt(8, address.getId());
			
            if(ps.executeUpdate() == 0)
                throw new DataAccessException("Address update affected 0 rows.");

        } catch(SQLException ex) {
            throw new DataAccessException("Failed to update address.", ex);
        }

    }
}
