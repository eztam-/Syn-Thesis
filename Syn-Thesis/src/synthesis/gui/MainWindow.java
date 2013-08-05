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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jvst.wrapper.VSTPluginAdapter;
import jvst.wrapper.VSTPluginGUIAdapter;
import jvst.wrapper.gui.VSTPluginGUIRunner;

import org.apache.log4j.Logger;

import synthesis.controller.OscPanelController;
import synthesis.controller.OscTabController;
import synthesis.logic.Oscillator;
import synthesis.logic.Synthesizer;
import synthesis.logic.VST_Adapter;
import synthesis.util.Constants;
import synthesis.util.LoggerFactory;

/**
 * This is the main widow of this plugin, which contains all the
 * components to view and adjust the model.
 * Objects of this class were created by the jVSTwRapper.
 * For testing purpose it is also possible to start this class 
 * outside of any VST host, by using its main method.
 * 
 * @author Matthias Birschl
 *
 */
public class MainWindow extends VSTPluginGUIAdapter {

	private static final long serialVersionUID = 6826548882238800915L;

	private static Logger log = LoggerFactory.getLogger(MainWindow.class);

	private Synthesizer synth;

	private JTabbedPane tabbedPane;

	/**
	 * Just for testing purpose
	 */
	public static void main(final String[] args) throws Exception{

		final MainWindow gui = new MainWindow(null, null);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * This constructor is called by the jVSTwRapper to load the GUI
	 * @see VSTPluginGUIAdapter#VSTPluginGUIAdapter(VSTPluginGUIRunner, VSTPluginAdapter)
	 */
	public MainWindow(final VSTPluginGUIRunner runner, final VSTPluginAdapter plugin){
		super(runner, plugin);

		configureLookAndFeel();

		if(plugin != null){
			final VST_Adapter vstAdapter = (VST_Adapter)plugin;
			this.synth = vstAdapter.getSynthesizer();
		}else{
			// This is just for testing purpose, if this plugin 
			// is running outside of a VST host
			this.synth = new Synthesizer();
		}
		this.setResizable(false);
		this.setTitle(Constants.PRODUCT_NAME);
		final Dimension windowSize = new Dimension(1120, 545);
		this.setPreferredSize(windowSize);
		setSize(windowSize);

		init();
	}

	/**
	 * Sets the look and feel of the GUI and do 
	 * some modifications on the look and feel
	 */
	private void configureLookAndFeel(){

		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}catch(final Exception e){
			log.error("Failed to load the look and feel", e);
		}

//		Object o = UIManager.get("TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter");
//		UIManager.put("TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", o);
		UIManager.put("TabbedPane.isTabRollover", false);
		
		
		// UIManager.put("background", Color.black);
		// UIManager.put("nimbusLightBackground", Color.black);
		// UIManager.put("control", Color.white);
		// UIManager.put("controlShadow", Color.white);
		// UIManager.put("TitledBorder.titleColor", Color.white);
		// UIManager.put("controlDkShadow", Color.white);

	}

	/**
	 * Creates the main tabbed pane with all child components 
	 * and does some configuration related to this window
	 */
	private void init(){

		this.setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		final Dimension tabbedPaneSize = new Dimension(1000, 370);
		tabbedPane.setPreferredSize(tabbedPaneSize);
		tabbedPane.setSize(tabbedPaneSize);
		tabbedPane.setFocusable(false);

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e){
				getRootPane().requestFocus();
			}
		});



		final OscTabController oscTabController = new OscTabController(tabbedPane, synth);

		for(int i = 0; i < 4; i++){

			final Oscillator oscillator = synth.getOscillator(i);

			final OscillatorPanel oscPanel = new OscillatorPanel(oscillator);
			final OscPanelController oscController = new OscPanelController(oscillator, oscPanel);
			oscPanel.addController(oscController);
			tabbedPane.addTab("", oscPanel);

			// Create the tab content
			final OscillatorTab oscTab = new OscillatorTab("Oscillator " + (i + 1), oscillator);
			oscTab.addActionListener(oscTabController);
			tabbedPane.setTabComponentAt(i, oscTab);

		}

		add(tabbedPane, BorderLayout.CENTER);

		final MenuBar menu = new MenuBar(synth);
		add(menu, BorderLayout.PAGE_START);

		setVisible(true);

	}

}
