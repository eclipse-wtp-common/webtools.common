/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.environment;

/**
 * 
 * This class is used by the IStatusHandler interface.
 * It allows code that is reporting status to specify choices to the user
 * which this reporting code can react to. 
 *
 * @since 1.0
 */
public class Choice {

  private char   shortcut = ' '; 
  private String label = null;
  private String description = null;
  	
	/**
	 * Constructor for Choice.
	 */
	public Choice() {
	}

	/**
	 * Constructor for Choice.
	 * @param shortcut the single letter shortcut for this choice.
	 * @param label the label to be displayed to the user for this choice.
	 */
	public Choice(char shortcut, String label) {
		this.shortcut = shortcut;
		this.label = label;
	}
	
	/**
	 * Constructor for Choice.
   * @param shortcut the single letter shortcut for this choice.
   * @param label the label to be displayed to the user for this choice.
	 * @param description the description for this choice.
	 */
	public Choice(char shortcut, String label, String description) {
		this.shortcut = shortcut;
		this.label = label;
		this.description = description;
	}

	/**
	 * Gets the label.
	 * @return Returns a String
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 * @param label The label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the description.
	 * @return Returns a String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the shortcut.
	 * @return Returns a char
	 */
	public char getShortcut() {
		return shortcut;
	}

	/**
	 * Sets the shortcut.
	 * @param shortcut The shortcut to set
	 */
	public void setShortcut(char shortcut) {
		this.shortcut = shortcut;
	}

}
