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

import javax.persistence.Column;
import javax.persistence.Entity;

import synthesis.db.DbObject;
import synthesis.util.Constants;

/**
 * An object of this class represents an preset, which contains all settings
 * for the synthesizer and can be persisted to the database.
 * @author Matthias Birschl
 *
 */
@Entity
public class Preset extends DbObject implements Comparable<Preset> {



	private String name;

	@Column(length = 2048)
	private final float[][] freqLevels = new float[Constants.NUM_OSCILLATORS][Constants.NUM_ADDITIVE_FREQS];

	private final float[] transposeFactors = new float[Constants.NUM_OSCILLATORS];
	private final float[] levels = new float[Constants.NUM_OSCILLATORS];

	private final int[] attackValues = new int[Constants.NUM_OSCILLATORS];
	private final int[] decayValues = new int[Constants.NUM_OSCILLATORS];
	private final float[] sustainValues = new float[Constants.NUM_OSCILLATORS];
	private final int[] releaseValues = new int[Constants.NUM_OSCILLATORS];

	/**
	 * Fetches all the values from the given {@link Synthesizer}
	 */
	public void fetchValues(final Synthesizer synth){
		for(int i = 0; i < Constants.NUM_OSCILLATORS; i++){
			final Oscillator osc = synth.getOscillator(i);

			freqLevels[i] = osc.getAdditiveFreqLevels();
			transposeFactors[i] = osc.getTransposeFactor();
			levels[i] = osc.getVolume();

			final Envelope env = osc.getEnvelope();
			attackValues[i] = env.getAttack();
			decayValues[i] = env.getDecay();
			sustainValues[i] = env.getSustain();
			releaseValues[i] = env.getRelease();
		}

	}

	/**
	 * Writes all values to the the given {@link Synthesizer}.
	 * After this, the observers of the synthesizer and the observers
	 * of objects that are referenced  by the synthesizer gets notified
	 * So this method loads a preset into the model and notifies the UI
	 */
	public void writeValues(final Synthesizer synth){

		for(int i = 0; i < Constants.NUM_OSCILLATORS; i++){
			final Oscillator osc = synth.getOscillator(i);

			osc.setAdditiveFreqLevels(freqLevels[i]);
			osc.setTransposeFactor(transposeFactors[i]);
			osc.setVolume(levels[i]);

			final Envelope env = osc.getEnvelope();
			env.setADSR(attackValues[i], decayValues[i], sustainValues[i], releaseValues[i]);

			osc.setChanged();
			env.setChanged();
		}
	}

	/**
	 * Returns the name of this preset
	 */
	public String getName(){
		return name;
	}

	/**
	 * Sets the name of this preset
	 */
	public void setName(final String name){
		this.name = name;
	}

	@Override
	public String toString(){
		return name;
	}

	@Override
	public int compareTo(final Preset p){

		return this.name.compareToIgnoreCase(p.name);
	}
}
