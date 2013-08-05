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

import java.util.ArrayList;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import synthesis.util.LoggerFactory;

/**
 * 
 * This class is an implementation of the interface {@link GenericDAO}  for the persistence framework 
 * Eclipselink.  {@link GenericDAO} is the interface, which higher layers can use to access the
 * persistence layer.
 * The objects of this class must be created with the class  {@link DAOfactory}.
 * This class contains no application specific code but is bound to the 
 * persistence framework Eclipselink. 
 * 
 * @author Matthias Birschl
 * 
 */
class EclipseLinkDAO implements GenericDAO {

	private static final String PERSISTENCE_UNIT_NAME = "syn_thesis";
	private static EntityManagerFactory factory;

	private EntityManager em;

	private static Logger log = LoggerFactory.getLogger(EclipseLinkDAO.class);

	public EclipseLinkDAO(){

		try{
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		}catch(final Exception e){
			log.error("Error while configuration of Eclipselink", e);
		}

	}

	@Override
	public boolean isPersistent(final DbObject obj){

		final Object o = em.find(obj.getClass(), obj.getId());

		return o != null;
	}

	@Override
	public void insert(final DbObject obj){
		em.persist(obj);
	}

	@Override
	public boolean save(final DbObject obj){
		final boolean isPersistent = !isPersistent(obj);
		em.merge(obj);
		return isPersistent;
	}

	@Override
	public boolean delete(final DbObject obj){
		final boolean isPersistent = isPersistent(obj);
		final DbObject o = em.find(obj.getClass(), obj.getId());
		em.remove(o);
		return isPersistent;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DbObject> List<T> findAll(final Class<?> clazz) throws PersistenceException{

		 if(!DbObject.class.isAssignableFrom(clazz)){
             throw new PersistenceException("Objects of the given class cannot be read from db ");
         }
		
		List<T> results = new ArrayList<T>();
		final Query q = em.createQuery("select o from " + clazz.getSimpleName() + " o");
		results = q.getResultList();

		return results;
	}

	@Override
	public void beginTransaction(){
		em = factory.createEntityManager();
		em.getTransaction().begin();
	}

	@Override
	public void endTransaction(final boolean ok){

		final EntityTransaction transaction = em.getTransaction();

		if(ok){
			transaction.commit();
		}
		else{
			transaction.rollback();
		}
		em.close();

	}



}
