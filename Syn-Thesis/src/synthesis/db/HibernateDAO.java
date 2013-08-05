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

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import synthesis.logic.VST_Adapter;
import synthesis.util.LoggerFactory;

/**
 * 
 * This class is an implementation of the interface {@link GenericDAO}  for the persistence framework 
 * Hibernate.  {@link GenericDAO} is the interface, which higher layers can use to access the
 * persistence layer.
 * The objects of this class must be created with the class  {@link DAOfactory}.
 * This class contains no application specific code but is bound to the 
 * persistence framework Hibernate. 
 * 
 * @author Matthias Birschl
 * 
 */
class HibernateDAO implements GenericDAO {

	private static Logger log = LoggerFactory.getLogger(HibernateDAO.class);

	private static Session session = null;
	private static Configuration cfg = null;
	private static SessionFactory sessionFactory;
	static{

		try{

			final String dbPath = VST_Adapter.getBasePath() + "/h2db/presets";
			log.debug("Loading H2 database from " + dbPath);

			cfg = new Configuration()
					.addAnnotatedClass(synthesis.db.DbObject.class)
					.addAnnotatedClass(synthesis.logic.Preset.class)
					.setProperty("hibernate.connection.driver_class", "org.h2.Driver")
					.setProperty("hibernate.connection.url", "jdbc:h2:file:" + dbPath)
					.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
					.setProperty("hibernate.connection.username", "sa")
					.setProperty("hibernate.connection.password", "")
					.setProperty("hibernate.connection.pool_size", "2")
					.setProperty("hibernate.hbm2ddl.auto", "update"); // create,
																		// update

			// Configuration for MySQL
			// cfg = new Configuration()
			// .addAnnotatedClass(logic.Oscillator.class)
			// .addAnnotatedClass(logic.Envelope.class)
			// .addAnnotatedClass(logic.Preset.class)
			// .setProperty("hibernate.connection.driver_class",
			// "com.mysql.jdbc.Driver")
			// .setProperty("hibernate.connection.url",
			// "jdbc:mysql://localhost/synthesis")
			// .setProperty("hibernate.dialect",
			// "org.hibernate.dialect.MySQLDialect")
			// .setProperty("hibernate.connection.username", "root")
			// .setProperty("hibernate.connection.password", "")
			// .setProperty("hibernate.connection.pool_size", "10")
			// .setProperty("hibernate.hbm2ddl.auto", "update");

			sessionFactory = cfg.buildSessionFactory();

			session = sessionFactory.openSession();
		}catch(final Exception e){
			log.error("Hibernate Configuration Error", e);

		}
	}

	public HibernateDAO(){

	}

	@Override
	public boolean isPersistent(final DbObject dbObject){

		return session.contains(dbObject);
	}

	@Override
	public void insert(final DbObject dbObject){

		try{
			session.save(dbObject);
		}catch(final Exception e){
			log.error("Persistence Save Error", e);
		}
	}

	@Override
	public final boolean save(final DbObject dbObject){

		final boolean isPersistent = session.contains(dbObject);

		session.merge(dbObject);
		return !isPersistent;

	}

	@Override
	public final boolean delete(final DbObject dbObject){

		final boolean isPersistent = session.contains(dbObject);
		session.delete(dbObject);
		return isPersistent;

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DbObject> List<T> findAll(final Class<?> clazz) throws PersistenceException{

		if(!DbObject.class.isAssignableFrom(clazz)){
			throw new PersistenceException("Objects of the given class cannot be read from db ");
		}

		List<T> results = new ArrayList<T>();

		final Query q = session.createQuery("select o from "
				+ clazz.getName() + " o ");
		results = q.list();

		return results;
	}

	@Override
	public void beginTransaction(){
		session.beginTransaction();
	}

	@Override
	public void endTransaction(final boolean ok){

		final Transaction transaction = session.getTransaction();

		if(ok){
			transaction.commit();
		}else{
			transaction.rollback();
		}

	}


}
