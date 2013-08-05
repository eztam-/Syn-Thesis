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

import java.awt.event.MouseEvent;

import synthesis.util.Constants;

/**
 * Represents a knob, which can adjust an octave
 * between -2 and 2
 * 
 * @author Matthias Birschl
 *
 */
public class OctaveKnob extends Knob {

	private static final long serialVersionUID = 1L;

	/**
	 * The angle on which the knob latches
	 */
	private float latchAngle; 

	/**
	 * Creates an new knob with a label,
	 * with the given text 
	 */
	public OctaveKnob(final String text){
		super(text, Constants.OCTAVE_KNOB_IMAGE);

		loadImage(Constants.OCTAVE_KNOB_IMAGE);

	}


	@Override
	public void mouseDragged(final MouseEvent e){
		float tmpValue = value;
		final float tmpAngle = latchAngle + ((startDragY - e.getY()) / 50f);
		if(tmpAngle > minAngle && tmpAngle < maxAngle){

			latchAngle = tmpAngle;

			if(latchAngle < -0.2){
				angle = -0.63f;
				tmpValue = 0.25f;
			}else if(latchAngle < 0.9){
				angle = 0.53f;
				tmpValue = 0.5f;
			}else if(latchAngle < 2.14){
				angle = 1.62f;
				tmpValue = 1;
			}else if(latchAngle < 2.70){
				angle = 2.7f;
				tmpValue = 2;
			}else if(latchAngle < 3.26){
				angle = 3.78f;
				tmpValue = 4;
			}

		}
		startDragY = e.getY();

		if(tmpValue != value){
			value = tmpValue;
			repaint();
			this.fireActionPerformed(new ValueChangedEvent(this, ValueChangedEvent.ACTION_PERFORMED, ValueChangedEvent.COMMAND));

			
		}

	}

	/**
	 * Sets the current value of this knob.
	 * @param value A transpose factor between 0.25 and 4 
	 * Examples:
	 * 0.25  = -2 octaves
	 * 0.5  = -1 octave
	 * 1	= nothing (default pitch)
	 * 2	= +1 octave
	 * 4	= +2 octaves
	 */
	@Override
	public void setValue(final float value){
		this.value = value;

		if(value == 0.25f){
			angle = -0.63f;
		}else if(value == 0.5f){
			angle = 0.53f;
		}else if(value == 1){
			angle = 1.62f;
		}else if(value == 2){
			angle = 2.7f;
		}else if(value == 4){
			angle = 3.78f;
		}

		repaint();
	}

}
