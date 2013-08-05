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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

import synthesis.logic.Envelope;

/**
 * This class represents an graphical
 * view of the ADSR parameters of a {@link Envelope}
 * 
 * @author Matthias Birschl
 *
 */
class ADSRplotter extends JComponent {


	private static final long serialVersionUID = 1L;
	private int attack;
	private int decay;
	private int sustain;
	private int release;


	@Override
	public void paint(final Graphics g){
		final Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.black);
		g2.clearRect(0, 0, getWidth(), getHeight());
		
		final Point p1 = new Point(3, getHeight() - 4);
		final Point pA = new Point(attack + 3, 3);
		final Point pD = new Point(attack + decay + 3, getHeight() - 4 - sustain);
		final Point pS = new Point(attack + decay + 23 + 3, getHeight() - 4 - sustain);
		final Point pR = new Point(attack + decay + 23 + release + 3, getHeight() - 4);
		if(pD.y < 3){
			pD.y = 3;
			pS.y = 3;
		}

		// Create the path of the envelope
		final GeneralPath path = new GeneralPath();
		path.moveTo(p1.x, p1.y);
		path.lineTo(pA.x, pA.y);
		path.lineTo(pD.x, pD.y);
		path.lineTo(pS.x, pS.y);
		path.lineTo(pR.x, pR.y);

		// paint the fill
		g2.setColor(new Color(0, 70, 0));
		g2.fill(path);
		g2.setStroke(new BasicStroke(3f));
		g2.drawLine(0, getHeight() - 3, pR.x, getHeight() - 3); 

		drawGrid(g2);
		
		// Draw the envelope
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		g2.setColor(Color.green);
		g2.draw(path);

		// Draw the conjunction points
		g2.setStroke(new BasicStroke(3));
		g2.draw(new Ellipse2D.Float(p1.x - 2, p1.y - 2, 4, 4));
		g2.draw(new Ellipse2D.Float(pA.x - 2, pA.y - 2, 4, 4));
		g2.draw(new Ellipse2D.Float(pD.x - 2, pD.y - 2, 4, 4));
		g2.draw(new Ellipse2D.Float(pS.x - 2, pS.y - 2, 4, 4));
		g2.draw(new Ellipse2D.Float(pR.x - 2, pR.y - 2, 4, 4));

	}

	
	/**
	 * Draws the grid in the background of the ADSRplotter
	 */
	private void drawGrid(Graphics2D g2){

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(Color.GRAY);

		final float dash1[] = { 1.0f, 2f };
		final BasicStroke dashStroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

		final float dash2[] = { 1.0f, 4f };
		final BasicStroke dashStroke2 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash2, 0.0f);

		g2.setStroke(dashStroke1);

		g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
		g2.setStroke(dashStroke2);
		for(int i = 20; i < getWidth(); i += 20){

			g2.drawLine(i, 0, i, getHeight());
		}
	}
	
	/**
	 * Sets the ADSR parameters so this component can
	 * paint the envelope
	 * @param attack A value between 0 and 1
	 * @param decay A value between 0 and 1
	 * @param sustain A value between 0 and 1
	 * @param release A value between 0 and 1
	 */
	public void setADSR(final float attack, final float decay, final float sustain, final float release){

		this.attack = (int)(attack * (getHeight() - 4));
		this.decay = (int)(decay * (getHeight() - 4));
		this.sustain = (int)(sustain * (getHeight() - 4));
		this.release = (int)(release * (getHeight() - 4));
		repaint();
	}
}
