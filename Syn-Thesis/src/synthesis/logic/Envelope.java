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

/**
 * This class represents an ADSR-envelope
 * 
 * @author Matthias Birschl
 * 
 */
public class Envelope extends Observable {

	private int attack = 10;
	private int decay = 44;
	private float sustain = 0.3f;
	private int release = 200;

	// private Logger log = LoggerFactory.getLogger(Envelope.class);

	/**
	 * The envelope values for the attack and decay phases. The values are
	 * between 0 and 1
	 */
	private float[] envelopeAD;

	/**
	 * The envelope values for the release phase. The values are between 0 and 1
	 */
	private float[] envelopeR;

	private final float sampleRate = VST_Adapter.getCurrentSampleRate();

	/**
	 * Calculates the envelope with the given parameters.
	 * 
	 * @param attack
	 *            Attack time in ms
	 * @param decay
	 *            Decay time in ms
	 * @param sustain
	 *            The sustain level as value between 0 und 1
	 * @param release
	 *            Release time in ms
	 */
	public void setADSR(final int attack, final int decay, final float sustain, final int release){

		setSustain(sustain); // sustain needs to be set before decay and release
		setAD(attack, decay);
		setRelease(release);
	}

	public void setAD(final int attack, final int decay){

		this.attack = attack;
		this.decay = decay;

		int attackInSamples = attack * (int)(sampleRate / 1000);
		int decayInSamples = decay * (int)(sampleRate / 1000);

		if(attackInSamples == 0){
			attackInSamples = 1;
		}
		// prevents klick sounds if the decay time is nearly zero
		if(decayInSamples < 40){
			decayInSamples = 40;
		}

		envelopeAD = new float[attackInSamples + decayInSamples];

		// Calculate the attack phase
		float step = 1f / attackInSamples;
		envelopeAD[0] = step;
		for(int i = 1; i < attackInSamples; i++){
			envelopeAD[i] = envelopeAD[i - 1] + step;
		}

		// calculate the decay phase
		step = (1f - sustain) / decayInSamples;
		for(int i = attackInSamples; i < attackInSamples + decayInSamples; i++){
			envelopeAD[i] = envelopeAD[i - 1] - step;
		}
	}

	/**
	 * Liefert den Wert innerhalb der Attack- und Decay-Phase der Huellkurve an
	 * der uebergebenen position. Wenn die position groesser ist als die laenge
	 * der Attack- und Decayphase, dann wird der susatin Wert zurueckgegeben
	 * 
	 * @param position
	 * @return Huellkurven Wert zwischen 0 und 1
	 */
	public float getValueAD(final int position){
		if(position >= envelopeAD.length){
			return sustain;
		}
		return envelopeAD[position];
	}

	/**
	 * Liefert den Wert innerhalb Release-Phase der Huellkurve an der
	 * uebergebenen position. Ist die uebergebene Position groesser als die
	 * R-Huellkurve dann wird 0 zurueckgegeben
	 * 
	 * @param position
	 * @return Huellkurven Wert zwischen 0 und 1
	 */
	public float getValueR(final int position){
		if(position < envelopeR.length){
			return envelopeR[position];
		}
		return 0;
	}

	public int getSizeAD(){

		return envelopeAD.length;
	}

	public int getSizeR(){

		return envelopeR.length;
	}

	public int getSize(){

		return envelopeAD.length + envelopeR.length;
	}

	public float getSustainLevel(){

		return sustain;
	}

	/**
	 * Recalculates the attack phase of the envelope with the given value
	 * 
	 * @param attack
	 *            The attack time in ms
	 */
	public void setAttack(final int attack){
		setAD(attack, this.decay);
	}

	/**
	 * Recalculates the decay phase of the envelope with the given value
	 * 
	 * @param decay
	 *            The Decay time in ms
	 */
	public void setDecay(final int decay){
		setAD(this.attack, decay);
	}

	/**
	 * Sets the sustain level of the envelope.. Attention! It recalculates the
	 * decay and release-phase
	 * 
	 * @param sustain
	 *            The sustain level as value between 0 and 1
	 */
	public void setSustain(final float sustain){
		this.sustain = sustain;
		setRelease(release);
		setDecay(decay);

	}

	/**
	 * Recalculates the release phase of the envelope with the given value
	 * 
	 * @param release
	 *            The release time in ms
	 */
	public void setRelease(final int release){

		this.release = release;
		int releaseInSamples = release * (int)(sampleRate / 1000);

		if(releaseInSamples < 40){
			releaseInSamples = 40;
		}
		if(sustain == 0){
			releaseInSamples = 1;
		}

		envelopeR = new float[releaseInSamples];
		float step = 1;
		if(releaseInSamples != 0){
			step = sustain / releaseInSamples;
		}

		envelopeR[0] = sustain;
		for(int i = 1; i < releaseInSamples; i++){
			envelopeR[i] = envelopeR[i - 1] - step;
		}

	}

	/**
	 * Returns the attack time
	 * 
	 * @return Attack time in ms
	 */
	public int getAttack(){
		return attack;
	}

	/**
	 * Returns the decay time
	 * 
	 * @return Decay time in ms
	 */
	public int getDecay(){
		return decay;
	}

	/**
	 * Returns the sustain level
	 * 
	 * @return Sustain level as value between 0 and 1
	 */
	public float getSustain(){
		return sustain;
	}

	/**
	 * Returns the release time
	 * 
	 * @return Release time in ms
	 */
	public int getRelease(){
		return release;
	}

	@Override
	public void setChanged(){
		super.setChanged();
		notifyObservers();
	}

	/**
	 * Returns a deep copy from an object of this class.
	 * 
	 * @return A new Object with same values as the object from which this is
	 *         called
	 */
	public Envelope copy(){
		final Envelope newEnv = new Envelope();
		newEnv.setADSR(attack, decay, sustain, release);
		return newEnv;
	}

}
