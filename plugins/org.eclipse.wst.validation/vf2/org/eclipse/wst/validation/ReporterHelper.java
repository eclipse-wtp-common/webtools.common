package org.eclipse.wst.validation;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.internal.ConfigurationConstants;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;


/**
 * This is a temporary class to ease the transition from the previous validation framework.
 * <p>
 * This is not API. 
 * @author karasiuk
 *
 */
public class ReporterHelper implements IReporter {
	private IProgressMonitor 	_monitor;
	private List<IMessage>		_list = new LinkedList<IMessage>();
	
	public ReporterHelper(IProgressMonitor monitor){
		_monitor = monitor;
	}

	public void addMessage(IValidator origin, IMessage message) {
		_list.add(message);
	}

	public void displaySubtask(IValidator validator, IMessage message) {
		_monitor.subTask(message.getText(validator.getClass().getClassLoader()));
	}

	public List<IMessage> getMessages() {
		return _list;
	}

	public boolean isCancelled() {
		return _monitor.isCanceled();
	}

	public void removeAllMessages(IValidator origin) {
		_list.clear();
	}

	public void removeAllMessages(IValidator origin, Object object) {
		_list.clear();
	}

	public void removeMessageSubset(IValidator validator, Object obj, String groupName) {
		_list.clear();
	}
	
	public void makeMarkers(){
		for (IMessage message : _list){
			Object target = message.getTargetObject();
			if (target != null){
				if (target instanceof IResource){
					IResource res = (IResource)target;
					try {
						IMarker marker = res.createMarker(ConfigurationConstants.VALIDATION_MARKER);
						marker.setAttribute(IMarker.MESSAGE, message.getText());
						int markerSeverity = IMarker.SEVERITY_INFO;
						int sev = message.getSeverity();
						if ((sev & IMessage.HIGH_SEVERITY) != 0)markerSeverity = IMarker.SEVERITY_ERROR;
						else if ((sev & IMessage.NORMAL_SEVERITY) != 0)markerSeverity = IMarker.SEVERITY_WARNING;
						marker.setAttribute(IMarker.SEVERITY, markerSeverity);
						marker.setAttribute(IMarker.LINE_NUMBER, message.getLineNumber());
					}
					catch (CoreException e){
						ValidationPlugin.getPlugin().handleException(e);
					}
				}
			}
		}
	}
	
	/**
	 * Copy the messages from the reporter into validation result.
	 */
	public void updateResult(ValidationResult result){
		for (IMessage message : _list){
			Object target = message.getTargetObject();
			if (target != null){
				if (target instanceof IResource){
					IResource res = (IResource)target;
					ValidatorMessage vm = ValidatorMessage.create(message.getText(), res);
					result.add(vm);
					int markerSeverity = IMarker.SEVERITY_INFO;
					int sev = message.getSeverity();
					if ((sev & IMessage.HIGH_SEVERITY) != 0)markerSeverity = IMarker.SEVERITY_ERROR;
					else if ((sev & IMessage.NORMAL_SEVERITY) != 0)markerSeverity = IMarker.SEVERITY_WARNING;
					vm.setAttribute(IMarker.SEVERITY, markerSeverity);
					vm.setAttribute(IMarker.LINE_NUMBER, message.getLineNumber());
					int offset = message.getOffset();
					if (offset != IMessage.OFFSET_UNSET){
						vm.setAttribute(IMarker.CHAR_START, offset);
						int len = message.getLength();
						if (len != IMessage.OFFSET_UNSET){
							vm.setAttribute(IMarker.CHAR_START, offset);
							vm.setAttribute(IMarker.CHAR_END, offset+len);
						}
					}
					
				}
			}
		}
		
	}

}
