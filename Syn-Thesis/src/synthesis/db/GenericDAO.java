/* Copyright (C) 2011 by Matthias Birschl (m-birschl@gmx.de)
 * 
 * This file is part of SynThesis.
 * SynThesis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package synthesis.db;

import java.util.List;

import javax.persistence.PersistenceException;

/**
 * This interface makes the persistence layer accessible for higher layers.
 * The code in this interface is independent from other layers and also
 * independent from the persistence mechanism. 
 * 
 * @author Matthias Birschl
 *
 */
public interface GenericDAO {

	/**
	 * Checks if the given {@link DbObject} is persistent
	 * 
	 * @return true True if the given {@link DbObject} is persistent otherwise false
	 */
	boolean isPersistent(final DbObject obj);

	/**
	 * Adds the given transient object to the database an makes it persistent.
	 * The given object gets an id.
	 * 
	 */
	void insert(final DbObject obj);

	/**
	 * Saves the current state of the given object to the database.
	 * If the object was transient before, it gets an id.
	 * 
	 * @return true If the object was not persistent before
	 */
	boolean save(final DbObject obj);

	/**
	 * Deletes the given object from the database
	 * 
	 * @return true If the given object was persistent otherwise false
	 */
	boolean delete(final DbObject obj);



	/**
	 * Provides a list with all existing objects of the given class from the database 
	 * 
	 * @return All objects of the given class from the database
	 */
	public <T extends DbObject> List<T> findAll(Class<?> persistentClass)throws PersistenceException;

	/**
	 * Begins a new transaction. All changes 
	 * See also: {@link #endTransaction(boolean)}
	 */
	public void beginTransaction();

	/**
	 * Finish an Transaction, that was opened before by {@link #beginTransaction()}.
	 * 
	 * @param ok True if the transaction was successfull. In this case all changes
	 * since the last call of {@link #beginTransaction()} gets applied to the database.
	 * If the given parameter is false, all changes gets rejected.
	 */
	public void endTransaction(final boolean ok);

	

}
