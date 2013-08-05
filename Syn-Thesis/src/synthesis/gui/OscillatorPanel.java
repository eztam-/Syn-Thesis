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
package synthesis.gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import synthesis.controller.OscPanelController;
import synthesis.logic.Oscillator;

/**
 * This is a viewer (in context of MVC) of the model class {@link Oscillator}
 * This class represents a GUI component which contains all
 * settings of one specific {@link Oscillator} except the volume and the 
 * octave pitch settings. 
 * @author Matthias Birschl
 *
 */
public class OscillatorPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AdditiveFreqPanel additiveFreqs;
	private WaveFormView waveFormView;
	private EnvelopeView envelope;
	private final Oscillator model;

	public OscillatorPanel(final Oscillator model){

		// this.setPreferredSize(new Dimension(370, 500));
		this.model = model;
		createAdditiveFreqPanel();
		createEnvelopePanel();
		createWaveFormPanel();
	}

	/**
	 * Creates the {@link AdditiveFreqPanel} and adds it to this
	 * {@link OscillatorPanel}
	 */
	private void createAdditiveFreqPanel(){

		additiveFreqs = new AdditiveFreqPanel(model);
		final Dimension addSize = new Dimension(900, 260);
		additiveFreqs.setPreferredSize(addSize);
		final TitledBorder additiveFreqBorder = BorderFactory.createTitledBorder("Harmonic Frequencies");
		additiveFreqBorder.setTitleJustification(TitledBorder.CENTER);
		additiveFreqs.setBorder(additiveFreqBorder);
		this.add(additiveFreqs);

	}

	/**
	 * Creates the {@link WaveFormView} and adds it to this
	 * {@link OscillatorPanel}
	 */
	private void createWaveFormPanel(){
		final Dimension waveFormSize = new Dimension(519, 155);
		waveFormView = new WaveFormView(model.getWaveForm());
		waveFormView.setPreferredSize(waveFormSize);
		waveFormView.setSize(waveFormSize);

		final JPanel waveformPanel = new JPanel();
		final TitledBorder waveFormBorder = BorderFactory.createTitledBorder("Waveform");
		waveFormBorder.setTitleJustification(TitledBorder.CENTER);
		waveformPanel.setBorder(waveFormBorder);
		waveformPanel.add(waveFormView);

		this.add(waveformPanel);
	}

	
	/**
	 * Creates the {@link EnvelopeView} and adds it to this
	 * {@link OscillatorPanel}
	 */
	private void createEnvelopePanel(){

		envelope = new EnvelopeView(model.getEnvelope());
		 final Dimension envelopeSize = new Dimension(300, 155);
		 envelope.setPreferredSize(envelopeSize);

		final JPanel envPanel = new JPanel(); // Extra panel because the border-text
												// is just for jPanels anti-aliased
		
		final TitledBorder envelopeBorder = BorderFactory.createTitledBorder("Envelope");
		envelopeBorder.setTitleJustification(TitledBorder.CENTER);
		envPanel.setBorder(envelopeBorder);

		envPanel.add(envelope);
		this.add(envPanel);
	}

	/**
	 * Adds a controller to this panel and all his
	 * child components.
	 */
	public void addController(final OscPanelController controller){
		additiveFreqs.addController(controller);
		envelope.addController(controller);
	}

	/**
	 * Returns the {@link AdditiveFreqPanel} of this panel.
	 */
	public AdditiveFreqPanel getAdditiveFreqPanel(){
		return additiveFreqs;
	}

}
