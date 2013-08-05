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
import java.awt.GridLayout;
import java.net.URL;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import synthesis.controller.MenuBarController;
import synthesis.logic.Preset;
import synthesis.logic.Synthesizer;
import synthesis.util.Constants;
import synthesis.util.LoggerFactory;

/**
 * This is a viewer (in context of MVC) of the model class {@link Synthesizer}
 * This class is an {@link JToolBar} which contains components to
 * select/modify the presets ({@link Preset}) of this plugin.
 * This {@link JToolBar} also contains some other buttons
 * which show informations about this plugin.
 * @author Matthias Birschl
 *
 */
public class MenuBar extends JToolBar implements Observer {

	private static Logger log = LoggerFactory.getLogger(MenuBar.class);

	private static final long serialVersionUID = 1L;

	private final JButton saveButton = new JButton();
	private final JButton saveAsButton = new JButton();
	private final JButton deleteButton = new JButton();
	private final JButton logButton = new JButton();
	private final JButton infoButton = new JButton();
//	private final JButton uploadButton = new JButton();
	private final JComboBox presetBox;
	private final Synthesizer synth;
	private final MenuBarController controller;
	private JFrame infoFrame; 
	
	
	/**
	 * Creates a new {@link MenuBar} with the given observable model 
	 * @param synth The model which gets observed
	 */
	public MenuBar(final Synthesizer synth){

		this.synth = synth;
		setFloatable(false);
		controller = new MenuBarController(synth, this);

		try{

			final URL saveImageUrl = this.getClass().getResource(Constants.SAVE_IMAGE);
			final URL deleteImageUrl = this.getClass().getResource(Constants.DELETE_IMAGE);
			final URL saveAsImageUrl = this.getClass().getResource(Constants.SAVE_AS_IMAGE);
			final URL logImageUrl = this.getClass().getResource(Constants.LOG_IMAGE);
			final URL infoImageUrl = this.getClass().getResource(Constants.INFO_IMAGE);
//			final URL uploadImageUrl = this.getClass().getResource(Constants.UPLOAD_IMAGE);
			
			saveButton.setIcon(new ImageIcon(saveImageUrl));
			deleteButton.setIcon(new ImageIcon(deleteImageUrl));
			saveAsButton.setIcon(new ImageIcon(saveAsImageUrl));
			logButton.setIcon(new ImageIcon(logImageUrl));
			infoButton.setIcon(new ImageIcon(infoImageUrl));
//			uploadButton.setIcon(new ImageIcon(uploadImageUrl));

		}catch(final Exception e){
			log.error("Loading image failed", e);
		}

		saveButton.setName("save");
		saveAsButton.setName("saveas");
		deleteButton.setName("delete");
		logButton.setName("log_button");
		infoButton.setName("info");
//		uploadButton.setName("send_presets");
		
		saveButton.setToolTipText("Save Preset");
		saveAsButton.setToolTipText("Save Preset As...");
		deleteButton.setToolTipText("Delete Preset");
		logButton.setToolTipText("Show Log Window");
		infoButton.setToolTipText("Information");
//		uploadButton.setToolTipText("Please share your presets!");
		
		presetBox = new JComboBox();
		presetBox.setPreferredSize(new Dimension(250, 25));
		presetBox.setSelectedItem(null);
		
		presetBox.addPopupMenuListener(controller);
		logButton.addActionListener(controller);
		presetBox.addActionListener(controller);
		saveButton.addActionListener(controller);
		saveAsButton.addActionListener(controller);
		deleteButton.addActionListener(controller);
		infoButton.addActionListener(controller);
//		uploadButton.addActionListener(controller);
		
		//this.add(new JLabel("Preset:"));
		this.add(presetBox);
		this.addSeparator();
		this.add(saveButton);
		this.add(saveAsButton);
		this.add(deleteButton);
		this.addSeparator();
		this.add(infoButton);
		this.add(logButton);
//		this.add(uploadButton);
		synth.addObserver(this);
		synth.setChanged();

	}

	
	/**
	 * Shows an new window with the text from {@link Constants#INFO_TEXT}
	 */
	public void showInfoFrameVisible(){
		final Dimension size = new Dimension(300,200);
		if(infoFrame==null){
			infoFrame = new JFrame();
			infoFrame.setLayout( new GridLayout(0,1));
			infoFrame.setSize(size);
			infoFrame.add(new JLabel(Constants.INFO_TEXT));
		}
		final int x = getLocationOnScreen().x + getWidth()/2 - size.width/2;
		final int y =  getLocationOnScreen().y + 190;
		infoFrame.setBounds(x,y, size.width, size.height);
		infoFrame.setVisible(true);
	}
	
	/**
	 * Unselects the current {@link Preset} in the preset combobox
	 */
	public void clearSelection(){
		presetBox.setSelectedItem(null);
	}
	
	/**
	 * Returns the currently selected {@link Preset} of the combobox
	 */
	public Preset getSelectedPreset(){

		if(presetBox.getSelectedItem() instanceof Preset){
			return (Preset)presetBox.getSelectedItem();
		}
		return null;
	}


	
	@Override
	public void update(final Observable arg0, final Object arg1){
		Preset selectedPreset = (Preset)presetBox.getSelectedItem();
		
		presetBox.removeAllItems();
		Collections.sort(synth.getPresets());

		for(final Preset preset: synth.getPresets()){
			presetBox.addItem(preset);
		}

		if(arg1 instanceof Preset){
			presetBox.setSelectedItem(arg1);
		}else{
			presetBox.setSelectedItem(selectedPreset);
		}
	}

}
