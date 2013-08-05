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
package synthesis.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import org.apache.log4j.Logger;

import synthesis.db.DAOfactory;
import synthesis.db.GenericDAO;
import synthesis.util.Constants;
import synthesis.util.LoggerFactory;

/**
 * This class represents an synthesizer with his oscillators, envelopes and other 
 * components. Usually only one instance of this class should exist per 
 * plugin instance
 * @author Matthias Birschl
 */
public class Synthesizer extends Observable {

	private final Oscillator[] oscillators = new Oscillator[4];

	private static Logger log = LoggerFactory.getLogger(Synthesizer.class);

	private static List<Preset> presets = new ArrayList<Preset>();

	private float pitch = 1;

	/**
	 * Parameter for the observers. This parameter means, that
	 * the synthesizer changed the first time after the plugin
	 * was started and the presets was read from the db.
	 */
	public final static String INIT_LOADING_FINISHED = "INIT_LOADING_FINISHED";
	
	/**
	 * Is true after the {@link PresetLoader} started the first time.
	 * Other plugin instances use this to detect, that the presets are already
	 * loaded from the db
	 */
	private static boolean presetLoaderStarted = false; 
	
	/**
	 * Other plugin instances waiting for the presets until this gets true
	 */
	private static boolean presetLoaderFinished = false;

	private final LinkedHashMap<Integer, Note> notes = new LinkedHashMap<Integer, Note>(16, 1);


	public Synthesizer(){

		for(int i = 0; i < Constants.NUM_OSCILLATORS; i++){
			
			final Oscillator osc = new Oscillator();
			// some initial values
			osc.getEnvelope().setADSR(1, 51, 0.8f, 500);	

			oscillators[i] =  osc;
		}

		final PresetLoader loader = new PresetLoader();
		loader.start();

	}

	/**
	 * Turns a {@link Note} of the given MIDI-number on (starts playing the note) 
	 * with the given velocity
	 * @param midiNote The MIDI-number of the note
	 * @param velocity A value between 0 and 1
	 * @see #noteOff(int midiNote)
	 */
	public void noteOn(final int midiNote, final float velocity){

		// if the same note is currently playing
		if(notes.get(midiNote) != null){ 									
			notes.get(midiNote).reset(velocity);
		}
		else{
			final Note note = new Note(midiNote, velocity, this);
			notes.put(midiNote, note);
		}

	}

	/**
	 * Turns a {@link Note} off (stops playing the note) which
	 * was turned on before.  
	 * @param midiNote The MIDI-number of the note
	 * @see #noteOn(int midiNote, float velocity)
	 */
	public void noteOff(final int midiNote){

		if(notes.containsKey(midiNote)){
			notes.get(midiNote).setReleasing();
		}

	}

	/**
	 * Returns the next sample of the waveform, that this synthesizer
	 * is playing
	 * @return A value between -1 and 1
	 */
	public float getNextSample(){
		float sample = 0;

		final Iterator<Note> itr = notes.values().iterator();
		while(itr.hasNext()){
			final Note note = itr.next();
			if(note.isFinihed){
				itr.remove();
			}else{
				sample += note.getNextSample();
			}

		}

		return sample;
	}

	/**
	 * Returns the {@link Oscillator} on the given index
	 * @param index The index of the oscillator. as an 
	 * value between 0 and {@link Constants#NUM_OSCILLATORS}
	 */
	public Oscillator getOscillator(final int index){
		return oscillators[index];
	}

	/**
	 * Sets the global pitch of the synthesizer.
	 * 
	 * @return A factor>0. For example: 
	 * 0.5	= -1 octave 
	 * 1	= nothing (default pitch) 
	 * 2	= +1 octave 4 = +2 octaves
	 */
	public float getPitch(){
		return pitch;
	}

	/**
	 * Sets the global pitch of the synthesizer.
	 * @param pitch A factor>0. For example:
	 * 0.5  = -1 octave
	 * 1	= nothing (default pitch)
	 * 2	= +1 octave
	 * 4	= +2 octaves  
	 */
	public void setPitch(final float pitch){
		this.pitch = pitch;
	}

	/**
	 * Returns all available presets
	 */
	public List<Preset> getPresets(){
		Collections.sort(presets);
		return presets;
	}

	
	/**
	 * Writes the current state of the model to the given preset
	 * and save the preset in the database.
	 */
	public void savePreset(final Preset preset){

		preset.fetchValues(this);
		final GenericDAO dao = DAOfactory.createDAO();
		boolean ok = false;
		try{
			dao.beginTransaction();
			dao.save(preset);
			ok = true;
		}catch(final Exception e){
			ok = false;
			log.error("Transaction error", e);
		}finally{
			dao.endTransaction(ok);
		}
	}

	/**
	 * Sets the active preset. Overrides the current settings of the model
	 * with the values of the given preset. After this, the observers of the model gets 
	 * notified.
	 */
	public void setActivePreset(final Preset preset){

		preset.writeValues(this);

	}

	/**
	 * Saves the current state of the model to an new preset with
	 * the given name to the database.
	 * @param name The name of the new preset
	 */
	public Preset savePresetAs(final String name){

		final Preset newPreset = new Preset();
		newPreset.setName(name);
		newPreset.fetchValues(this);

		setActivePreset(newPreset);

		presets.add(newPreset);
		super.setChanged();
		notifyObservers(newPreset);

		final GenericDAO dao = DAOfactory.createDAO();
		boolean ok = false;
		try{
			dao.beginTransaction();
			dao.insert(newPreset);
			ok = true;
		}catch(final Exception e){
			ok = false;
			log.error("Transaction error", e);
		}finally{
			dao.endTransaction(ok);
		}

		return newPreset;
	}

	/**
	 * Deletes the given preset from the model and from the database
	 */
	public void deletePreset(final Preset preset){

		if(preset == null){
			return;
		}
		presets.remove(preset);
		setChanged();

		boolean ok = false;
		final GenericDAO dao = DAOfactory.createDAO();
		try{
			dao.beginTransaction();
			dao.delete(preset);
			ok = true;
		}catch(final Exception e){
			ok = false;
			log.error("Transaction error", e);
		}finally{
			dao.endTransaction(ok);
		}

	}
	
	/**
	 * Notifies all observers, that this oscillator has changed
	 */
	@Override
	public void setChanged(){
		super.setChanged();
		notifyObservers();

	}

	/**
	 * On startup of the first plugin instance in a host, it takes some time
	 * until the database connection is established. So that the plugin don't
	 * have to wait until the user can use it, the database loading is processed
	 * in a extra thread.
	 * 
	 * This class also ensures, that only the first loaded plugin instance load
	 * the presets from the db and shares them to other plugin instances
	 * 
	 * @author Matthias Birschl
	 * 
	 */
	private class PresetLoader extends Thread {

		@Override
		public void run(){

			log.debug("presetloader started");

			if(!presetLoaderStarted){ // Presets may loaded just once for all
										// plugin instances
				presetLoaderStarted = true;
				fetchPresetsFromDB();
			}else{
				waitForPresets();
			}

			log.debug("presetloader finish");

		}

		/**
		 * Fetches all presets from the database and notify the observers of the
		 * top level class {@link Synthesizer}. The loaded presets are provided to all
		 * plugin instances over an static field in the top level class
		 */
		private void fetchPresetsFromDB(){
			boolean ok = false;

			final GenericDAO dao = DAOfactory.createDAO();

			try{
				dao.beginTransaction();
				presets = dao.findAll(Preset.class);
				ok = true;
			}catch(final Exception e){
				ok = false;
				log.error("Transaction error", e);
			}finally{
				dao.endTransaction(ok);
			}
			setChanged();
		
			presetLoaderFinished = true;
		}

		/**
		 * If more than one instances of this VST plugin are loaded into the
		 * host at the same time, then only one plugin instance fetches the data
		 * from the database and shares it via static fields to the other
		 * instances. This method waits, until the preset are loaded from the
		 * database. It checks every second, if the presets are now available.
		 * When the presets are available, the obervers of the top level class
		 * {@link Synthesizer} gets notified.
		 */
		private void waitForPresets(){
			while(!presetLoaderFinished){
				try{
					Thread.sleep(1000);
				}catch(final InterruptedException e){
					log.error("Error while waiting for preset loading", e);
				}
			}
			setChanged();

		}

	}

}
