/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $RCSfile: ILogRenderer2.java,v $
 *  $Revision: 1.4 $  $Date: 2005/08/24 21:10:34 $ 
 */
package org.eclipse.jem.util.logger.proxy;

import java.util.logging.Level;
 
/**
 * Log renderer that provides more function. Basically it can handle
 * the logging of specific types in a different manner than the default
 * conversion to string supplied by Logger.
 * 
 * It also uses the Level classes from java.util.logging as the logging levels.
 * 
 * @since 1.0.0
 */
public interface ILogRenderer2 extends ILogRenderer {
	
	/**
	 * When Logger.setLevel(DEFAULT): restore to what the default level was.
	 * When log(...,DEFAULT): Log at the default level for the type of object.
	 */
	static final Level DEFAULT = new Logger.LocalLevel("DEFAULT", Integer.MAX_VALUE-1); //$NON-NLS-1$
	
	/**
	 * When log(...,TRACE) : Log only when in trace mode.
	 * Don't use in Logger.setLevel(). Has no meaning in that case.
	 */
	static final Level TRACE = new Logger.LocalLevel("TRACE", Integer.MAX_VALUE-2);	 //$NON-NLS-1$

	/**
	 * Log the throwable at the given level (if DEFAULT, use default level for a throwable).
	 * 
	 * @param t
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(Throwable t, Level level);
	
	/**
	 * Log the object at the given level (if DEFAULT, use default level for an object).
	 * 
	 * @param o
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(Object o, Level level);
	
	/**
	 * Log the boolean at the given level (if DEFAULT, use default level for a boolean).
	 * 
	 * @param b
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(boolean b, Level level);	

	/**
	 * Log the char at the given level (if DEFAULT, use default level for a char).
	 * 
	 * @param c
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(char c, Level level);	
	
	/**
	 * Log the byte at the given level (if DEFAULT, use default level for a byte).
	 * 
	 * @param b
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(byte b, Level level);
	
	/**
	 * Log the short at the given level (if DEFAULT, use default level for a short).
	 * 
	 * @param s
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(short s, Level level);
	
	/**
	 * Log the int at the given level (if DEFAULT, use default level for an int).
	 * 
	 * @param i
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(int i, Level level);

	/**
	 * Log the long at the given level (if DEFAULT, use default level for a long).
	 * 
	 * @param l
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(long l, Level level);

	/**
	 * Log the float at the given level (if DEFAULT, use default level for a float).
	 * 
	 * @param f
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(float f, Level level);

	/**
	 * Log the double at the given level (if DEFAULT, use default level for a double).
	 * 
	 * @param d
	 * @param level
	 * @return
	 * 
	 * @since 1.0.0
	 */
	String log(double d, Level level);
}
