package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * This class extends the workbench reporter by capturing some summary information about any added
 * messages.
 * @author karasiuk
 *
 */
public class SummaryReporter extends WorkbenchReporter {
	
	private int _severityHigh;
	private int _severityNormal;
	private int	_severityLow;

	public SummaryReporter(IProject project, IProgressMonitor monitor) {
		super(project, monitor);
	}
	
	@Override
	public void addMessage(IValidator validator, IMessage message) {
		super.addMessage(validator, message);
		switch (message.getSeverity()){
		case IMessage.HIGH_SEVERITY: 
			_severityHigh++;
			break;
		case IMessage.NORMAL_SEVERITY: 
			_severityNormal++;
			break;
		case IMessage.LOW_SEVERITY:
			_severityLow++;
			break;
		}
	}

	public int getSeverityHigh() {
		return _severityHigh;
	}

	public int getSeverityNormal() {
		return _severityNormal;
	}

	public int getSeverityLow() {
		return _severityLow;
	}

}
