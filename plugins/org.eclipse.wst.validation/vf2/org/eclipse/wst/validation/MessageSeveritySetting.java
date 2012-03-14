/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.wst.validation.internal.ValMessages;

/**
 * Associate a message severity with a message category.
 * @noextend
 * @author karasiuk
 *
 */
public class MessageSeveritySetting {
	public enum Severity {
		Error(ValMessages.SevError, IMarker.SEVERITY_ERROR),
		Warning(ValMessages.SevWarning, IMarker.SEVERITY_WARNING),
		Ignore(ValMessages.SevIgnore, IMarker.SEVERITY_INFO);
		
		private String _severityLabel;
		private Severity(String label, int markerSeverity){
			_severityLabel = label;
			_markerSeverity = markerSeverity;
		}
		private int		_markerSeverity;
		
		@Override
		public String toString() {
			return _severityLabel;
		}

		/**
		 * Answer the associated IMarker severity.
		 */
		public int getMarkerSeverity() {
			return _markerSeverity;
		}
		
	}
	
	private String		_id;
	private String 		_label;
	private Severity 	_default;
	private Severity 	_current;
	
	/**
	 * Create an association between a label and a message severity.
	 * 
	 * @param id
	 * 		An id that is used to identify this particular setting. This must be
	 * 		unique within the scope of this particular validator.
	 * 
	 * @param label
	 * 		An end user string, that can describe either an individual message
	 * 		or a message category.
	 * 
	 * @param defaultSeverity
	 * 		The default severity for this label. If the end user doesn't change
	 * 		anything this is what the severity will be.
	 */
	public MessageSeveritySetting(String id, String label, Severity defaultSeverity){
		_id = id;
		_label = label;
		_default = defaultSeverity;
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		if (_current != null)h += _current.getMarkerSeverity()*1000;
		if (_id != null)h += _id.hashCode();
		return h;
	}

	public String getLabel() {
		return _label;
	}

	/** Answer the default severity as defined by the validator owner. */
	public Severity getDefault() {
		return _default;
	}

	/** 
	 * Answer the current severity for the message. The end user may have changed this through a
	 * preference dialog.
	 */
	public Severity getCurrent() {
		if (_current != null)return _current;
		return _default;
	}

	/**
	 * The id that the validator owner assigned to the message.
	 */
	public String getId() {
		return _id;
	}

	/**
	 * Set the current message severity.
	 * 
	 * @param severity
	 * @return true if the setting changed
	 */
	public boolean setCurrent(Severity severity) {
		if (_current == severity)return false;
		_current = severity;
		return true;
	}
	
	@Override
	public String toString() {
		return _id + " " + getCurrent().toString(); //$NON-NLS-1$
	}

	/**
	 * Answer a copy of this object.
	 */
	public MessageSeveritySetting copy() {
		MessageSeveritySetting ms = new MessageSeveritySetting(_id, _label, _default);
		ms._current = _current;
		return ms;
	}

}
