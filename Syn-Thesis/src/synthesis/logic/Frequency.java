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

/**
 * This class represents an frequency and its waveform
 * 
 * @author Matthias Birschl
 */
public class Frequency extends WaveForm {



	/**
	 * Calculates the waveform of one period of the given frequency
	 * once. So the samples of the waveform can be read in constant time. 
	 * @param frequency The frequency in Hz
	 */
	public Frequency(final float frequency){

		final float periodLength = VST_Adapter.getCurrentSampleRate() / frequency;
		float sample;
		for(int i = 0; i < getSize(); i++){
			sample = (float)Math.sin(i * ((2 * Math.PI) / periodLength));
			setSample(i, sample);
		}
	}







}
