/* CarInsuranceDAO.java
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

package be.ugent.degage.db.dao;

import java.util.Date;
import java.util.List;

import be.ugent.degage.db.DataAccessException;
import be.ugent.degage.db.models.Car;
import be.ugent.degage.db.models.CarInsurance;

public interface CarInsuranceDAO {
	public CarInsurance createCarInsurance(Date expiration, int bonus_malus, int polisNr, Car car) throws DataAccessException;
	public void updateCarInsurance(CarInsurance insurance) throws DataAccessException;
	public void deleteCarInsurance(CarInsurance insurance) throws DataAccessException;
	public List<CarInsurance> getAllCarInsurances(Car car) throws DataAccessException;
	public void deleteAllCarInsurances(Car car) throws DataAccessException;
}
