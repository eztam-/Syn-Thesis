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

import javax.sound.midi.ShortMessage;

import jvst.wrapper.VSTPluginAdapter;
import jvst.wrapper.valueobjects.VSTEvent;
import jvst.wrapper.valueobjects.VSTEvents;
import jvst.wrapper.valueobjects.VSTMidiEvent;

import org.apache.log4j.Logger;

import synthesis.util.Constants;
import synthesis.util.LoggerFactory;
/**
 * This class processes the calls from the VST host.
 * 
 * @author Matthias Birschl
 *
 */
public class VST_Adapter extends VSTPluginAdapter {

	private final Synthesizer synth = new Synthesizer();
	private final VSTparameters parameters = new VSTparameters(synth);
	private static float sampleRate = 44100;
	private static Logger log = LoggerFactory.getLogger(VST_Adapter.class);

	// private boolean bypass = false;

	public VST_Adapter(final long wrapper){
		super(wrapper);
		System.setProperty("user.dir", getLogBasePath());
		
		setIsLogEnabled(false);

		this.isSynth(true);
		this.canProcessReplacing(true);

		// Plugin ID for this synth from steinberg
		this.setUniqueID('9' << 24 | '2' << 16 | '1' << 8 | 'k');

		sampleRate = getSampleRate();

	}

	public static String getBasePath(){
		return getLogBasePath();
	}

	public static float getCurrentSampleRate(){
		return sampleRate;
	}


	@Override
	public int canDo(final String feature){

		if(VSTPluginAdapter.CANDO_PLUG_RECEIVE_VST_EVENTS.equals(feature)){
			return VSTPluginAdapter.CANDO_YES;
		}
		if(VSTPluginAdapter.CANDO_PLUG_RECEIVE_VST_MIDI_EVENT.equals(feature)){
			return VSTPluginAdapter.CANDO_YES;
		}
		// if(VSTPluginAdapter.CANDO_PLUG_BYPASS.equals(feature)){
		// return VSTPluginAdapter.CANDO_YES;
		// }

		return VSTPluginAdapter.CANDO_NO;
	}

	@Override
	public boolean setBypass(final boolean value){
		// Seems not to work. The method is never called by the wrapper !?
		return false;
	}

	@Override
	public String getProductString(){
		return Constants.PRODUCT_NAME;
	}

	@Override
	public String getEffectName(){
		return Constants.PRODUCT_NAME;
	}

	@Override
	public String getVendorString(){
		return Constants.VENDOR_NAME;
	}


	@Override
	public int getPlugCategory(){
		return VSTPluginAdapter.PLUG_CATEG_SYNTH;
	}


	@Override
	public void setSampleRate(final float sampleRate){

		VST_Adapter.sampleRate = sampleRate;
		log.info("Samplerate was set to: " + sampleRate);
	}

	@Override
	public void processReplacing(final float[][] inputs, final float[][] outputs, final int sampleFrames){


		for(int i = 0; i < sampleFrames; i++){
			outputs[0][i] = synth.getNextSample();
			outputs[1][i] = outputs[0][i];
		}
	
	}


	@Override
	public int processEvents(final VSTEvents e){

		for(int i = 0; i < e.getNumEvents(); i++){

			
			if(e.getEvents()[i].getType() == VSTEvent.VST_EVENT_MIDI_TYPE){
				final VSTMidiEvent event = (VSTMidiEvent)e.getEvents()[i];
				final byte[] midiMessage = event.getData();

				final int status = midiMessage[0] & 0xf0; // Only the nibble with the MIDI-status is needed

				if(status == ShortMessage.PITCH_BEND){
					processPitchBendEvent(midiMessage);

				}

			
				if(status == ShortMessage.NOTE_ON || status == ShortMessage.NOTE_OFF){ 

					processNoteEvent(midiMessage, status);

				}

			}
		}

		return 1;
	}

	/**
	 * Process a pitch bend change.
	 */
	private void processPitchBendEvent(final byte[] midiMessage){
		int a = midiMessage[2];
		a <<= 7;
		a |= midiMessage[1];

		float pitch = (a / 8192f) - 1; // Convert to a value between -1 and 1

		if(pitch > 0){
			pitch = pitch + 1;
		}
		else if(pitch < 0){
			pitch = 0.5f * pitch + 1f; // 1 Octave
			// pitch= 0.75f*pitch+1f; // 2 Octaves
		}else{
			pitch = 1;
		}
		synth.setPitch(pitch);
		
	}
	
	/**
	 * Process a "note on" or note "off event"
	 */
	private void processNoteEvent(final byte[] midiMessage, final int status){
		
		final int note = midiMessage[1] & 0x7f;
		int velocity = midiMessage[2] & 0x7f;
		if(status == ShortMessage.NOTE_OFF){ 
			synth.noteOff(note);
			velocity = 0;
		}
		if(status == ShortMessage.NOTE_ON){ 
			synth.noteOn(note, velocity / 128f);
		}
	}
	
	
	@Override
	public void setParameter(final int index, final float value){
		if(value <= 1 && value >= 0){
			parameters.setParameter(index, value);
		}
	}

	@Override
	public float getParameter(final int index){
		return parameters.getParameter(index);
	}


	@Override
	public String getParameterName(final int index){

		return parameters.getParamName(index);
	}


	@Override
	public String getParameterDisplay(final int index){
		return parameters.getParamDisplay(index);
	}


	@Override
	public String getParameterLabel(final int index){

		return parameters.getParamLabel(index);
	}

	@Override
	public int getNumParams(){

		return parameters.getNumParams();
	}

	@Override
	public boolean string2Parameter(final int index, final String value){
		boolean ret = false;
		try{
			final float val = Float.parseFloat(value);
			setParameter(index, val);
			ret = true;
		}catch(final Exception e){
			log.warn("Failed to set the parameter to the given string ", e);
		}

		return ret;
	}

	/**
	 * Returns the synthesizer on which all the VST events get applyed
	 */
	public Synthesizer getSynthesizer(){
		return synth;
	}

	@Override
	public int getNumPrograms(){
		// Not supported by this plugin
		return 0;
	}

	@Override
	public int getProgram(){
		// Not supported by this plugin
		return 0;
	}

	@Override
	public void setProgram(final int index){
		// Not supported by this plugin
	}

	@Override
	public void setProgramName(final String name){
		// Not supported by this plugin
	}

	@Override
	public String getProgramName(){
		// Not supported by this plugin
		return "";
	}

	@Override
	public String getProgramNameIndexed(final int category, final int index){
		// Not supported by this plugin

		// List<Preset> presets = synth.getPresets();
		// if(presets.size()>index){
		// return presets.get(index).getName();
		// }
		return "";
	}
}
