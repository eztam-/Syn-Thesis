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
package synthesis.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import synthesis.db.DAOfactory;
import synthesis.db.GenericDAO;
import synthesis.logic.Preset;

public class DB {

	private final Preset preset = new Preset();
	private final GenericDAO dao = DAOfactory.createDAO();

	@Test
	public void isPersistent(){

		// preset.setTransposeFactor(0.333f);
		// preset.setVolume(0.222f);

		insertPreset();

		dao.beginTransaction();
		assertEquals(true, dao.isPersistent(preset));
		// assertEquals(true, dao.isPersistent(preset.getEnvelope()));
		dao.endTransaction(true);

		deletePreset();

		dao.beginTransaction();
		assertEquals(false, dao.isPersistent(preset));
		// assertEquals(false, dao.isPersistent(preset.getEnvelope()));
		dao.endTransaction(true);

	}

	@Test
	public void findAll(){

		dao.beginTransaction();
		List<Preset> presets = dao.findAll(preset.getClass());
		dao.endTransaction(true);

		boolean found = false;
		for(final Preset p: presets){
			if(p.getId() == preset.getId()){
				found = true;
			}
		}

		assertFalse(found);

		insertPreset();

		dao.beginTransaction();
		presets = dao.findAll(preset.getClass());
		dao.endTransaction(true);

		found = false;
		for(final Preset p: presets){
			if(p.getId() == preset.getId()){
				found = true;
			}
		}

		assertTrue(found);

		deletePreset();
	}


	// @Test
	// public void transactions(){
	//
	// insertPreset();
	// dao.beginTransaction();
	// dao.delete(preset);
	// dao.endTransaction(false);
	//
	// dao.beginTransaction();
	// assertTrue(dao.isPersistent(preset));
	// dao.endTransaction(true);
	//
	// deletePreset();
	//
	// }

	@Test
	public void save(){

		insertPreset();

		dao.beginTransaction();
		assertFalse(dao.save(preset));
		dao.endTransaction(true);

		dao.beginTransaction();
		final List<Preset> presets = dao.findAll(preset.getClass());
		dao.endTransaction(true);

		for(final Preset p: presets){
			if(p.getId() == preset.getId()){
				assertTrue(true);
			}
		}

		deletePreset();

	}

	private void insertPreset(){
		dao.beginTransaction();
		dao.insert(preset);
		dao.endTransaction(true);
	}

	private void deletePreset(){
		dao.beginTransaction();
		dao.delete(preset);
		dao.endTransaction(true);
	}
}
