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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractButton;

public class LevelSelector extends AbstractButton implements MouseListener {

	private static final long serialVersionUID = 1L;
	private int yPos = getHeight() / 2;
	private float value;
	private boolean hover = false;
	
	public LevelSelector(){
		addMouseListener(this);
	}

	@Override
	public void paint(final Graphics g){
		// super.paint(g);
		final Graphics2D g2 = (Graphics2D)g;
		 
		g.setColor(new Color(220, 220, 220));
		g.fillRect(1,1 , getWidth()-1, getHeight()-1);
		
		if(hover){
			g.setColor(Color.white);
		}else{
			g.setColor(Color.black);
		}

		Rectangle2D rect;
		// Little correction, that very small negative values are also visible
		if(value<0 && yPos==getHeight() / 2){
			yPos+=2;
		}
		
		if(yPos < getHeight() / 2){
			rect = new Rectangle2D.Float(1, yPos, getWidth()-2, getHeight() / 2 - yPos);
		}
		else{
			rect = new Rectangle2D.Float(1, getHeight() / 2, getWidth()-2, yPos - (getHeight() / 2));
		}

		g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		// Farbverlauf
		final GradientPaint gradient = new GradientPaint(0, 0, Color.gray,
				getWidth() / 2, 0, Color.darkGray,
				false
				);

		g2.setPaint(gradient); 
		g2.fill(rect); 

	}

	public void setValue(final float value){

		this.value = value;
		yPos = (int)((value - 1f) / -(1f / (getHeight() / 2f)));
		repaint();
	}

	public float getValue(){

		return value;
	}

	@Override
	public void mouseClicked(final java.awt.event.MouseEvent e){}

	@Override
	public void mousePressed(final java.awt.event.MouseEvent e){
		requestFocus();

		yPos = e.getY();

		// increase the threshold near the zero point about 2px
		if(yPos == (getHeight() / 2) - 1 || yPos == (getHeight() / 2) + 1){
			yPos = getHeight() / 2;
		}
		// increase the threshold near the maximum point about 2px
		if(yPos <= 2){
			yPos = 0;
		}
		// increase the threshold near the minimum point about 2px
		if(yPos >= getHeight() - 2){
			yPos = getHeight();
		}

		value = (1f / (getHeight() / 2f) * (getHeight() / 2f - yPos));
		repaint();

		setChanged();
	}

	/**
	 * Fires a new {@link ActionEvent} and so informs the listeners
	 * that this {@link LevelSelector} has changed 
	 */
	public void setChanged(){
		this.fireActionPerformed(new LevelChangedEvent(this, LevelChangedEvent.ACTION_PERFORMED, LevelChangedEvent.COMMAND));
	}

	@Override
	public void setPreferredSize(final Dimension preferredSize){
		super.setPreferredSize(preferredSize);
		yPos = preferredSize.height / 2;

	}

	@Override
	public void mouseReleased(final java.awt.event.MouseEvent e){}

	@Override
	public void mouseEntered(final java.awt.event.MouseEvent e){
		hover = true;
		repaint();
	}

	@Override
	public void mouseExited(final java.awt.event.MouseEvent e){
		hover = false;
		repaint();
	}

	/**
	 * This event can be fired to inform listeners, that the 
	 * {@link LevelSelector} has changed
	 * 
	 * @author Matthias Birschl
	 * 
	 */
	public class LevelChangedEvent extends ActionEvent {

		private static final long serialVersionUID = 1L;
		public final static String COMMAND = "LEVEL_CHANGED";

		public LevelChangedEvent(final Object source, final int id, final String command){
			super(source, id, command);

		}

	}
}
