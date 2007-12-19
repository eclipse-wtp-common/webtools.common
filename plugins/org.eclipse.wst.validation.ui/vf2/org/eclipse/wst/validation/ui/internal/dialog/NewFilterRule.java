package org.eclipse.wst.validation.ui.internal.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.wst.validation.internal.model.FilterRule;
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
				(new Label(control, SWT.WRAP)).setText(desc[i]);
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
			Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(2, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrFileExtensionLabel);
			_pattern = new Text(control, SWT.NONE);
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
		
		private IProject	_project;
		private int			_type;
		
		public FilePage(IProject project){
			super("file", ValUIMessages.FrFolderOrFile, null); //$NON-NLS-1$
			_project = project;
		}

		public void createControl(Composite parent) {
			final Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(4, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrFolderOrFileLabel);
			_pattern = new Text(control, SWT.NONE);
			_pattern.setFocus();
			_pattern.setLayoutData(new GridData(300, 14));
			_pattern.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
				}
				
			});
			
			_browseFile = new Button(control, SWT.PUSH);
			_browseFile.setText(ValUIMessages.FrBrowseFile);
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
					IPath base = _project == null ? 
						ResourcesPlugin.getWorkspace().getRoot().getLocation() :
						_project.getLocation();
					String root = null;
					if (base != null){
						root = base.toOSString();
						fd.setFilterPath(root);
					}
					
					String file = fd.open();
					if (file != null){
						if (root != null && file.startsWith(root) && file.length() > root.length()){
							file = file.substring(root.length()+1);
							_type = FilterRule.File.FileTypeFile;
						}
						else _type = FilterRule.File.FileTypeFull;
						_pattern.setText(file);
					}
				}
				
			});
			
			_browseFolder = new Button(control, SWT.PUSH);
			_browseFolder.setText(ValUIMessages.FrBrowseFolder);
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
					IPath base = _project == null ? 
						ResourcesPlugin.getWorkspace().getRoot().getLocation() : _project.getLocation();
					String root = null;
					if (base != null){
						root = base.toOSString();
						fd.setFilterPath(root);
					}
					String dir = fd.open();
					if (dir != null){
						if (root != null && dir.startsWith(root) && dir.length() > root.length()){
							dir = dir.substring(root.length()+1);
							_type = FilterRule.File.FileTypeFolder;
						}
						else _type = FilterRule.File.FileTypeFull;
						_pattern.setText(dir);				
					}
				}
				
			});
			
			_case = new Button(control, SWT.CHECK);
			_case.setText(ValUIMessages.FrCaseSensitive);
			_case.setSelection(false);
			_case.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,false, false, 2, 1));
		}

		public FilterRule getFilterRule() {
			if (!isPageComplete())return null;
			return FilterRule.createFile(_pattern.getText(), _case.getSelection(), _type);
		}
		
		public boolean isPageComplete() {
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
			return _natures.getText().trim().length() > 0;
		}
	
	}
	
	public static class FacetPage extends WizardPage implements FilterWizardPage {
		
		private Text	_pattern;
		
		public FacetPage(){
			super("facet", ValUIMessages.FrFacit, null); //$NON-NLS-1$
		}

		public void createControl(Composite parent) {
			Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(2, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrFacitLabel);
			_pattern = new Text(control, SWT.NONE);
			_pattern.setFocus();
			_pattern.setLayoutData(new GridData(300, 14));
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
			return _pattern.getText().trim().length() > 0;
		}
		
	}
	
	public static class ContentTypePage extends WizardPage implements FilterWizardPage {
		private Text	_pattern;
		
		public ContentTypePage(){
			super("contentType", ValUIMessages.FrContentType, null); //$NON-NLS-1$
		}

		public void createControl(Composite parent) {
			Composite control = new Composite(parent, SWT.NONE);
			setControl(control);
			control.setLayout(new GridLayout(2, false));
			(new Label(control, SWT.NONE)).setText(ValUIMessages.FrContentTypeLabel);
			_pattern = new Text(control, SWT.NONE);
			_pattern.setFocus();
			_pattern.setLayoutData(new GridData(300, 14));
			_pattern.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
				}
				
			});
		}

		public FilterRule getFilterRule() {
			if (!isPageComplete())return null;
			FilterRule rule = FilterRule.createContentType(_pattern.getText());
			return rule;
		}
		
		public boolean isPageComplete() {
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
