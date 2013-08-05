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

import java.util.Observable;

import synthesis.util.Constants;
import synthesis.util.MIDI;

/**
 * Represents an oscillator, whose waveform can be modified by adding
 * or subtracting different frequencies with different amounts. 
 * This class also contains the waveforms of all required frequencies in a 
 * static field, so all oscillators and all plugin instances can use the 
 * same frequency-waveforms.
 *  
 * @author Matthias Birschl
 *
 */
public class Oscillator extends Observable {

	
	/**
	 * The waveform of this oscillator
	 */
	private final WaveForm waveForm = new WaveForm();

	/**
	 * Contains all the frequencies that can be added to an oscillator.
	 * This frequencies exists just once for all oscillators and plugin
	 * instances 
	 */
	private static Frequency[] frequencies = new Frequency[Constants.NUM_ADDITIVE_FREQS];

	private float[] additiveLevels = new float[Constants.NUM_ADDITIVE_FREQS];

//	private static Logger log = LoggerFactory.getLogger(Oscillator.class);

	private final Envelope envelope = new Envelope();

	private boolean hasWaveForm = false;

	private float volume = 1;

	private float transposeFactor = 1;


	/**
	 * Create the frequencies. The frequencies exists just once for all 
	 * oscillators and also for all plugin instances !
	 */
	static{
		for(int i = 0; i < frequencies.length; i++){
			final Frequency freq = new Frequency(MIDI.getFreq(0) * (i + 1));
			frequencies[i] = freq;
		}
	}

	public Oscillator(){

	}

	
	/**
	 * Adds and subtracts the overtones with the given levels to this oscillator.
	 * 
	 * @param additiveLevels An array with the length {@link Constants#NUM_ADDITIVE_FREQS}.
	 * Each element contains the level of one overtone as a value between -1 and 1.
	 * A negative value means, that the overtone gets subtracted to this oscillator.
	 * A zero value means, that this overtone is not used by this oscillator.
	 * A positive value means, that the overtone gets added to this oscillator.
	 */
	public void setAdditiveFreqLevels(final float[] additiveLevels){

		hasWaveForm = false;
		this.additiveLevels = additiveLevels;
		waveForm.clear();
		for(int i = 0; i < additiveLevels.length; i++){
			if(additiveLevels[i] != 0){
				waveForm.addFrequency(frequencies[i], additiveLevels[i]);
				hasWaveForm = true;
			}
			
		}
		waveForm.normalize();
		waveForm.setChanged();

	}

	
	/**
	 * The transpose factor determines the pitch level of this oscillator 
	 * 
	 * @param factor The transpose factor. For example: 
	 * 0.25 = -2 octaves 
	 * 0.5 	= -1 octave
	 * 1	=  0 octaves
	 * 2	= +1 octave
	 * 4	= +2 octaves
	 */
	public void setTransposeFactor(final float factor){
		this.transposeFactor = factor;

	}
	
	
	/**
	 * The transpose factor determines the pitch level of this oscillator 
	 * 
	 * @param factor The transpose factor. For example: 
	 * 0.25 = -2 octaves 
	 * 0.5 	= -1 octave
	 * 1	=  0 octaves
	 * 2	= +1 octave
	 * 4	= +2 octaves
	 */
	public float getTransposeFactor(){

		return transposeFactor;
	}

	/**
	 * Returns the {@link WaveForm} of this oscillator.
	 */
	public WaveForm getWaveForm(){
		return waveForm;
	}

	/**
	 * Returns the {@link Envelope} of this oscillator.
	 */
	public Envelope getEnvelope(){
		return envelope;
	}

	
//	public void setEnvelope(Envelope envelope){
//		this.envelope = envelope;
//	}

	/**
	 * Returns the volume of this oscillator.
	 * @return A value between 0 and 1
	 */
	public float getVolume(){
		return volume;
	}

	/**
	 * Sets the volume of this oscillator.
	 * @param volume A value between 0 and 1
	 */
	public void setVolume(final float volume){
		this.volume = volume;
	}

	/**
	 * True wenn dieser oszillator eine Wellenform enthaelt, bzw, nicht alle
	 * additiven freq-levels 0 sind
	 * 
	 * @return
	 */
	boolean hasWaveForm(){

		return hasWaveForm;
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
	 * Returns the levels of the overtones, of this oscillator
	 * @return An array with the length {@link Constants#NUM_ADDITIVE_FREQS}.
	 * Each element contains the level of one overtone as a value between -1 and 1.
	 * A negative value means, that the overtone is subtracted to this oscillator.
	 * A zero value means, that this overtone is not used bay this oscillator.
	 * A positive value means, that the overtone is added to this oscillator.
	 */
	public float[] getAdditiveFreqLevels(){
		return additiveLevels;
	}

	
//	/**
//	 * Creates a deep copy of this oscillator
//	 * @return a new {@link Oscillator} instance.
//	 */
//	public Oscillator copy(){
//
//		Oscillator newOsc = new Oscillator();
//		newOsc.setAdditiveFreqLevels(additiveLevels);
//		newOsc.setVolume(volume);
//		newOsc.setTransposeFactor(transposeFactor);
//		newOsc.setEnvelope(envelope.copy());
//
//		return newOsc;
//	}

}
