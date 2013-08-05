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
package synthesis.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import synthesis.gui.EnvelopeView;
import synthesis.gui.LevelSelector;
import synthesis.gui.OscillatorPanel;
import synthesis.logic.Oscillator;
import synthesis.util.Constants;

/**
 * This is the controller(in the context of the MVC pattern) of the
 * {@link OscillatorPanel} which contains GUI components that refer
 * to one specific {@link OscillatorPanel}(the model)
 * 
 * @author Matthias Birschl
 * 
 */
public class OscPanelController implements ActionListener {

	private final Oscillator model;
	private final OscillatorPanel view;

	// private static Logger log
	// =LoggerFactory.getLogger(OscPanelController.class);

	/**
	 * The clipboard stores the levels of the frequencies, which is used for the copy&paste buttons
	 */
	private static float[] clipBoard;	

	public OscPanelController(final Oscillator model, final OscillatorPanel view){
		this.model = model;
		this.view = view;
	}

	@Override
	public void actionPerformed(final ActionEvent e){

		if(e.getSource() instanceof LevelSelector){
			model.setAdditiveFreqLevels(view.getAdditiveFreqPanel()
					.getAdditiveFreqLevels());
		}

		else if(e.getSource() instanceof EnvelopeView){
			final EnvelopeView env = (EnvelopeView)e.getSource();

			// convert the values to ms
			final int attackMs = (int)(env.getAttack() * synthesis.util.Constants.MAX_ATTACK_TIME);
			final int decayMs = (int)(env.getDecay() * synthesis.util.Constants.MAX_DECAY_TIME);
			final int releaseMs = (int)(env.getRelease() * synthesis.util.Constants.MAX_RELEASE_TIME);


			env.getEnvModel().setADSR(attackMs, decayMs, env.getSustain(),
					releaseMs);
		}else if(e.getSource() instanceof JButton){

			processButtonKlicked((JButton)e.getSource());

		}

	}

	/**
	 * Processes mouse clicks on the copy, paste and reset buttons.
	 */
	private void processButtonKlicked(final JButton btn){
		if(btn.getName() == "copy"){
			clipBoard = model.getAdditiveFreqLevels().clone();
		}else if(btn.getName() == "paste" && clipBoard != null){
			model.setAdditiveFreqLevels(clipBoard);
			model.setChanged();
		}else if(btn.getName() == "reset"){

			final float[] levels = new float[Constants.NUM_ADDITIVE_FREQS];
			for(int i = 0; i < levels.length; i++){
				levels[i] = 0;
			}

			model.setAdditiveFreqLevels(levels);
			model.setChanged();
		}
	}

}
