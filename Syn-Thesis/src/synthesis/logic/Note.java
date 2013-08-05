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

import synthesis.util.Constants;
import synthesis.util.MIDI;

/**
 * Represents one note, that is played by the synthesizer.
 * @author Matthias Birschl
 */
class Note {


	private final float[] samplePositions = { 0, 0, 0, 0 };

	private final int midiNote;
	private float velocity;
	private float tmpVelocity;
	
	private boolean isReleasing = false;
	private int envelopePos = 0;
	public boolean isFinihed;

	private final float step;
	private final int waveFormSize;
	private final Synthesizer synth;

	private float value = 0;

	/**
	 * If true, then this note just plays until the amplitude
	 * reaches an level near to zero. After this, this note
	 * can be reset and play again 
	 */
	private boolean phaseFadeOut = false; 

//	private static Logger log = LoggerFactory.getLogger(Note.class);

	/**
	 * Creates a new note, that can be played by the synthesizer
	 * @param midiNote The midi value of the note
	 * @param velocity The velocity of the note
	 * @param synth The synthesizer, that plays this note
	 */
	public Note(final int midiNote, final float velocity, final Synthesizer synth){
		this.midiNote = midiNote;
		this.synth = synth;
		this.velocity = velocity;
		
		final float lowestFreq = MIDI.getFreq(0);
		step = MIDI.getFreq(midiNote) / lowestFreq;
		waveFormSize = synth.getOscillator(0).getWaveForm().getSize(); 
	}

	/**
	 * Returns the next sample of this note.
	 * The calculation of the sample includes the settings of
	 * all active oscillators (including the envelopes) 
	 * @return A sample as a value between -1 and 1
	 */
	public float getNextSample(){ 

		float sample = 0;


		// Gets reseted by getNextEnvValue(Envelope envelope) as long as 
		// all envelopes reached their end
		isFinihed = true; 
							

		for(int i = 0; i < Constants.NUM_OSCILLATORS; i++){

			final Oscillator osc = synth.getOscillator(i);
			
			// TODO Also oscillators with a volume == 0 can be skipped 
			if(osc.hasWaveForm()){ // Only active oscillators gets considered
				
				if(samplePositions[i] >= waveFormSize - 1){
					samplePositions[i] = samplePositions[i] - waveFormSize + 1;
				}

				sample += osc.getWaveForm().getInterpSample(samplePositions[i]) * getNextEnvValue(osc.getEnvelope()) * osc.getVolume();
				samplePositions[i] += step * synth.getPitch() * osc.getTransposeFactor();

			}
		}

		envelopePos++;
		value = sample * velocity;

		if(phaseFadeOut){
			// TODO Use a more precise solution to detect a amplitude near the zero point
			if(value < 0.01f && value > -0.01f){ 
				isReleasing = false;
				isFinihed = false;
				envelopePos = 0;
				samplePositions[0] = 0;
				samplePositions[1] = 0;
				samplePositions[2] = 0;
				samplePositions[3] = 0;
				velocity = tmpVelocity;
				phaseFadeOut = false;
			}

		}

		return value;
	}

	/**
	 * Returns the next envelope value of the given envelope.
	 * @return A value between 0 and 1
	 */
	private float getNextEnvValue(final Envelope envelope){ 
		float value = 0;

		if(!isReleasing){
			value = envelope.getValueAD(envelopePos);
			isFinihed = false;
		}
		else if(envelopePos < envelope.getSizeR()){
			value = envelope.getValueR(envelopePos);
			isFinihed = false;
		}

		return value;

	}

	// /**
	// * Checks, which of the given values is closer to zero
	// *
	// * @param a
	// * @param b
	// * @return true if value a is closer to zero or both are equal
	// * false if b is closer to zero
	// *
	// */
	// private boolean closerToZero(float a, float b){
	// // Make the values positive
	// a = (a <= 0.0F) ? 0.0F - a : a;
	// b = (b <= 0.0F) ? 0.0F - b : b;
	//
	// return a <= b;
	// }

	@Override
	public boolean equals(final Object obj){
		if(obj instanceof Note){
			return midiNote == ((Note)obj).midiNote;
		}
		return false;
	}

	@Override
	public int hashCode(){
		return midiNote;
	}

	/**
	 * Sets the envelope states of this note to the 
	 * release phase.
	 */
	public void setReleasing(){
		envelopePos = 0;
		isReleasing = true;
	}

	/**
	 * Returns the MIDI note value of this note
	 * @return A value between 0 and 127
	 */
	public int getMidiNote(){
		return midiNote;
	}

	/**
	 * Sets the velocity of this note
	 * @param velocity A value between 0 and 1
	 */
	public void setVelocity(final float velocity){
		this.velocity = velocity;
	}

	/**
	 * Plays this note anymore until the amplitude reaches a point near to zero.
	 * After this, the note will play again from beginning with the given
	 * velocity
	 * 
	 * @param newVelocity
	 */
	public void reset(final float newVelocity){

		this.tmpVelocity = velocity;
		phaseFadeOut = true;

	}

}