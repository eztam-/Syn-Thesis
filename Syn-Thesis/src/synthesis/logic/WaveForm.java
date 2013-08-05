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

import synthesis.util.MIDI;

/**
 * This class represents a waveform. 
 * @author Matthias Birschl
 *
 */
public class WaveForm extends Observable {

//	private static Logger log = LoggerFactory.getLogger(WaveForm.class);

	private final int periodLength = (int)(VST_Adapter.getCurrentSampleRate() / MIDI.getFreq(0));
	private float[] waveForm = new float[periodLength];

	public WaveForm(){

		clear();

	}

	/**
	 * Sets all samples of this waveform to 0
	 */
	public void clear(){
		waveForm = new float[periodLength];
		for(int i = 0; i < periodLength; i++){
			waveForm[i] = 0f;
		}
	}



	/**
	 * Adds the waveform of a {@link Frequency} with the given level to this waveform.
	 * @param freq The frequency 
	 * @param level A value between 0 and 1
	 */
	public void addFrequency(final Frequency freq, final float level){
		for(int i = 0; i < freq.getSize() && i < waveForm.length; i++){
			waveForm[i] += freq.getSample(i) * level;
		}

	}


	/**
 	* Normalize this waveform
 	*/
	public void normalize(){

		float max = 0;
		for(final float sample: waveForm){
			if(sample > max){
				max = sample;
			}else if(sample * (-1) > max){
				max = sample * (-1);
			}
		}
		if(max != 0){
			for(int i = 0; i < waveForm.length; i++){
				waveForm[i] = waveForm[i] / max;
			}
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

	

	public float getSample(final int position){
		return waveForm[position % waveForm.length]; 
	}

	public void setSample(final int index, final float sample){
		waveForm[index] = sample;
	}


	public int getSize(){
		return waveForm.length;
	}

	/**
	 * Returns the sample on the given position of the waveform. If no sample exist 
	 * on the exact position, the sample gets interpolated with the 
	 * linear interpolation. 
	 * @param position The position of the waveform
	 * @return An interpolated sample as a value between -1 and 1
	 */
	public float getInterpSample(final float position){

		final int x1 = (int)position;
		final int x2 = (int)position + 1;

		final float y1 = waveForm[x1];
		float y2;
		if(x1 + 1 <= waveForm.length - 1){
			y2 = waveForm[x1 + 1];
		}
		else{
			y2 = waveForm[0]; 
		}

		final float interpSample = (position - x1) * y2 + (x2 - position) * y1; 

		return interpSample;
	}
}
