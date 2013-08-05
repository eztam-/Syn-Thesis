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
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;

import synthesis.logic.Preset;
import synthesis.util.Constants;
import synthesis.util.LoggerFactory;

/**
 * This class represents the "Save As..." panel which is needed
 * to save an {@link Preset} under an other name
 *  
 * @author Matthias Birschl
 */
public class SaveAsWindow extends JPanel {

	private static Logger log = LoggerFactory.getLogger(SaveAsWindow.class);

	private static final long serialVersionUID = 1L;
	boolean save = false;
	private final JTextField textField;

	/**
	 * Creates an new "save as" Panel
	 * @param controller The controller receive the events from
	 * the save and cancel buttons
	 */
	public SaveAsWindow(final ActionListener controller){
		setPreferredSize(new Dimension(450, 43));

		this.setLayout(new FlowLayout());
		final JLabel label = new JLabel("Name:");

		textField = new JTextField();
		textField.setPreferredSize(new Dimension(200, 25));

		ImageIcon saveIcon = null;
		ImageIcon cancelIcon = null;
		try{

			final URL saveUrl = this.getClass().getResource(Constants.SAVE_IMAGE_SMALL);
			final URL cancelUrl = this.getClass().getResource(Constants.CANCEL_IMAGE_SMALL);

			saveIcon = new ImageIcon(saveUrl);
			cancelIcon = new ImageIcon(cancelUrl);

		}catch(final Exception e){
			log.error("Loading image failed", e);
		}

		final JButton saveButton = new JButton("save", saveIcon);
		saveButton.setName("dialog_save");
		final JButton cancelButton = new JButton("cancel", cancelIcon);
		cancelButton.setName("dialog_cancel");

		saveButton.addActionListener(controller);
		cancelButton.addActionListener(controller);

		this.add(label);
		this.add(textField);
		this.add(cancelButton);
		this.add(saveButton);

		setBackground(Color.LIGHT_GRAY);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	/**
	 * Retruns the text, that the user entered in the textfield
	 * @return
	 */
	public String getText(){
		// TODO validate the String
		return textField.getText(); 
	}
}
