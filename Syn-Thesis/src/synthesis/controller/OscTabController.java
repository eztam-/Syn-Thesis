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

import javax.swing.JTabbedPane;


import synthesis.gui.OscillatorTab;
import synthesis.logic.Oscillator;
import synthesis.logic.Synthesizer;

/**
 * This is the controller(in the context of the MVC pattern) of the
 * {@link OscillatorTab} which contains some GUI components related to one
 * specific oscillator. And switches to the oscillators that are belong
 * to the tabs
 * 
 * @author Matthias Birschl
 * 
 */
public class OscTabController implements ActionListener {

	JTabbedPane tabbedPane;
	Synthesizer synth;

	public OscTabController(final JTabbedPane tabbedPane, final Synthesizer synth){
		this.tabbedPane = tabbedPane;
		this.synth = synth;
	}

	@Override
	public void actionPerformed(final ActionEvent e){

		if(e.getSource() instanceof OscillatorTab){

			final OscillatorTab tab = (OscillatorTab)e.getSource();
			Oscillator osc = null;

			for(int i = 0; i < tabbedPane.getTabCount(); i++){
				if(tabbedPane.getTabComponentAt(i) == tab){
					tabbedPane.setSelectedIndex(i);
					osc = synth.getOscillator(i);
					break;
				}
			}

			if(osc != null){
				osc.setVolume(tab.getVolume());
				osc.setTransposeFactor(tab.getTransponseFactor());
			}

		}

	}

}
