package org.eclipse.wst.validation.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.tests.validation.Activator;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;

/**
 * Simple test validator. 
 * <p>
 * This validator processes a file looking for certain magic tokens:
 * <ul>
 * <li>include fileName - open the file with name fileName and process it as an include
 * <li>error - create an error marker for this line
 * <li>warning - create a warning marker for this line
 * <li>info - create an info marker for this line
 * </ul>
 * @author karasiuk
 *
 */
public class TestValidator extends AbstractValidator {
	
	public static String id(){
		return Activator.PLUGIN_ID +".Test1";
	}
	
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
		ValidationResult vr = new ValidationResult();
		InputStream in = null;
		try {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				in = file.getContents();
				BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
				process(vr, rdr, resource, state);
			}
		}
		catch (Exception e){
			Activator.getDefault().handleException(e);
		}
		finally {
			Misc.close(in);
		}
				
		return vr;
	}
	
	public String getId(){
		return id();
	}
	
	private void process(ValidationResult vr, BufferedReader rdr, IResource resource, ValidationState state) throws IOException, CoreException {
		String line = null;
		int count = 0;
		Map<String, String> map = null;
		while (null != (line = rdr.readLine())){
			count++;
			int severity = -1;
			String[] tokens = line.split("\\s");
			String token = tokens[0];
			severity = findSeverity(token, severity);
			if (severity == -1 && map != null){
				String renamed = map.get(token);
				severity = findSeverity(renamed, severity);
			}
			
			if (severity != -1){
				ValidatorMessage message = ValidatorMessage.create(line, resource);
				message.setAttribute(IMarker.LINE_NUMBER, count);
				message.setAttribute(IMarker.SEVERITY, severity);
				vr.add(message);
			}
			
			if (token.equals("include")){
				map = new HashMap<String, String>(10);
				String msg = processInclude(vr, tokens, resource, map);
				if (msg != null){
					ValidatorMessage message = ValidatorMessage.create(msg, resource);
					message.setAttribute(IMarker.LINE_NUMBER, count);
					message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					vr.add(message);
				}
			}
		}
		
	}
	
	private int findSeverity(String token, int dft){
		if (token == null)return dft;
		if (token.equals("error"))return IMarker.SEVERITY_ERROR;
		if (token.equals("warning"))return IMarker.SEVERITY_WARNING;
		if (token.equals("info"))return IMarker.SEVERITY_INFO;
		return dft;
	}

	private String processInclude(ValidationResult vr, String[] tokens, IResource resource, Map<String, String> map) throws IOException, CoreException{
		IContainer parent = resource.getParent();
		IFile file = parent.getFile(new Path(tokens[1]));
		if (!file.exists())return "Could not find file: " + file;
		
		InputStream in = null;
		try {
			in = file.getContents();
			BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while(null != (line = rdr.readLine())){
				String[] p = line.split("\\s");
				if (p.length == 3 && "map".equals(p[0])){
					map.put(p[1], p[2]);
				}
			}
		}
		finally {
			Misc.close(in);
		}
		IResource[] depends = {file};
		vr.setDependsOn(depends);
		return null;
		
	}

	public String getName(){
		return "TestValidator1";
	}
	
}
