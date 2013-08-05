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
package synthesis.util;



/**
 * This class contains some constants, which define some
 * fundamental properties of this plugin
 * @author Matthias Birschl
 *
 */
public class Constants {

	public static String PRODUCT_NAME = "Syn-Thesis";
	public static String VENDOR_NAME = "Matthias Birschl";
	public static String VERSION = "0.51 beta";

	
	/**
	 * The maximum attack time in ms
	 */
	public static int MAX_ATTACK_TIME = 500;
	
	/**
	 * The maximum decay time in ms
	 */
	public static int MAX_DECAY_TIME = 500;
	
	/**
	 * The maximum release time in ms
	 */
	public static int MAX_RELEASE_TIME = 500;

	/**
	 * The number of harmonics
	 */
	public static int NUM_ADDITIVE_FREQS = 64;

	/**
	 * Number of parameters of one oscillator which are accessible for the VST
	 * host
	 */
	public static int NUM_AUTO_OSC_PARAMS = 6;

	/**
	 * The number of oscillators
	 */
	public static int NUM_OSCILLATORS = 4;

	public static String DEFAULT_KNOB_IMAGE = "/synthesis/resources/knob.png";
	public static String ADR_KNOB_IMAGE = "/synthesis/resources/adr_knob.png";
	public static String OCTAVE_KNOB_IMAGE = "/synthesis/resources/octave_knob.png";
	public static String SUSTAIN_KNOB_IMAGE = "/synthesis/resources/sustain_knob.png";

	
	public static String UPLOAD_IMAGE = "/synthesis/resources/send.png";
	public static String SAVE_IMAGE = "/synthesis/resources/save.png";
	public static String SAVE_AS_IMAGE = "/synthesis/resources/save_as.png";
	public static String DELETE_IMAGE = "/synthesis/resources/delete.png";
	public static String LOG_IMAGE = "/synthesis/resources/log.png";
	public static String INFO_IMAGE = "/synthesis/resources/info.png";
	public static String HELP_IMAGE = "/synthesis/resources/help.png";
	public static String CANCEL_IMAGE_SMALL = "/synthesis/resources/cancel_16.png";
	public static String SAVE_IMAGE_SMALL = "/synthesis/resources/save_16.png";

	public static String COPY_IMAGE = "/synthesis/resources/copy_24.png";
	public static String PASTE_IMAGE = "/synthesis/resources/paste_24.png";
	public static String RESET_IMAGE = "/synthesis/resources/reset_24.png";
	
	
	
	
	
	public static String INFO_TEXT = "<html><h3>Syn-Thesis " +VERSION+
			"</h3><br>"+
			"Please send your presets to <a href='mailto:m-birschl@gmx.de'>m-birschl@gmx.de</a>. I'll put your presets into the next release.<br>"+
			"Just send me an email with the file h2db/presets.h2.db from the plugin directory. Thanks!<br>"
			+
			"<br>The copy and paste icons are from<br>http://www.visualpharm.com\n</html>";
	


}
