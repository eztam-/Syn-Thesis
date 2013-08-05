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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import synthesis.util.LoggerFactory;

/**
 * Represents a knob, which can adjust a value between 0 and 1
 * 
 * @author Matthias Birschl
 *
 */
class Knob extends AbstractButton implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(Knob.class);

	private BufferedImage image;

	/**
	 * The smallest allowed angle, to that this knob is rotatable
	 */
	final float minAngle = -0.65f; 
	/**
	 * The highest allowed angle, to that this knob is rotatable
	 */
	final float maxAngle = 3.8f; 
	/**
	 * The current angel around this knob is rotated
	 */
	float angle = minAngle; 
	
	private final float knobRadius = 22; 

	private final float centerY = 36; 
	private final float centerX = 35; 

	/**
	 * Y position on which a mouse drag has benn started
	 */
	int startDragY; 
	private JLabel label;

	float value = 0; 


	/**
	 * Creates an new knob with the given text as label and the
	 * given image.
	 * @param middleText The text on the bottom of the knob
	 * @param imagePath An image with the dimension of 70 x 65 pixels 
	 */
	public Knob(final String middleText, final String imagePath){ 

		super();

		addMouseMotionListener(this);
		addMouseListener(this);

		setLayout(null);
		label = new JLabel(middleText, JLabel.CENTER);
		final Font curFont = label.getFont();
		label.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 12));

		this.add(label, BorderLayout.PAGE_END);
		label.setSize(50, 15);
		label.setLocation(11, 60);

		loadImage(imagePath);

		final Dimension size = new Dimension(70, 74);
		this.setPreferredSize(size);
		this.setSize(size);
	}

	/**
	 * Loads the image on the given path and sets it as 
	 * the background of this button 
	 * @param imagePath An relative path from the class root to the image
	 */
	protected void loadImage(final String imagePath){

		try{

			final URL imageUrl = this.getClass().getResource(imagePath);
			if(imageUrl != null){
				image = ImageIO.read(imageUrl);
				// image = Toolkit.getDefaultToolkit().getImage(imageUrl);
			}

		}catch(final Exception e){
			log.error("Loading image failed", e);
		}
	}

	@Override
	public void paint(final Graphics g){
		super.paint(g);
		final Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g2.drawImage(image, 0, 0, null);

		g2.rotate(angle, centerX, centerY);

		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));

		final Line2D line = new Line2D.Float(centerX - knobRadius + 8, centerY, centerX, centerY);
		g2.draw(line);

	}

	@Override
	public void mousePressed(final MouseEvent e){

		startDragY = e.getY();
//		this.fireActionPerformed(new KlickEvent(this, KlickEvent.ACTION_PERFORMED, KlickEvent.COMMAND));
		requestFocus();
	}

	@Override
	public void mouseDragged(final MouseEvent e){

		final float tmpAngle = angle + ((startDragY - e.getY()) / 50f);
		if(tmpAngle > minAngle && tmpAngle < maxAngle){
			angle = tmpAngle;
		}
		startDragY = e.getY();

		value = (angle - minAngle) / (-minAngle + maxAngle);

		// Round the minimum and maximum
		if(value < 0.009){
			value = 0;
		}
		else if(value > 0.99){
			value = 1;
		}
		repaint();
		this.fireActionPerformed(new ValueChangedEvent(this, ValueChangedEvent.ACTION_PERFORMED, ValueChangedEvent.COMMAND));

	}


	public void setValue(final float value){
		this.value = value;
		this.angle = (value * (-minAngle + maxAngle)) + minAngle;

		repaint();
	}


	/**
	 * Returns the current value of this knob
	 * @return A value between 0 and 1
	 */
	public float getValue(){
		return value;
	}

	@Override
	public void mouseMoved(final MouseEvent e){}

	@Override
	public void mouseClicked(final MouseEvent e){}

	@Override
	public void mouseReleased(final MouseEvent e){}

	@Override
	public void mouseEntered(final MouseEvent e){}

	@Override
	public void mouseExited(final MouseEvent e){}



	/**
	 * This event can be fired to inform the listeners of a knob
	 * that its value changed
	 * 
	 * @author Matthias Birschl
	 * 
	 */
	public class ValueChangedEvent extends ActionEvent {

		private static final long serialVersionUID = 1L;
		public final static String COMMAND = "VALUE_CHANGED";

		public ValueChangedEvent(final Object source, final int id, final String command){
			super(source, id, command);

		}

	}

//	public class KlickEvent extends ActionEvent {
//
//		private static final long serialVersionUID = 1L;
//		public final static String COMMAND = "KNOB_KLICKED";
//
//		public KlickEvent(final Object source, final int id, final String command){
//			super(source, id, command);
//
//		}
//
//	}

}
