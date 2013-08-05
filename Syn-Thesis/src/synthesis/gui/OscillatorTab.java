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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractButton;
import javax.swing.JLabel;

import synthesis.logic.Oscillator;
import synthesis.util.Constants;

/**
 * This is the a viewer (in context of MVC) of the model 
 * class {@link Oscillator}
 * An object of this class are representing one tab, that belongs to one 
 * {@link Oscillator}. It shows two knobs, for adjusting the volume 
 * and the octave pitch of the model.
 * 
 * @author Matthias Birschl
 *
 */
public class OscillatorTab extends AbstractButton implements ActionListener, Observer {

	private static final long serialVersionUID = 1L;
	private final Knob volKnob;
	private final OctaveKnob octaveKnob;
	private final Oscillator model;

	/**
	 * Creates an new {@link OscillatorTab} with the given model
	 * and the given label text.
	 * @param labelText This string is shown as a label on tab
	 * @param model The observable model 
	 */
	public OscillatorTab(final String labelText, final Oscillator model){
		this.model = model;
		
		final Dimension size = new Dimension(135, 110);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(null);
		
		
		final JLabel label = new JLabel(labelText);
		label.setBounds(0, 5, getWidth(), 15);
		

		final Font curFont = label.getFont();
		label.setFont(new Font(curFont.getFontName(), Font.BOLD, 12));
		
		this.add(label);

		volKnob = new Knob("level", Constants.DEFAULT_KNOB_IMAGE);
		octaveKnob = new OctaveKnob("octave");
		octaveKnob.setBounds(0, size.height-octaveKnob.getHeight()-5, octaveKnob.getWidth(), octaveKnob.getHeight());
		volKnob.setBounds(70,size.height-volKnob.getHeight()-5, octaveKnob.getWidth(), octaveKnob.getHeight());
		
		volKnob.addActionListener(this);
		octaveKnob.addActionListener(this);

		this.add(octaveKnob);
		this.add(volKnob);
		model.addObserver(this);
		update(model,"");
	}

	@Override
	public void actionPerformed(final ActionEvent e){

		fireActionPerformed(e);

	}

	/**
	 * Returns the current value of the volume knob
	 * @return A value between 0 and 1
	 */
	public float getVolume(){
		return volKnob.getValue();
	}

	/**
	 * Returns the current value of the octave knob
	 * @return A transpose factor. Examples:
	 * 0.25  = -2 octaves
	 * 0.5  = -1 octave
	 * 1	= nothing (default pitch)
	 * 2	= +1 octave
	 * 4	= +2 octaves   
	 */
	public float getTransponseFactor(){
		return octaveKnob.getValue();
	}

	@Override
	public void update(final Observable arg0, final Object arg1){
		volKnob.setValue(model.getVolume());
		octaveKnob.setValue(model.getTransposeFactor());

	}

}
