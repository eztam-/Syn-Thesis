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

import jvst.wrapper.VSTPluginAdapter;
import synthesis.util.Constants;

/**
 * This class maps all automatable parameters of this plugin to 
 * id numbers. This class makes it possible to read and set an 
 * automatable parameter of this plugin by his id.
 * All this is needed, because the VST host identifies the parameteres
 * by id's
 * @author Matthias Birschl
 *
 */
public class VSTparameters {

	private final Synthesizer synth;

//	private static Logger log = LoggerFactory.getLogger(VSTparameters.class);

	private final String[] paramNames = { "o1 attack", "o1 decay",
			"o1 sustain", "o1 release", "o1 level", "o1 octave",
			"o2 attack", "o2 decay", "o2 sustain", "o2 release",
			"o2 level", "o2 octave", "o3 attack", "o3 decay",
			"o3 sustain", "o3 release", "o3 level", "o3 octave",
			"o4 attack", "o4 decay", "o4 sustain", "o4 release",
			"o4 level", "o4 octave" };

	private final String[] paramLabels = { 
	"ms", "ms", "", "ms", "", "octave", "ms", "ms",
			"", "ms", "", "octave", "ms", "ms",
			"", "ms", "", "octave", "ms", "ms",
			"", "ms", "", "octave" };

	public VSTparameters(final Synthesizer synth){
		this.synth = synth;
	}

	/**
	 * 
	 * Returns the parameter label for the given index
	 * @see VSTPluginAdapter#getParameterLabel(int)
	 */
	public String getParamLabel(final int index){
		return paramLabels[index];
	}

	/**
	 * Returns the parameter name for the given index
	 * @param index The index of the parameter.
	 * @see VSTPluginAdapter#getParameterName(int)
	 */
	public String getParamName(final int index){
		return paramNames[index];
	}

	/**
	 * Returns the parameter value for the given index
	 * @return A value between 0 and 1
	 * @see VSTPluginAdapter#getParameter(int)
	 */
	public float getParameter(int index){

		float value = 0f;
		final Oscillator osc = synth.getOscillator(index / Constants.NUM_AUTO_OSC_PARAMS);
		index %= Constants.NUM_AUTO_OSC_PARAMS;

		switch(index){
			case 0:
				value = (float)osc.getEnvelope().getAttack() / (float)Constants.MAX_ATTACK_TIME;
				break;
			case 1:
				value = (float)osc.getEnvelope().getDecay() / (float)Constants.MAX_DECAY_TIME;
				break;
			case 2:
				value = osc.getEnvelope().getSustain();
				break;
			case 3:
				value = (float)osc.getEnvelope().getRelease() / (float)Constants.MAX_RELEASE_TIME;
				break;
			case 4:
				value = osc.getVolume();
				break;
			case 5:
				value = osc.getTransposeFactor(); 
				break;

		}

		return value;
	}

	/**
	 * Returns a String with the value of the parameter
	 * on the given index
	 * @see VSTPluginAdapter#getParameterDisplay(int)
	 */
	public String getParamDisplay(final int index){

		return Float.toString(getParameter(index));
	}

	/**
	 * Sets value of an parameter
	 * @param index The index of the parameter
	 * @param value Avalue between 0 and 1
	 * @see VSTPluginAdapter#setParameter(int, float)
	 */
	public void setParameter(int index, final float value){

		final Oscillator osc = synth.getOscillator(index / Constants.NUM_AUTO_OSC_PARAMS);
		index %= Constants.NUM_AUTO_OSC_PARAMS;

		switch(index){
			case 0:
				osc.getEnvelope().setAttack((int)(value * Constants.MAX_ATTACK_TIME));
				osc.getEnvelope().setChanged();
				break;
			case 1:
				osc.getEnvelope().setDecay((int)(value * Constants.MAX_DECAY_TIME));
				osc.getEnvelope().setChanged();
				break;
			case 2:
				osc.getEnvelope().setSustain(value);
				osc.getEnvelope().setChanged();
				break;
			case 3:
				osc.getEnvelope().setRelease((int)(value * Constants.MAX_RELEASE_TIME));
				osc.getEnvelope().setChanged();
				break;
			case 4:
				osc.setVolume(value);
				osc.setChanged();
				break;
			case 5:
				osc.setTransposeFactor(convertToTranspFactor(value));
				osc.setChanged();
				break;

		}
	}

	/**
	 * Returns the number of available parameters
	 * 
	 * @see VSTPluginAdapter#getNumParams()
	 */
	public int getNumParams(){

		return paramNames.length;
	}


	/**
	 * Converts the given value to a transpose factor that can transpose
	 * a waveform to whole octaves. The octaves are -2,-1,0,1,2 
	 * Examples for the transpose factor:
	 * 0.5  = -1 octave
	 * 1	= nothing (default pitch)
	 * 2	= +1 octave
	 * 4	= +2 octaves  
	 * @param value A value between 0 and 1
	 * @return A transpose factor with 0.25, 0.5, 1, 2, 4
	 */
	private float convertToTranspFactor(final float value){
		float transpFactor = 1;
		if(value < 0.125){
			transpFactor = 0.25f;
		}else if(value < 0.375){
			transpFactor = 0.5f;
		}else if(value < 0.625){
			transpFactor = 1f;
		}else if(value < 0.875){
			transpFactor = 2f;
		}else if(value <= 1){
			transpFactor = 4f;
		}
		return transpFactor;
	}
}
