package org.eclipse.wst.validation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.wst.validation.internal.ValMessages;

/**
 * Associate a message severity with a message category.
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
	 * @param id an id that is used to identify this particular setting. This must be unique within the
	 * scope of this particular validator.
	 * 
	 * @param label an end user string,
	 * that can describe either an individual message or a message category.
	 * 
	 * @param defaultSeverity the default severity for this label. If the user doesn't change anything
	 * this is what the severity will be.
	 */
	public MessageSeveritySetting(String id, String label, Severity defaultSeverity){
		_id = id;
		_label = label;
		_default = defaultSeverity;
	}

	public String getLabel() {
		return _label;
	}

	public Severity getDefault() {
		return _default;
	}

	public Severity getCurrent() {
		if (_current != null)return _current;
		return _default;
	}

	public String getId() {
		return _id;
	}

	public void setCurrent(Severity severity) {
		_current = severity;
	}
	
	@Override
	public String toString() {
		return _id + " " + getCurrent().toString(); //$NON-NLS-1$
	}

	public MessageSeveritySetting copy() {
		MessageSeveritySetting ms = new MessageSeveritySetting(_id, _label, _default);
		ms._current = _current;
		return ms;
	}

}
