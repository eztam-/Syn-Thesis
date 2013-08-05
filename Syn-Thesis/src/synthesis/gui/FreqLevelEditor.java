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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JToggleButton;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import org.apache.log4j.Logger;

import synthesis.util.LoggerFactory;

/**
 * This class represents an button, which pops up an textfield when it 
 * gets clicked by the mouse. 
 * 
 * @author Matthias Birschl
 *
 */
public class FreqLevelEditor extends JToggleButton implements ActionListener, FocusListener, PropertyChangeListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private final JFormattedTextField textField; 
	private static Logger log = LoggerFactory.getLogger(FreqLevelEditor.class);
	private final PopupFactory factory = PopupFactory.getSharedInstance();
	private Popup popup;
	private final NumberFormat format = NumberFormat.getNumberInstance();
	private final LevelSelector levelSelector;
	
	
	/**
	 * Creates a new {@link FreqLevelEditor} which can edit the
	 * value of the given {@link LevelSelector}
	 */
	public FreqLevelEditor(final LevelSelector levelSelector){
		this.levelSelector = levelSelector;

		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(5);

		textField = new JFormattedTextField(format);
		textField.setPreferredSize(new Dimension(50, 25));
		textField.setSize(new Dimension(50, 25));

		addActionListener(this);
		addMouseListener(this);
		textField.addFocusListener(this);
		textField.addPropertyChangeListener(this);

	}

	@Override
	public void actionPerformed(final ActionEvent e){

		if(this.isSelected()){

			textField.removePropertyChangeListener(this);
			textField.removeFocusListener(this);
			
			final Point location = getLocationOnScreen();
			final int xPos = location.x - textField.getWidth() / 2 + this.getWidth() / 2;
			final int yPos = location.y - textField.getHeight();
			popup = factory.getPopup(this, textField, xPos, yPos);

		
			textField.setText(format.format(levelSelector.getValue()));
	
			popup.show();
			textField.requestFocus();

			textField.addPropertyChangeListener(this);
			textField.addFocusListener(this);

		}else if(popup != null){
			popup.hide();
		}

	}

	@Override
	public void focusGained(final FocusEvent arg0){}

	@Override
	public void focusLost(final FocusEvent e){

		if(e.getOppositeComponent() != this){
			log.debug("focus lost");
			popup.hide();
			this.setSelected(false);

		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt){

		if(textField.getValue() != null){
			
			final float val = ((Number)textField.getValue()).floatValue();
			if(val <= 1 && val >= -1 && levelSelector.getValue()!=val){
				log.debug("changed"+val+" "+levelSelector.getValue());
				levelSelector.setValue(val);
				levelSelector.setChanged();
				getRootPane().requestFocus();
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e){}

	@Override
	public void mousePressed(MouseEvent e){}

	@Override
	public void mouseReleased(MouseEvent e){}

	@Override
	public void mouseEntered(MouseEvent e){
		levelSelector.mouseEntered(e);
		
	}

	@Override
	public void mouseExited(MouseEvent e){
		levelSelector.mouseExited(e);
		
	}

}
