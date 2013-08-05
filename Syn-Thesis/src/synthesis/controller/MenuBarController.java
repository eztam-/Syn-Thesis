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
package synthesis.controller;


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import synthesis.gui.MenuBar;
import synthesis.gui.SaveAsWindow;
import synthesis.logic.Preset;
import synthesis.logic.Synthesizer;
import synthesis.util.LoggerFactory;

/**
 * This is the controller(in the context of the MVC pattern) of the
 * {@link MenuBar} which contains the preset management components and also some
 * other buttons.
 * 
 * @author Matthias Birschl
 * 
 */
public class MenuBarController implements ActionListener, PopupMenuListener {

	private final Synthesizer model;
	private final MenuBar view;
	private Popup popup;
	private SaveAsWindow saveAsWin;

	private boolean isPopupVisible = false;

	public MenuBarController(final Synthesizer model, final MenuBar view){
		this.model = model;
		this.view = view;

	}

	@Override
	public void actionPerformed(final ActionEvent e){

		if(e.getSource() instanceof JButton){
			final JButton button = (JButton)e.getSource();

			if(button.getName() == "log_button"){
				LoggerFactory.showLogViewer();

			}else if(button.getName() == "save"){
				model.savePreset(view.getSelectedPreset());

			}else if(button.getName() == "saveas"){
				processSaveAs();

			}else if(button.getName() == "delete"){
				model.deletePreset(view.getSelectedPreset());
				view.clearSelection();
			}else if(button.getName() == "dialog_cancel"){
				popup.hide();
				isPopupVisible = false;
			}else if(button.getName() == "dialog_save"){
				model.savePresetAs(saveAsWin.getText());
				popup.hide();
				isPopupVisible = false;
			}else if(button.getName() == "info"){
				view.showInfoFrameVisible();
			}
//			else if(button.getName() == "send_presets"){
//				
//			}

		}

	}

	/**
	 * Closes the save as dialog when it was open, otherwise shows the dialog.
	 */
	private void processSaveAs(){

		if(isPopupVisible){
			popup.hide();
			isPopupVisible = false;
		}else{
			isPopupVisible = true;
			saveAsWin = new SaveAsWindow(this);
			final PopupFactory factory = PopupFactory.getSharedInstance();

			final Point location = view.getLocationOnScreen();

			popup = factory.getPopup(view.getRootPane(), saveAsWin
					, location.x + 293, location.y + view.getHeight());
			popup.show();
		}

	}

	@Override
	public void popupMenuCanceled(final PopupMenuEvent e){

	}

	@Override
	public void popupMenuWillBecomeInvisible(final PopupMenuEvent e){
		final JComboBox comboBox = (JComboBox)e.getSource();
		if(comboBox.getSelectedItem() instanceof Preset){
			model.setActivePreset((Preset)comboBox.getSelectedItem());
		}

	}

	@Override
	public void popupMenuWillBecomeVisible(final PopupMenuEvent e){
		
		view.update(model, "");
		
		
	}


}
