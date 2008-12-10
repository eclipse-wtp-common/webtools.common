package org.eclipse.wst.validation.internal;

public class ValidationResultSummary {
	
	private int _error;
	private int _warning;
	private int	_info;
	
	public ValidationResultSummary(int error, int warning, int info){
		_error = error;
		_warning = warning;
		_info = info;
	}

	public int getSeverityError() {
		return _error;
	}

	public int getSeverityWarning() {
		return _warning;
	}

	public int getSeverityInfo() {
		return _info;
	}

}
