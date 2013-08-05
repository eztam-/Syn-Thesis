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
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import synthesis.logic.Oscillator;
import synthesis.util.Constants;
import synthesis.util.LoggerFactory;

/**
 * This is a viewer (in context of MVC) of the model class {@link Oscillator}
 * This is a GUI component which allows the user to set the overtones
 * of the model.
 * @author Matthias Birschl
 *
 */
public class AdditiveFreqPanel extends JPanel implements Observer {

	private static Logger log = LoggerFactory.getLogger(AdditiveFreqPanel.class);

	private static final long serialVersionUID = 1L;
	private final LevelSelector[] levelSelector;

	private final FreqLevelEditor[] levelEditors = new FreqLevelEditor[Constants.NUM_ADDITIVE_FREQS];

	private final Oscillator model;
	private JButton copyButton, pasteButton, resetButton;

	/**
	 * Creates a new {@link AdditiveFreqPanel} with the given model
	 */
	public AdditiveFreqPanel(final Oscillator model){

		this.model = model;
		levelSelector = new LevelSelector[Constants.NUM_ADDITIVE_FREQS];

		final BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(layout);

		this.add(createFreqLabelPanel());
		this.add(createFreqPanel());
		this.add(editButtonPanel());

		model.addObserver(this);

	}

	/**
	 * Creates the labels for the harmonics. Each label shows
	 * the number of the harmonic
	 * @see Constants#NUM_ADDITIVE_FREQS
	 */
	private JPanel createFreqLabelPanel(){
		final JPanel freqLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));

		final Box box = new Box(BoxLayout.X_AXIS);
		box.setPreferredSize(new Dimension(50, 15));
		freqLabelPanel.add(box);
		Dimension freqLabelSize=null;

		for(int i = 0; i < levelSelector.length; i++){
			
			freqLabelSize = new Dimension(getFreqWidth(i),15);
			
			
			JLabel label;
			label = new JLabel("" + (i + 1));
			label.setBackground(new Color(180, 180 , 180 ));
			label.setOpaque(true);
			label.setPreferredSize(freqLabelSize);
			label.setFont(new Font("SansSerif", Font.PLAIN, 9));
			label.setHorizontalAlignment(JLabel.CENTER);
			freqLabelPanel.add(label);
		}
		this.add(freqLabelPanel);

		return freqLabelPanel;
	}

	/**
	 * Creates a panel with one {@link LevelSelector} for each 
	 * of the {@link Constants#NUM_ADDITIVE_FREQS} harmonics. 
	 */
	private JPanel createFreqPanel(){

		Dimension addSize = new Dimension(10, 170);
		
		final JPanel freqPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));

		freqPanel.add(createScalePanel(addSize.height));

		for(int i = 0; i < levelSelector.length; i++){
			
			addSize = new Dimension(getFreqWidth(i),170);
			levelSelector[i] = new LevelSelector();
			levelSelector[i].setPreferredSize(addSize);

			freqPanel.add(levelSelector[i]);
		}
		this.add(freqPanel);
		return freqPanel;

	}

	/**
	 * Creates a panel with one {@link FreqLevelEditor} for each 
	 * of the {@link Constants#NUM_ADDITIVE_FREQS} harmonics. 
	 */
	private JPanel editButtonPanel(){

		Dimension editorButtonSize = new Dimension(12, 14);
		final JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));

		final JLabel label = new JLabel("edit");
		label.setForeground(Color.darkGray);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createLineBorder(Color.gray));

		label.setPreferredSize(new Dimension(50, editorButtonSize.height));
		editButtonPanel.add(label);

		setMetalLookAndFeel();
		for(int i = 0; i < levelSelector.length; i++){
			editorButtonSize = new Dimension(getFreqWidth(i),14);
			levelEditors[i] = new FreqLevelEditor(levelSelector[i]);
			levelEditors[i].setPreferredSize(editorButtonSize);
			editButtonPanel.add(levelEditors[i]);
		}
		setNimbusLookAndFeel();

		return editButtonPanel;
	}

	/**
	 * Creates the panel which is placed on the left side before the additive
	 * frequency selectors, it containst the scale for the FreqLevelPanel and
	 * the buttons copy, paste and reset
	 * 
	 */
	private JPanel createScalePanel(final int height){
		final JPanel scalePanel = new JPanel();

		final Dimension scalePanelSize = new Dimension(50, height);
		scalePanel.setPreferredSize(scalePanelSize);
		scalePanel.setSize(scalePanelSize);

		scalePanel.setLayout(null);
		final JLabel l1 = new JLabel("+1");
		l1.setBounds(0, 0, scalePanelSize.width - 5, 9);
		l1.setHorizontalAlignment(SwingConstants.RIGHT);
		scalePanel.add(l1);

		final JLabel l2 = new JLabel("0");
		l2.setBounds(0, scalePanelSize.height / 2 - 5, scalePanelSize.width - 5, 10);
		l2.setHorizontalAlignment(SwingConstants.RIGHT);
		scalePanel.add(l2);

		final JLabel l3 = new JLabel("-1");
		l3.setBounds(0, scalePanelSize.height - 11, scalePanelSize.width - 5, 10);
		l3.setHorizontalAlignment(SwingConstants.RIGHT);
		scalePanel.add(l3);

		copyButton = new JButton();
		copyButton.setBounds(0, scalePanelSize.height / 2 - 48, 32, 32);
		copyButton.setName("copy");
		copyButton.setMargin(new Insets(0, 0, 0, 0));
		copyButton.setFocusable(false);
		copyButton.setToolTipText("Copy");
		scalePanel.add(copyButton);

		pasteButton = new JButton();
		pasteButton.setBounds(0, scalePanelSize.height / 2 - 16, 32, 32);
		pasteButton.setName("paste");
		pasteButton.setMargin(new Insets(0, 0, 0, 0));
		pasteButton.setFocusable(false);
		pasteButton.setToolTipText("Paste");
		scalePanel.add(pasteButton);

		resetButton = new JButton();
		resetButton.setBounds(0, scalePanelSize.height / 2 + 16, 32, 32);
		resetButton.setName("reset");
		resetButton.setMargin(new Insets(0, 0, 0, 0));
		resetButton.setFocusable(false);
		resetButton.setToolTipText("Reset");
		scalePanel.add(resetButton);
		resetButton.setFocusable(false);

		try{
			final URL copyImageUrl = this.getClass().getResource(Constants.COPY_IMAGE);
			final URL pasteImageUrl = this.getClass().getResource(Constants.PASTE_IMAGE);
			final URL resetImageUrl = this.getClass().getResource(Constants.RESET_IMAGE);

			copyButton.setIcon(new ImageIcon(copyImageUrl));
			pasteButton.setIcon(new ImageIcon(pasteImageUrl));
			resetButton.setIcon(new ImageIcon(resetImageUrl));
		}catch(final Exception e){
			log.error("Loading image failed", e);
		}

		return scalePanel;
	}

	
	/**
	 * Returns the levels of the harmonics, that the user entered. 
	 * @return An array with the size {@link Constants#NUM_ADDITIVE_FREQS}. 
	 * Each value are between -1 and 1.
	 */
	public float[] getAdditiveFreqLevels(){
		final float[] freqLevels = new float[levelSelector.length];

		for(int i = 0; i < levelSelector.length; i++){
			freqLevels[i] = levelSelector[i].getValue();
		}

		return freqLevels;
	}

	/**
	 * Adds a controller to the copy,paste and reset buttons
	 */
	public void addController(final ActionListener controller){
		for(final LevelSelector l: levelSelector){
			l.addActionListener(controller);
		}
		copyButton.addActionListener(controller);
		pasteButton.addActionListener(controller);
		resetButton.addActionListener(controller);
	}

	@Override
	public void update(final Observable o, final Object arg){

		for(int i = 0; i < levelSelector.length; i++){
			levelSelector[i].setValue(model.getAdditiveFreqLevels()[i]);
		}

	}

	/**
	 * Activates the Nimbus look and feel
	 */
	private void setNimbusLookAndFeel(){
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}catch(final Exception e){
			log.error("Failed to load the look and feel", e);
		}
	}

	/**
	 * Activates the Metal look and feel
	 */
	private void setMetalLookAndFeel(){
		try{
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

		}catch(final Exception e){
			log.error("Failed to load the look and feel", e);
		}

	}
	
	/**
	 * Calculates the width of the components, related to 
	 * a specific harmonic. This is used to reduce the width of
	 * the view of higher harmonics.
	 * @param freqIndex The number of the harmonic
	 * @return The width 
	 */
	private int getFreqWidth(final int freqIndex){
		if(freqIndex<16){
			return 14;
		}else if(freqIndex<32){
			return 12;
		}else if(freqIndex<48){
			return 11;
		}else if(freqIndex<64){
			return 10;
		}
		return 14;
	}

}
