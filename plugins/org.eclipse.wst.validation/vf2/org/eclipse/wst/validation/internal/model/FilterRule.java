/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.model;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.validation.internal.ContentTypeWrapper;
import org.eclipse.wst.validation.internal.Deserializer;
import org.eclipse.wst.validation.internal.ExtensionConstants;
import org.eclipse.wst.validation.internal.Serializer;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.ValMessages;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * A rule that is used to filter out (or in) validation on a resource.
 * The concrete classes are all immutable.
 * @author karasiuk
 *
 */
public abstract class FilterRule implements IAdaptable {
	
	protected final String _pattern;
	
	protected static final String PortableFileDelim = "/"; //$NON-NLS-1$
	
	/**
	 * Create a rule based on a configuration element.
	 * 
	 * @param rule
	 *            The configuration element, see the extension point for details.
	 * @return null if there is a problem.
	 */
	public static FilterRule create(IConfigurationElement rule){
		String name = rule.getName();
		if (ExtensionConstants.Rule.fileext.equals(name))return new FileExt(rule);
		if (ExtensionConstants.Rule.projectNature.equals(name))return new ProjectNature(rule);
		if (ExtensionConstants.Rule.file.equals(name))return File.createFile(rule);
		if (ExtensionConstants.Rule.contentType.equals(name))return ContentType.createContentType(rule);
		if (ExtensionConstants.Rule.facet.equals(name))return new Facet(rule);
		if (ExtensionConstants.Rule.pattern.equals(name))return FilePattern.createFilePattern(rule);
		if (ExtensionConstants.Rule.targetRuntime.equals(name))return new TargetRuntime(rule);
		return null;
	}
	
	static FilterRule create(Deserializer des) {
		String type = des.getString();
		if (ExtensionConstants.Rule.fileext.equals(type)){
			String pattern = des.getString();
			boolean caseSensitive = des.getBoolean();
			return new FileExt(pattern, caseSensitive);
		}
		
		if (ExtensionConstants.Rule.projectNature.equals(type)){
			String pattern = des.getString();
			return new ProjectNature(pattern);
		}
		
		if (ExtensionConstants.Rule.file.equals(type)){
			String pattern = des.getString();
			boolean caseSensitive = des.getBoolean();
			int fileType = des.getInt();
			return new File(pattern, caseSensitive, fileType);
		}
		
		if (ExtensionConstants.Rule.contentType.equals(type)){
			String pattern = des.getString();
			boolean exactMatch = des.getBoolean();
			return new ContentType(pattern, exactMatch);
		}
		
		if (ExtensionConstants.Rule.facet.equals(type)){
			String pattern = des.getString();
			return new Facet(pattern, null);
		}
		
		if (ExtensionConstants.Rule.targetRuntime.equals(type)){
			String pattern = des.getString();
			return new TargetRuntime(pattern);
		}
		
		if (ExtensionConstants.Rule.pattern.equals(type)){
			String pattern = des.getString();
			boolean caseSensitive = des.getBoolean();
			return new FilePattern(pattern, caseSensitive);
		}
		return null;
	}

	
	public static FilterRule createFile(String pattern, boolean caseSensitive, int type){
		return new File(pattern, caseSensitive, type);
	}
	
	public static FilterRule createFileExt(String pattern, boolean caseSensitive){
		FileExt ext = new FileExt(pattern, caseSensitive);
		return ext;
	}
	
	public static FilterRule createFacet(String facetId){
		return new Facet(facetId, null);
	}
	
	public static FilterRule createProject(String projectNature){
		return new ProjectNature(projectNature);
	}
	
	public static FilterRule createContentType(String contentType, boolean exactMatch){
		return new ContentType(contentType, exactMatch);
	}
	
	public static FilterRule createTargetRuntime(String targetRuntime){
		return new TargetRuntime(targetRuntime);
	}
		
	protected FilterRule(String pattern){
		_pattern = pattern;
	}

	/** 
	 * Answer true if the rule matches the resource, false if it doesn't, and
	 * null if the rule does not apply to resources.
	 * 
	 * 	@param resource the resource that is being validated.
	 */
	public Boolean matchesResource(IResource resource, ContentTypeWrapper contentTypeWrapper){
		return null;
	}

	/** 
	 * Answer true if the rule matches the project, false if it doesn't, and null if the
	 * rule does not apply.
	 * 
	 * 	@param project the project that is being validated.
	 */
	public Boolean matchesProject(IProject project){
		return null;
	}
		
	public String toString() {
		return getDisplayableType() + ": " + _pattern; //$NON-NLS-1$
	}
	
	/** Answer a name of the rule, that can be displayed to a user. */
	public String getName(){
		return toString();
	}
	
	public String getPattern(){
		return _pattern;
	}
	
	/** Answer the type of rule. */
	public abstract String getType();
	
	/** Answer a type that can be displayed to an end user. */
	public abstract String getDisplayableType();
	
	public static boolean asBoolean(String value, boolean aDefault){
		if (value == null)return aDefault;
		if (value.equals(ExtensionConstants.True))return true;
		if (value.equals(ExtensionConstants.False))return false;
		return aDefault;
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	
	public static abstract class FilterRuleCaseSensitive extends FilterRule {
				
		public FilterRuleCaseSensitive(String pattern, boolean caseSensitive) {
			super(pattern);
			_caseSensitive = caseSensitive;
		}
		
		private final boolean _caseSensitive;
				
		@Override
		public int hashCodeForConfig() {
			int h =  super.hashCodeForConfig();
			if (_caseSensitive)h += 401;
			return h;
		}
				
		@Override
		public void save(Serializer ser) {
			super.save(ser);
			ser.put(_caseSensitive);
		}
						
		public boolean isCaseSensitive() {
			return _caseSensitive;
		}
				
	}
	
	public static final class ProjectNature extends FilterRule {
		
		private String patternLabel = null;
		
		private ProjectNature(IConfigurationElement rule) {
			super(rule.getAttribute(ExtensionConstants.RuleAttrib.id));
			
		}
		
		public ProjectNature(String projectNature) {
			super(projectNature);
			
			IProjectNatureDescriptor nature = ResourcesPlugin.getWorkspace().getNatureDescriptor(projectNature);
			
			if(nature != null){
				patternLabel = nature.getLabel();
			}
		}

		public String getDisplayableType() {
			return ValMessages.RuleProjectNature;
		}
		
		public String getType() {
			return ExtensionConstants.Rule.projectNature;
		}
		
		public Boolean matchesProject(IProject project) {
			try {
				return project.hasNature(_pattern);
			}
			catch (CoreException e){
			}
			return Boolean.FALSE;
		}
		
		public String toString()
		{
			if(patternLabel != null && patternLabel.length() > 0){
				return getDisplayableType() + ": " + patternLabel.concat(" - ").concat(_pattern); //$NON-NLS-1$ //$NON-NLS-2$
			}

			return getDisplayableType() + ": " + _pattern; //$NON-NLS-1$
		}		
	}
	
	public static final class FileExt extends FilterRuleCaseSensitive {
		
		private FileExt(IConfigurationElement rule){
			super(rule.getAttribute(ExtensionConstants.RuleAttrib.ext), 
				asBoolean(rule.getAttribute(ExtensionConstants.RuleAttrib.caseSensitive), false));
		}
				
		private FileExt(String pattern, boolean caseSensitive) {
			super(pattern, caseSensitive);
		}

		public String getType() {
			return ExtensionConstants.Rule.fileext;
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleFileExt;
		}
		
		public String getName() {
			return toString();
		}
		
		public String toString() {
			if (isCaseSensitive())return NLS.bind(ValMessages.FileExtWithCase, getDisplayableType(), _pattern);
			return NLS.bind(ValMessages.FileExtWithoutCase, getDisplayableType(), _pattern);
		}

		public Boolean matchesResource(IResource resource, ContentTypeWrapper contentTypeWrapper) {
			String ext = resource.getFileExtension();
			if (isCaseSensitive())return _pattern.equals(ext);
			return _pattern.equalsIgnoreCase(ext);
		}
	}
	
	/**
	 * A rule that is used to filter based on file or folder names.
	 * @author karasiuk
	 *
	 */
	public static final class File extends FilterRuleCaseSensitive {
		
		private final String	_patternAsLowercase;
		
		/** One of the FileTypeXX constants. */
		private final int		_type;
		
		public static final int FileTypeFile = 1;
		public static final int FileTypeFolder = 2;
		public static final int FileTypeFull = 3;
		
		private static File createFile(IConfigurationElement rule){
			String pattern = rule.getAttribute(ExtensionConstants.RuleAttrib.name);
			if (pattern == null)throw new IllegalStateException(ValMessages.ErrPatternAttrib);
			String type = rule.getAttribute(ExtensionConstants.RuleAttrib.fileType);
			if (type == null)throw new IllegalStateException(ValMessages.ErrTypeReq);
			
			int myType = -1;
			if (ExtensionConstants.FileType.file.equals(type))myType = FileTypeFile;
			else if (ExtensionConstants.FileType.folder.equals(type)){
				myType = FileTypeFolder;
				if (!pattern.endsWith(PortableFileDelim))pattern += PortableFileDelim;
			}
			else if (ExtensionConstants.FileType.full.equals(type))myType = FileTypeFull;
			else {
				Object[] parms = {type, ExtensionConstants.FileType.file, ExtensionConstants.FileType.folder, 
					ExtensionConstants.FileType.full};
				throw new IllegalStateException(NLS.bind(ValMessages.ErrType, parms));
			}
			boolean caseSensitive = asBoolean(rule.getAttribute(ExtensionConstants.RuleAttrib.caseSensitive), false);
			return new File(pattern, caseSensitive, myType);
		}
		
		private  File(String pattern, boolean caseSensitive, int type){			
			super(pattern, caseSensitive);
			_type = type;
			_patternAsLowercase = pattern == null ? null : pattern.toLowerCase();
		}
				
		public String getType() {
			return ExtensionConstants.Rule.file;
		}
		
		public String getDisplayableType() {
			if (_type == FileTypeFolder)return ValMessages.RuleFolder;
			if (_type == FileTypeFull)return ValMessages.RuleFull;
			return ValMessages.RuleFile;
		}
		
		public void setData(IConfigurationElement rule) {
		}
		
		public String toString() {
			if (isCaseSensitive())return NLS.bind(ValMessages.FileExtWithCase, getDisplayableType(), _pattern);
			return NLS.bind(ValMessages.FileExtWithoutCase, getDisplayableType(), _pattern);
		}
		
		public Boolean matchesResource(IResource resource, ContentTypeWrapper contentTypeWrapper) {
			String name = null;
			switch (_type){
			case FileTypeFile:
				name = resource.getName();
				break;
				
			case FileTypeFolder:
				name = resource.getProjectRelativePath().removeLastSegments(1).toString() + PortableFileDelim;
				break;
				
			case FileTypeFull:
				name = resource.getProjectRelativePath().toPortableString();
				break;
			}
			
			if (name == null)return Boolean.FALSE;
			if (isCaseSensitive())return name.startsWith(_pattern);
			return name.toLowerCase().startsWith(_patternAsLowercase);
		}
								
		@Override
		public void save(Serializer ser) {
			super.save(ser);
			ser.put(_type);
		}
		
	}
	
	public static final class Facet extends FilterRule {
		
		private final String _versionExpression;
		private String _facetLabel = null;
		
		private Facet(IConfigurationElement rule){
			super(rule.getAttribute(ExtensionConstants.RuleAttrib.id));
			_versionExpression = rule.getAttribute(ExtensionConstants.RuleAttrib.version);
			
		}
				
		public Facet(String facetId, String versionExpression) {
			super(facetId);
			_versionExpression = versionExpression;
			
			try
			{
				IProjectFacet facet = ProjectFacetsManager.getProjectFacet(facetId);
				if(facet != null){
					_facetLabel = facet.getLabel();
				}
			} catch(IllegalArgumentException ex) {
				//do nothing
			}
		}

		public String getType() {
			return ExtensionConstants.Rule.facet;
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleFacet;
		}
		
		@Override
		public Boolean matchesProject(IProject project) {
			try {
				if (_versionExpression == null)return FacetedProjectFramework.hasProjectFacet(project, _pattern);
				return FacetedProjectFramework.hasProjectFacet(project, _pattern, _versionExpression);
			}
			catch (CoreException e){
				if (Tracing.isLogging())ValidationPlugin.getPlugin().handleException(e);
			}
			return Boolean.FALSE;
		}
		
		@Override
		public String toString() {
			String facetLabel = _facetLabel;
			StringBuffer b = new StringBuffer(200);
			b.append(getDisplayableType());
			b.append(": "); //$NON-NLS-1$
			
			//Dispay facet Labels when selecting Settings
			if(_facetLabel == null){
				try
				{
					IProjectFacet facet = ProjectFacetsManager.getProjectFacet(_pattern);
					facetLabel = facet.getLabel();		
				} catch(IllegalArgumentException ex) {
					//do nothing
				}				
			}
			
			if(facetLabel != null && facetLabel.length() > 0){
				b.append(facetLabel);
				b.append(" - "); //$NON-NLS-1$
			}
			
			b.append(_pattern);
			
			if (_versionExpression !=  null){
				b.append(" ("); //$NON-NLS-1$
				b.append(_versionExpression);
				b.append(")"); //$NON-NLS-1$
			}
			return b.toString();
		}
	}
	
	public static final class ContentType extends FilterRule {
		
		private final IContentType 	_type;
		private final boolean		_exactMatch;
		
		private ContentType(String pattern, boolean exactMatch){
			super(pattern);
			_type = Platform.getContentTypeManager().getContentType(pattern);
			_exactMatch = exactMatch;
		}
		
		private static ContentType createContentType(IConfigurationElement rule){
			String pattern = rule.getAttribute(ExtensionConstants.RuleAttrib.id);
			boolean exactMatch = true;
			String exact = rule.getAttribute(ExtensionConstants.RuleAttrib.exactMatch);
			if (ExtensionConstants.False.equals(exact)) exactMatch = false;
			
			return new ContentType(pattern, exactMatch);
		}
				
		public String getType() {
			return ExtensionConstants.Rule.contentType;
		}
		
		@Override
		public int hashCodeForConfig() {
			int h =  super.hashCodeForConfig();
			if (_exactMatch)h += 301;
			return h;
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleContentType;
		}
								
		@Override
		public void save(Serializer ser) {
			super.save(ser);
			ser.put(_exactMatch);
		}

		public void setData(IConfigurationElement rule) {
		}
				
		public Boolean matchesResource(IResource resource, ContentTypeWrapper contentTypeWrapper) {
			if (_type == null)return Boolean.FALSE;
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				IContentType ct = contentTypeWrapper.getContentType(file);
				if (ct == null)return Boolean.FALSE;
				boolean match = false;
				if (_exactMatch)match = ct.getId().equals(_type.getId());
				else match = ct.isKindOf(_type);
				
				if (match && Tracing.isTraceMatches())
					Tracing.log("FilterRule-01: ", toString() + " has matched " + resource); //$NON-NLS-1$ //$NON-NLS-2$
				return match;
			}
			return Boolean.FALSE;
		}
		
		@Override
		public String toString() {
			try {
				if (_exactMatch)return NLS.bind(ValMessages.ContentTypeExact, getDisplayableType(), _type.getName().concat(" - ").concat(_pattern)); //$NON-NLS-1$
					return NLS.bind(ValMessages.ContentTypeNotExact, getDisplayableType(), _type.getName().concat(" - ").concat(_pattern)); //$NON-NLS-1$
			}catch(NullPointerException npe) {
				if (_exactMatch)return NLS.bind(ValMessages.ContentTypeExact, getDisplayableType(), _pattern);
					return NLS.bind(ValMessages.ContentTypeNotExact, getDisplayableType(), _pattern);
			}
		}
	}
	
	public static final class TargetRuntime extends FilterRule {
		
		private String patternLabel = null;
		
		private TargetRuntime(IConfigurationElement rule) {
			super(rule.getAttribute(ExtensionConstants.RuleAttrib.id));
		}
	
		public TargetRuntime(String targetRuntime) {
			super(targetRuntime);
			
			String runtime = ValidatorHelper.getRuntimeName(targetRuntime);
			
			if(runtime != null){
				patternLabel = runtime;
			}
		}
		
		public String getType() {
			return ExtensionConstants.Rule.targetRuntime;
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleTargetRuntime;
		}
		
		public Boolean matchesProject(IProject project){
			try {
				IRuntime runtime = ValidatorHelper.getTargetRuntime(project);
				if(runtime != null)
					return runtime.getName().equals(_pattern);
			} catch (CoreException e) {
				//do nothing
			}
			
			return Boolean.FALSE;
		}
		
		@Override
		public String toString() {
			if(patternLabel == null){
				patternLabel = ValidatorHelper.getRuntimeName(_pattern);
			}

			if(patternLabel != null && patternLabel.length() > 0){
				return getDisplayableType() + ": " + patternLabel.concat(" - ").concat(_pattern); //$NON-NLS-1$ //$NON-NLS-2$
			}

			return getDisplayableType() + ": " + _pattern; //$NON-NLS-1$
		}
	}
	
	public static final class FilePattern extends FilterRuleCaseSensitive {
		
		private final Pattern _compiledPattern;
		
		private static FilePattern createFilePattern(IConfigurationElement rule){
			String pattern = rule.getAttribute(ExtensionConstants.RuleAttrib.regex);
			boolean caseSensitive = asBoolean(rule.getAttribute(ExtensionConstants.RuleAttrib.caseSensitive), false);
			return new FilePattern(pattern, caseSensitive);
		}
		
		private FilePattern(String pattern, boolean caseSensitive){
			super(pattern, caseSensitive);
			int flags = 0;
			if (caseSensitive)flags = Pattern.CASE_INSENSITIVE;
			Pattern compiledPattern = Pattern.compile(pattern, flags);				
			_compiledPattern = compiledPattern;
		}

		@Override
		public String getDisplayableType() {
			return ValMessages.RulePattern;
		}

		@Override
		public String getType() {
			return ExtensionConstants.Rule.pattern;
		}
		
		@Override
		public Boolean matchesResource(IResource resource, ContentTypeWrapper wrapper) {
			String name = PortableFileDelim + resource.getProjectRelativePath().toPortableString();
			return _compiledPattern.matcher(name).matches();
		}		
	}

	/**
	 * Save your settings into the serializer.
	 * @param ser
	 */
	public void save(Serializer ser) {
		ser.put(getType());
		ser.put(getPattern());		
	}

	public int hashCodeForConfig() {
		int h = getType().hashCode();
		if (_pattern != null)h += _pattern.hashCode();
		return h;
	}
}
