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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.LF5Appender;


/**
 * This class manages the logging of this plugin
 * @author Matthias Birschl
 *
 */
public class LoggerFactory {

	private static LF5Appender lf5 = new LF5Appender();
	private static boolean isLogViewerVisible = false;
	private static Logger rootLogger;

	static{
		hideLogViewer();
		rootLogger = Logger.getRootLogger();
		rootLogger.removeAllAppenders();
		rootLogger.addAppender(lf5);
		hideLogViewer();
		Logger.getLogger("org.hibernate").setLevel(Level.WARN);
	}

	/**
	 * Creates a new logger for the given class
	 */
	public static Logger getLogger(final Class<?> clazz){
		final Logger log = Logger.getLogger(clazz);

		if(!isLogViewerVisible){
			hideLogViewer();
		}

		return log;
	}

	
	/**
	 * Hides the UI of the log viewer
	 */
	public static void hideLogViewer(){

		lf5.getLogBrokerMonitor().hide();
		isLogViewerVisible = false;

	}

	/**
	 * Shows the UI of the log viewer
	 */
	public static void showLogViewer(){

		lf5.getLogBrokerMonitor().show();
		isLogViewerVisible = true;
	}

}
