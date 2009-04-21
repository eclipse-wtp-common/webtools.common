/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.ui.internal.dialog;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.ui.internal.HelpContextIds;
import org.eclipse.wst.validation.ui.internal.ValUIMessages;

/**
 * A wizard that adds new rules to validators.
 * @author karasiuk
 *
 */
public class NewFilterRule extends Wizard {
	
	private Page1 			_page1;
	private IWizardPage		_page2;
	
	private IProject		_project;
	
	private FilterRule		_rule;
	
	public NewFilterRule(IProject project){
		_project = project;
		setWindowTitle(ValUIMessages.FrWizard);
		_page1 = new Page1(project);
		addPage(_page1);
		setForcePreviousAndNextButtons(true);
	}
	
	public boolean canFinish() {	
		if (_page2 != null)return _page2.isPageComplete();
		return false;
	}
		
	public FilterRule getRule(){
		return _rule;
	}
	
	public boolean performFinish() {
		if (_page2 == null)return false;
		FilterWizardPage page = (FilterWizardPage)_page2;
		_rule = page.getFilterRule();
		return _rule != null;
	}
	
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == _page1){
			setForcePreviousAndNextButtons(false);
			_page2 = returnOrBuildPage(_page1.getSelectedFilter());
			return _page2;
		}
		return null;
	}
	
	private IWizardPage returnOrBuildPage(int selectedFilter) {
			IWizardPage page =  null;
			if (_project != null && selectedFilter == 2)selectedFilter = 4;
			switch (selectedFilter){
			case 0:
				page = new FileExtPage();
				break;
			case 1:
				page = new FilePage(_project);
				break;
			case 2:
				page = new ProjectPage();
				break;
			case 3:
				page = new FacetPage();
				break;
			case 4:
				page = new ContentTypePage();
				break;
			}
		addPage(page);
		return page;
	}

	public static class Page1 extends WizardPage {
		
		private int 		_selectedFilter;
		private IProject	_project;

		
		public Page1(IProject project){
			super("page1", ValUIMessages.FrSelectFilterType, null); //$NON-NLS-1$
			setPageComplete(true);
			_project = project;
		}
		
		public void createControl(Composite parent) {
			String helpId = _project == null ? HelpContextIds.FilterRule : HelpContextIds.ProjectFilterRule;
			PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, helpId);
			String[] labels = null;
			String[] desc = null;
			if (_project != null){
				labels = new String[]{ValUIMessages.LabelExtension, ValUIMessages.LabelFile,
						ValUIMessages.LabelContentType};
				
				desc = new String[]{ValUIMessages.DescExtension, ValUIMessages.DescFile,
						ValUIMessages.DescContentType};
			}
			else {
				labels = new String[]{ValUIMessages.LabelExtension, ValUIMessages.LabelFile,
						ValUIMessages.LabelProjects, ValUIMessages.LabelFacets,
						ValUIMessages.LabelContentType};
				
				desc = new String[]{ValUIMessages.DescExtension, ValUIMessages.DescFile,
						ValUIMessages.DescProjects, ValUIMessages.DescFacets, 
						ValUIMessages.DescContentType};
			}
			
			Composite control = new Composite(parent, SWT.NONE);
			control.setLayout(new GridLayout(2, false));

			SelectionListener listener = new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					doIt(e);
				}

				public void widgetSelected(SelectionEvent e) {
					doIt(e);
				}
				
				private void doIt(SelectionEvent e){
					if (e.getSource() instanceof Button){
						Button b = (Button)e.getSource();
						if (b.getData() instanceof Integer) {
							Integer index = (Integer) b.getData();
							setSelectedFilter(index.intValue());
						}
					}
				}
				
			};
			for (int i=0; i<labels.length; i++){
				Button button = new Button(control, SWT.RADIO);
				button.setText(labels[i]);
				button.setData(new Integer(i));			
				button.addSelectionListener(listener);
				(new Text(control, SWT.WRAP|SWT.READ_ONLY)).setText(desc[i]);
				if (i == 0)button.setSelection(true);				
			}			
			setControl(control);			
		}

		public int getSelectedFilter() {
			return _selectedFilter;
		}

		public void setSelectedFilter(int selectedFilter) {
			_selectedFilter = selectedFilter;
			getContainer().updateButtons();
		}
						
	}
	
	public static class FileExtPage extends WizardPage implements FilterWizardPage {
		
		private Text 	_pattern;
		private Button	_case;
		
		public FileExtPage(){
			super("fileext", ValUIMessages.FrFileExtension, null); //$NON-NLS-1$
		}

		public void createControl(Composite parent) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIds.FRFileExt);
			Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(2, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrFileExtensionLabel);
			_pattern = new Text(control, SWT.BORDER);
			_pattern.setFocus();
			_pattern.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
				}
				
			});
			
			_case = new Button(control, SWT.CHECK);
			_case.setText(ValUIMessages.FrCaseSensitive);
			_case.setSelection(false);
			_case.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,false, false, 2, 1));
			
		}

		public FilterRule getFilterRule() {
			if (!isPageComplete())return null;
			return FilterRule.FileExt.createFileExt(_pattern.getText(), _case.getSelection());
		}
		
		public boolean isPageComplete() {
			return _pattern != null && _pattern.getText().trim().length() > 0;
		}
		
		
	}
	
	public static class FilePage extends WizardPage implements FilterWizardPage {
		
		private Text 	_pattern;
		private Button	_browseFile;
		private Button	_browseFolder;
		private Button	_case;
		private Button	_simpleFileName;
		
		private IProject	_project;
		private int			_type = FilterRule.File.FileTypeFile;
		private IPath		_fullFileName;
		private String		_root;
		
		public FilePage(IProject project){
			super("file", ValUIMessages.FrFolderOrFile, null); //$NON-NLS-1$
			_project = project;
		}

		public void createControl(Composite parent) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIds.FRName);
			final Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(4, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrFolderOrFileLabel);
			_pattern = new Text(control, SWT.BORDER);
			_pattern.setFocus();
			_pattern.setLayoutData(new GridData(300, SWT.DEFAULT));
			_pattern.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
				}
				
			});

			_root = null;
			IPath base = _project == null ? ResourcesPlugin.getWorkspace().getRoot().getLocation() :
				_project.getLocation();
			if (base != null)_root = base.toOSString();
						
			_browseFile = new Button(control, SWT.PUSH);
			_browseFile.setText(ValUIMessages.FrBrowseFile);
			_browseFile.setEnabled(_root != null);
			_browseFile.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					browse();
				}

				public void widgetSelected(SelectionEvent e) {
					browse();
				}
				
				private void browse(){
					FileDialog fd = new FileDialog(control.getShell(), SWT.OPEN);
					fd.setText(ValUIMessages.FrFileFilter);
					fd.setFilterPath(_root);
					
					String file = fd.open();
					if (file != null){
						if (file.startsWith(_root))file = file.substring(_root.length()+1);
						else file = null;
					}
					if (file != null){
						_fullFileName = new Path(file);
						if (_project == null)_fullFileName = _fullFileName.removeFirstSegments(1);

						if (_simpleFileName.getSelection()){
							_type = FilterRule.File.FileTypeFile;
							_pattern.setText(_fullFileName.lastSegment());
						}
						else {
							_type = FilterRule.File.FileTypeFull;
							_pattern.setText(_fullFileName.toPortableString());
						}
					}
					else {
						_fullFileName = null;
						_pattern.setText(""); //$NON-NLS-1$
					}
				}				
			});
			
			_browseFolder = new Button(control, SWT.PUSH);
			_browseFolder.setText(ValUIMessages.FrBrowseFolder);
			_browseFolder.setEnabled(_root != null);
			_browseFolder.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					browse();
				}

				public void widgetSelected(SelectionEvent e) {
					browse();
				}
				
				private void browse(){
					DirectoryDialog fd = new DirectoryDialog(control.getShell());
					fd.setMessage(ValUIMessages.FrFolderFilter);
					fd.setFilterPath(_root);
					String dir = fd.open();
					if (dir != null){
						_fullFileName = null;
						_type = FilterRule.File.FileTypeFolder;
						if (dir.startsWith(_root) && dir.length() > _root.length()){
							dir = dir.substring(_root.length()+1);
							IPath path = new Path(dir);
							if (_project == null)path = path.removeFirstSegments(1);
							_pattern.setText(path.toPortableString());
						}
						else {
							_pattern.setText(""); //$NON-NLS-1$
						}
					}
				}
				
			});
			
			_case = new Button(control, SWT.CHECK);
			_case.setText(ValUIMessages.FrCaseSensitive);
			_case.setSelection(false);
			_case.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,false, false, 4, 1));
			
			addSimpleFileNameButton(control);
		}

		private void addSimpleFileNameButton(final Composite control) {
			_simpleFileName = new Button(control, SWT.CHECK);
			_simpleFileName.setText(ValUIMessages.FrSimpleFileName);
			_simpleFileName.setSelection(_type == FilterRule.File.FileTypeFile);
			_simpleFileName.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,false, false, 4, 1));
			
			_simpleFileName.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					if (_type == FilterRule.File.FileTypeFolder)return;
					if (_simpleFileName.getSelection()){
						if (_fullFileName != null)_pattern.setText(_fullFileName.lastSegment());
						_type = FilterRule.File.FileTypeFile;
					}
					else {
						if (_fullFileName != null)_pattern.setText(_fullFileName.toPortableString());
						_type = FilterRule.File.FileTypeFull;					}
					}				
				});
		}

		public FilterRule getFilterRule() {
			if (!isPageComplete())return null;
			return FilterRule.createFile(_pattern.getText(), _case.getSelection(), _type);
		}
		
		public boolean isPageComplete() {
			if (_pattern == null)return false;
			if (_pattern.getText() == null)return false;
			return _pattern.getText().trim().length() > 0;
		}
		
	}
	
	public static class ProjectPage extends WizardPage implements FilterWizardPage{
		
		private Combo		_natures;
		private String[]	_ids;
		
		public ProjectPage(){
			super("project", ValUIMessages.FrProjectNature, null); //$NON-NLS-1$
		}

		public void createControl(Composite parent) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIds.FRProjectNature);
			Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(2, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrProjectNatureLabel);
			
			IProjectNatureDescriptor[] pn = ResourcesPlugin.getWorkspace().getNatureDescriptors();
			String items[] = new String[pn.length];
			_ids = new String[pn.length];
			for (int i=0; i<pn.length; i++){
				_ids[i] = pn[i].getNatureId();
				items[i] = pn[i].getLabel() + " - " + _ids[i]; //$NON-NLS-1$
			}
			_natures = new Combo(control, SWT.DROP_DOWN);
			_natures.setItems(items);
			_natures.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();					
				}
				
			});
						
		}

		public FilterRule getFilterRule() {
			if (!isPageComplete())return null;
			int i = _natures.getSelectionIndex();
			if (i == -1)return null;
			
			return FilterRule.createProject(_ids[i]);
		}
		
		public boolean isPageComplete() {
			if (_natures == null)return false;
			if (_natures.getText() == null)return false;
			return _natures.getText().trim().length() > 0;
		}
	
	}
	
	public static class FacetPage extends WizardPage implements FilterWizardPage {
		
		private Text	_pattern;
		
		public FacetPage(){
			super("facet", ValUIMessages.FrFacit, null); //$NON-NLS-1$
		}

		public void createControl(Composite parent) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIds.FRFacet);
			Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(2, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrFacitLabel);
			_pattern = new Text(control, SWT.BORDER);
			_pattern.setFocus();
			_pattern.setLayoutData(new GridData(300, SWT.DEFAULT));
			_pattern.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
				}
				
			});
		}

		public FilterRule getFilterRule() {
			if (!isPageComplete())return null;
			FilterRule rule = FilterRule.createFacet(_pattern.getText());
			return rule;
		}
		
		public boolean isPageComplete() {
			if (_pattern == null)return false;
			if (_pattern.getText() == null)return false;
			return _pattern.getText().trim().length() > 0;
		}
		
	}
	
	public static class ContentTypePage extends WizardPage implements FilterWizardPage {
		private Combo	_pattern;
		private Button	_exactMatch;
		private Map<String, IContentType> _map;
		
		public ContentTypePage(){
			super("contentType", ValUIMessages.FrContentType, null); //$NON-NLS-1$
		}

		public void createControl(Composite parent) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIds.FRContentType);
			Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(2, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrContentTypeLabel);
			
			IContentType[] types = Platform.getContentTypeManager().getAllContentTypes();
			_map = new TreeMap<String, IContentType>();
			for (IContentType type : types){
				String name = type.getName();
				if (name == null)name = type.getId();
				_map.put(name, type);
			}
			String items[] = new String[_map.size()];
			int i = 0;
			for (String label : _map.keySet()){
				items[i++] = label;
			}
			_pattern = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
			_pattern.setFocus();
			_pattern.setLayoutData(new GridData(300, SWT.DEFAULT));
			_pattern.setVisibleItemCount(20);
			_pattern.setItems(items);
			_pattern.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
				}
				
			});
			
			_exactMatch = new Button(control, SWT.CHECK);
			_exactMatch.setText(ValUIMessages.FrExactMatch);
			_exactMatch.setSelection(false);
			_exactMatch.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,false, false, 2, 1));

		}

		public FilterRule getFilterRule() {
			if (!isPageComplete())return null;
			IContentType type = _map.get(_pattern.getText());
			if (type == null)return null;
			FilterRule rule = FilterRule.createContentType(type.getId(), _exactMatch.getSelection());
			return rule;
		}
		
		public boolean isPageComplete() {
			if (_pattern == null)return false;
			if (_pattern.getText() == null)return false;
			return _pattern.getText().trim().length() > 0;
		}
		
	}
	
	interface FilterWizardPage {
		/**
		 * Answer the rule that was created.
		 * 
		 * @return null if the user didn't create a new rule.
		 */
		FilterRule getFilterRule();
	}


}
