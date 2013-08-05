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
package synthesis.util;

/**
 * This is a helper class, which converts MIDI note numbers to
 * frequencies
 * @author Matthias Birschl
 *
 */
public class MIDI {

	private static float[] frequencies = new float[128];

	static{
		for(int midiNote = 0; midiNote < 128; midiNote++){

			// 69 is the concert pitch(440 Hz)
			frequencies[midiNote] = 440 * (float)Math.pow(2, ((midiNote - 69.0) / 12.0)); 
		}

	}

	/**
	 * Returns the frequency related to the given midiNote
	 * @param midiNote A value between 0 and 127
	 * @return The frequency in Hz
	 */
	public static float getFreq(final int midiNote){

		return frequencies[midiNote];
	}
}
