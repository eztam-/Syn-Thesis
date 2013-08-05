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

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractButton;

import synthesis.logic.Envelope;
import synthesis.util.Constants;

/**
 * This is the viewer part (in context of MVC) of the model class {@link Envelope}
 * 
 * This class represents a GUI component, which contains four 
 * knobs ({@link Knob}) to adjust the parameters of an 
 * ADSR-Envelope (attack, decay, sustain, release).
 * This component contains also a graphic representation
 * of the current envelope settings ({@link ADSRplotter})
 * 
 * @author Matthias Birschl
 */
public class EnvelopeView extends AbstractButton implements ActionListener, Observer {


	private static final long serialVersionUID = 1L;
	private final Knob knobA;
	private final Knob knobD;
	private final Knob knobS;
	private final Knob knobR;
	private final ADSRplotter adsrPlotter;

	private final Envelope model;

	/**
	 * Creates a new {@link EnvelopeView} which observes the given model 
	 */
	public EnvelopeView(final Envelope model){

		this.model = model;
		final FlowLayout layout = new FlowLayout();
		layout.setVgap(0);
		this.setLayout(layout);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		adsrPlotter = new ADSRplotter();
		final Dimension adsrPlotterSize = new Dimension(260, 80);
		adsrPlotter.setPreferredSize(adsrPlotterSize);
		adsrPlotter.setSize(adsrPlotterSize);
		this.add(adsrPlotter);

		knobA = new Knob("attack", Constants.ADR_KNOB_IMAGE);
		knobD = new Knob("decay", Constants.ADR_KNOB_IMAGE);
		knobS = new Knob("sustain", Constants.SUSTAIN_KNOB_IMAGE);
		knobR = new Knob("release", Constants.ADR_KNOB_IMAGE);

		knobA.addActionListener(this);
		knobD.addActionListener(this);
		knobS.addActionListener(this);
		knobR.addActionListener(this);

		add(knobA);
		add(knobD);
		add(knobS);
		add(knobR);

		model.addObserver(this);
		update(model, "");
	}

	@Override
	public void actionPerformed(final ActionEvent e){
		adsrPlotter.setADSR(knobA.getValue(), knobD.getValue(), knobS.getValue(), knobR.getValue());
		this.fireActionPerformed(new EnvelopeChangedEvent(this, EnvelopeChangedEvent.ACTION_PERFORMED, EnvelopeChangedEvent.COMMAND));

	}

	/**
	 * The attack value, that the user adjusted
	 * @return A value between 0 and 1
	 */
	public float getAttack(){
		return knobA.getValue();
	}

	/**
	 * The decay value, that the user adjusted
	 * @return A value between 0 and 1
	 */
	public float getDecay(){
		return knobD.getValue();
	}

	/**
	 * The sustain value, that the user adjusted
	 * @return A value between 0 and 1
	 */
	public float getSustain(){
		return knobS.getValue();
	}

	/**
	 * The release value, that the user adjusted
	 * @return A value between 0 and 1
	 */
	public float getRelease(){
		return knobR.getValue();
	}

	/**
	 * Returns the model(in context of MVC) of this viewer
	 */
	public Envelope getEnvModel(){
		return model;
	}

	@Override
	public void update(final Observable o, final Object arg){
	
		final float attack = (float)model.getAttack() / (float)Constants.MAX_ATTACK_TIME;
		final float decay = (float)model.getDecay() / (float)Constants.MAX_DECAY_TIME;
		final float release = (float)model.getRelease() / (float)Constants.MAX_RELEASE_TIME;

		knobA.setValue(attack);
		knobD.setValue(decay);
		knobS.setValue(model.getSustain());
		knobR.setValue(release);
		adsrPlotter.setADSR(attack, decay, model.getSustain(), release);

	}

	
	/**
	 * Adds a controller that handles the events of
	 * this viewer
	 */
	public void addController(final ActionListener controller){
		this.addActionListener(controller);
	}

	/**
	 * This event can be fired to inform the listeners,
	 * that this {@link EnvelopeView} has changed 
	 * 
	 * @author Matthias Birschl
	 * 
	 */
	public class EnvelopeChangedEvent extends ActionEvent {

		private static final long serialVersionUID = 1L;
		public final static String COMMAND = "ENVELOPE_CHANGED";

		public EnvelopeChangedEvent(final Object source, final int id, final String command){
			super(source, id, command);

		}

	}

}
