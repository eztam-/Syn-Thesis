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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import synthesis.logic.WaveForm;

/**
 * This class represents a GUI component, which draws the graph of
 * a waveform. This is the viewer part (in context of MVC) of the 
 * model class {@link WaveForm}
 * 
 * @author Matthias Birschl
 */
class WaveFormView extends JPanel implements Observer {


	private static final long serialVersionUID = 1L;
	private final GeneralPath waveFormPath;
	private final WaveForm model;

	/**
	 * Creates a new waveform viewer that observers the given
	 * model ({@link WaveForm})  
	 */
	public WaveFormView(final WaveForm model){
		waveFormPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		this.model = model;
		model.addObserver(this);

	}

	@Override
	public void paint(final Graphics g){
		super.paint(g);
		final Graphics2D g2 = (Graphics2D)g;

		g2.setBackground(Color.black);
		g2.clearRect(0, 0, getWidth(), getHeight());

		// Painting the grid
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		g2.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(1f));
		g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
		g2.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

		final float dash[] = { 1.0f, 5f };
		g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

		g2.drawLine(0, getHeight() / 4, getWidth(), getHeight() / 4);
		g2.drawLine(0, getHeight() - getHeight() / 4, getWidth(), getHeight()
				- getHeight() / 4);

		g2.drawLine(getWidth() / 4, 0, getWidth() / 4, getHeight());
		g2.drawLine(getWidth() - getWidth() / 4, 0,
				getWidth() - getWidth() / 4, getHeight());

		//Painting the waveform
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.green);
		g2.draw(waveFormPath);

		// Glass/Glow effect
		final Rectangle glassSquareTop = new Rectangle(0, 0, getWidth(), getHeight() / 2);
		final GradientPaint paintTop = new GradientPaint(0, 0, new Color(1, 1, 1, 0.15f), 0, getHeight() / 2, new Color(0.9f, 1, 0.9f, 0.0f));
		g2.setPaint(paintTop);
		g2.fill(glassSquareTop);
		g2.draw(glassSquareTop);

		final Rectangle glassSquareBottom = new Rectangle(0, getHeight() / 2, getWidth(), getHeight());
		final GradientPaint paintBottom = new GradientPaint(0, getHeight() / 2, new Color(0.9f, 1, 0.9f, 0.0f), 0, getHeight(), new Color(1, 1, 1, 0.15f));
		g2.setPaint(paintBottom);
		g2.fill(glassSquareBottom);
		g2.draw(glassSquareBottom);

	}

	@Override
	public void update(final Observable o, final Object arg){

		final float step = model.getSize() / (getWidth() -2f);

		waveFormPath.reset();
		waveFormPath.moveTo(0, getHeight() / 2);
		for(float i = 0; i * step < model.getSize(); i++){
			waveFormPath.lineTo(i, model.getInterpSample(step * i) 
					* (getHeight() / 2) + getHeight() / 2);
		}
		waveFormPath.lineTo(getWidth(), getHeight()/2);
		repaint();
	}
}
