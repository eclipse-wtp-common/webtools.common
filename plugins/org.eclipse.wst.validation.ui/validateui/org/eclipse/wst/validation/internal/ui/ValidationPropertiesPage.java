/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;


import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidationConfiguration;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;

import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;

/**
 * This class and its inner classes are not intended to be subclassed outside of the validation
 * framework.
 * 
 * This page implements the PropertyPage for validators; viewed when the user right-clicks on the
 * IProject, selects "Properties", and then "Validation."
 * 
 * There exist three possible page layouts: if there is an eclipse internal error, and the page is
 * brought up on a non-IProject type; if there are no validators configured on that type of
 * IProject, and a page which lists all validators configured on that type of IProject. These three
 * pages are implemented as inner classes, so that it's clear which method is needed for which
 * input. When all of the methods, and behaviour, were implemented in this one class, much more
 * error-checking had to be done, to ensure that the method wasn't being called incorrectly by one
 * of the pages.
 */
public class ValidationPropertiesPage extends PropertyPage {
	static final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$
	static final String TAB = "\t"; //$NON-NLS-1$
	static final String NEWLINE_AND_TAB = NEWLINE + TAB;
	private IValidationPage _pageImpl = null;

	/**
	 * Initially, this interface was created as an abstract class, and getControl() was implemented.
	 * (getProject() could also have been implemented in the abstract class.) However, at runtime, a
	 * NullPointerException was thrown; the inner class had lost its pointer to its enclosing class.
	 * After some experimentation, I discovered that if I changed the parent to an interface, the
	 * enclosing class could be found. (Merely moving the AValidationPage into its own file was
	 * insufficient.)
	 */
	public interface IValidationPage {
		public abstract Composite createPage(Composite parent) throws InvocationTargetException;

		public abstract boolean performOk() throws InvocationTargetException;

		public boolean performDefaults() throws InvocationTargetException;

		public Composite getControl();

		public abstract void dispose();
	}

	public class InvalidPage implements IValidationPage {
		private Composite page = null;

		private Composite composite = null;
		private GridLayout layout = null;
		private Label messageLabel = null;

		public InvalidPage(Composite parent) {
			page = createPage(parent);
		}

		/**
		 * This page is added to the Properties guide if some internal problem occurred; for
		 * example, the highlighted item in the workbench is not an IProject (according to this
		 * page's plugin.xml, this page is only valid when an IProject is selected).
		 */
		public Composite createPage(Composite parent) {
			// Don't create the default and apply buttons.
			noDefaultAndApplyButton();

			final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite = new Composite(sc1, SWT.NONE);
			sc1.setContent(composite);
			layout = new GridLayout();
			composite.setLayout(layout);
			WorkbenchHelp.setHelp(composite, ContextIds.VALIDATION_PROPERTIES_PAGE);

			messageLabel = new Label(composite, SWT.NONE);
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INVALID_REGISTER));

			composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			return composite;
		}

		public boolean performDefaults() {
			return true;
		}

		/**
		 * Since this page occurs under invalid circumstances, there is nothing to save.
		 */
		public boolean performOk() {
			return true;
		}

		public Composite getControl() {
			return page;
		}

		public void dispose() {
			messageLabel.dispose();
			//			layout.dispose();
			composite.dispose();
		}
	}

	public class NoValidatorsPage implements IValidationPage {
		private Composite page = null;

		private Composite composite = null;
		private GridLayout layout = null;
		private GridData data = null;
		private Label messageLabel = null;

		public NoValidatorsPage(Composite parent) {
			page = createPage(parent);
		}

		/**
		 * This page is created if an IProject is selected, but that project has no validators
		 * configured (i.e., the page is valid, but an empty list.)
		 */
		public Composite createPage(Composite parent) {
			// Don't create the default and apply buttons.
			noDefaultAndApplyButton();

			// top level group
			final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite = new Composite(sc1, SWT.NONE);
			sc1.setContent(composite);
			layout = new GridLayout();
			composite.setLayout(layout);
			data = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
			composite.setLayoutData(data);
			WorkbenchHelp.setHelp(composite, ContextIds.VALIDATION_PROPERTIES_PAGE);

			messageLabel = new Label(composite, SWT.NONE);
			String[] msgParm = {getProject().getName()};
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_LBL_NOVALIDATORS_DESC, msgParm));
			composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			return composite;
		}

		public boolean performDefaults() {
			return true;
		}

		/**
		 * Since there are no validators, there is nothing to save.
		 */
		public boolean performOk() {
			return true;
		}

		public Composite getControl() {
			return page;
		}

		public void dispose() {
			messageLabel.dispose();
			//			layout.dispose();
			//			data.dispose();
			composite.dispose();
		}
	}

	public class ValidatorListPage implements IValidationPage {
		Composite page = null;
		GridLayout layout = null;
		GridData data = null;
		Label messageLabel = null;
		CheckboxTableViewer validatorList = null;
		Button overrideGlobalButton = null;
		Button autoButton = null;
		Button valWhenBuildButton = null;
		Button selectAllButton = null;
		Button deselectAllButton = null;
		Label emptyRowPlaceholder = null;
		Text maxValProblemsField = null;
		Label maxValProblemsFieldLabel = null;

		ProjectConfiguration pagePreferences = null;

		// default values for the widgets, initialized in the constructor
		private boolean isAutoBuildEnabled = false;
		private boolean isBuilderConfigured = false;
		private boolean canOverride = false;

		private ValidatorMetaData[] oldVmd = null; // Cache the enabled validators so that, if there

		// is no change to this list, the expensive task
		// list update can be avoided

		/**
		 * This class is provided for the CheckboxTableViewer in the
		 * ValidationPropertiesPage$ValidatorListPage class.
		 */
		public class ValidationContentProvider implements IStructuredContentProvider {
			/**
			 * Disposes of this content provider. This is called by the viewer when it is disposed.
			 */
			public void dispose() {
				//dispose
			}

			/**
			 * Returns the elements to display in the viewer when its input is set to the given
			 * element. These elements can be presented as rows in a table, items in a list, etc.
			 * The result is not modified by the viewer.
			 * 
			 * @param inputElement
			 *            the input element
			 * @return the array of elements to display in the viewer
			 */
			public java.lang.Object[] getElements(Object inputElement) {
				if (inputElement instanceof ValidatorMetaData[]) {
					// The ValidatorMetaData[] is the array which is returned by ValidatorManager's
					// getConfiguredValidatorMetaData(IProject) call.
					// This array is set to be the input of the CheckboxTableViewer in
					// ValidationPropertiesPage$ValidatorListPage's createPage(Composite)
					// method.
					return (ValidatorMetaData[]) inputElement;
				}
				return new Object[0];
			}

			/**
			 * Notifies this content provider that the given viewer's input has been switched to a
			 * different element.
			 * <p>
			 * A typical use for this method is registering the content provider as a listener to
			 * changes on the new input (using model-specific means), and deregistering the viewer
			 * from the old input. In response to these change notifications, the content provider
			 * propagates the changes to the viewer.
			 * </p>
			 * 
			 * @param viewer
			 *            the viewer
			 * @param oldInput
			 *            the old input element, or <code>null</code> if the viewer did not
			 *            previously have an input
			 * @param newInput
			 *            the new input element, or <code>null</code> if the viewer does not have
			 *            an input
			 */
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				//do nothing
			}



		}

		/**
		 * This class is provided for ValidationPropertiesPage$ValidatorListPage's
		 * checkboxTableViewer element.
		 */
		public class ValidationLabelProvider extends LabelProvider {
			/**
			 * Override the LabelProvider's text, by customizing the text for a ValidatorMetaData
			 * element.
			 */
			public String getText(Object element) {
				if (element == null) {
					return ""; //$NON-NLS-1$
				} else if (element instanceof ValidatorMetaData) {
					return ((ValidatorMetaData) element).getValidatorDisplayName();
				} else {
					return super.getText(element);
				}
			}
		}

		/**
		 * This class is used to sort the CheckboxTableViewer elements.
		 */
		public class ValidationViewerSorter extends ViewerSorter {
			/**
			 * Returns a negative, zero, or positive number depending on whether the first element
			 * is less than, equal to, or greater than the second element.
			 * <p>
			 * The default implementation of this method is based on comparing the elements'
			 * categories as computed by the <code>category</code> framework method. Elements
			 * within the same category are further subjected to a case insensitive compare of their
			 * label strings, either as computed by the content viewer's label provider, or their
			 * <code>toString</code> values in other cases. Subclasses may override.
			 * </p>
			 * 
			 * @param viewer
			 *            the viewer
			 * @param e1
			 *            the first element
			 * @param e2
			 *            the second element
			 * @return a negative number if the first element is less than the second element; the
			 *         value <code>0</code> if the first element is equal to the second element;
			 *         and a positive number if the first element is greater than the second element
			 */
			public int compare(Viewer viewer, Object e1, Object e2) {
				// Can't instantiate ViewerSorter because it's abstract, so use this
				// inner class to represent it.
				return super.compare(viewer, e1, e2);
			}
		}

		public ValidatorListPage(Composite parent) throws InvocationTargetException {
			ConfigurationManager prefMgr = ConfigurationManager.getManager();
			ValidatorManager vMgr = ValidatorManager.getManager();

			pagePreferences = new ProjectConfiguration(prefMgr.getProjectConfiguration(getProject())); // This
			// represents
			// the
			// values
			// on
			// the
			// page
			// that
			// haven't
			// been
			// persisted
			// yet.
			// Start
			// with
			// the
			// last
			// values
			// that
			// were
			// persisted
			// into
			// the
			// current
			// page's
			// starting
			// values.

			// store the default values for the widgets
			canOverride = prefMgr.getGlobalConfiguration().canProjectsOverride();
			isAutoBuildEnabled = vMgr.isGlobalAutoBuildEnabled();
			isBuilderConfigured = ValidatorManager.doesProjectSupportBuildValidation(getProject());
			oldVmd = pagePreferences.getEnabledValidators(); // Cache the enabled validators so
			// that, if there is no change to this
			// list, the expensive task list update
			// can be avoided

			createPage(parent);
		}

		/**
		 * This page is created if the current project has at least one validator configured on it.
		 */
		public Composite createPage(Composite parent) throws InvocationTargetException {
			// top level group
			final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
			page = new Composite(sc1, SWT.NONE);
			sc1.setContent(page);
			page.setLayout(new GridLayout()); // use the layout's default preferences

			Composite validatorGroup = new Composite(page, SWT.NONE);
			GridLayout validatorGroupLayout = new GridLayout();
			validatorGroupLayout.numColumns = 2;
			validatorGroup.setLayout(validatorGroupLayout);

			GridData overrideData = new GridData(GridData.FILL_HORIZONTAL);
			overrideData.horizontalSpan = 2;
			overrideGlobalButton = new Button(validatorGroup, SWT.CHECK);
			overrideGlobalButton.setLayoutData(overrideData);
			overrideGlobalButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_BUTTON_OVERRIDE, new String[]{getProject().getName()}));
			overrideGlobalButton.setFocus(); // must focus on something for F1 to have a topic to
			// launch
			overrideGlobalButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setDoesProjectOverride(overrideGlobalButton.getSelection());
					try {
						updateWidgets();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});


			emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
			emptyRowPlaceholder.setLayoutData(new GridData());

			GridData valWhenBuildData = new GridData(GridData.FILL_HORIZONTAL);
			valWhenBuildData.horizontalSpan = 2;
			valWhenBuildButton = new Button(validatorGroup, SWT.CHECK);
			valWhenBuildButton.setLayoutData(valWhenBuildData);
			valWhenBuildButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_BUTTON_FULL, new String[]{getProject().getName()}));
			valWhenBuildButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setBuildValidate(valWhenBuildButton.getSelection());
					valWhenBuildButton.setFocus();
				}
			});

			GridData valWhenAutoBuildData = new GridData(GridData.FILL_HORIZONTAL);
			valWhenAutoBuildData.horizontalSpan = 2;
			autoButton = new Button(validatorGroup, SWT.CHECK);
			autoButton.setLayoutData(valWhenAutoBuildData);
			autoButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_LBL_AUTO_VALIDATE, new String[]{getProject().getName()}));
			autoButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setAutoValidate(autoButton.getSelection());
					autoButton.setFocus();
				}
			});

			GridData listLabelData = new GridData(GridData.FILL_HORIZONTAL);
			listLabelData.horizontalSpan = 2;
			messageLabel = new Label(validatorGroup, SWT.NONE);
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_LBL_DESC, new String[]{getProject().getName()}));
			messageLabel.setLayoutData(listLabelData);

			GridData validatorListData = new GridData(GridData.FILL_HORIZONTAL);
			validatorListData.horizontalSpan = 2;
			validatorList = CheckboxTableViewer.newCheckList(validatorGroup, SWT.BORDER);
			validatorList.getTable().setLayoutData(validatorListData);
			validatorList.setLabelProvider(new ValidationLabelProvider());
			validatorList.setContentProvider(new ValidationContentProvider());
			validatorList.setSorter(new ValidationViewerSorter());
			validatorList.getTable().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setEnabledValidators(ValidationConfiguration.convertToArray(validatorList.getCheckedElements()));
					try {
						updateWidgets();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});
			validatorList.setInput(pagePreferences.getValidators());

			selectAllButton = new Button(validatorGroup, SWT.PUSH);
			GridData selectData = new GridData();
			selectAllButton.setLayoutData(selectData);
			selectAllButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_BUTTON_SELECTALL));
			selectAllButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						performSelectAll();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});
			WorkbenchHelp.setHelp(selectAllButton, ContextIds.VALIDATION_PROPERTIES_PAGE);


			GridData deselectData = new GridData();
			deselectAllButton = new Button(validatorGroup, SWT.PUSH);
			deselectAllButton.setLayoutData(deselectData);
			deselectAllButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_BUTTON_DESELECTALL));
			deselectAllButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						performDeselectAll();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});
			WorkbenchHelp.setHelp(deselectAllButton, ContextIds.VALIDATION_PROPERTIES_PAGE);

			Composite maxGroup = new Composite(page, SWT.NONE);
			GridLayout maxGroupLayout = new GridLayout();
			maxGroupLayout.numColumns = 2;
			maxGroup.setLayout(maxGroupLayout);
			GridData maxGroupData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			maxGroup.setLayoutData(maxGroupData);

			GridData maxLabel = new GridData();
			maxValProblemsFieldLabel = new Label(maxGroup, SWT.NONE);
			maxValProblemsFieldLabel.setLayoutData(maxLabel);
			maxValProblemsFieldLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_LBL_MAXMSSGS, new String[]{getProject().getName()}));

			maxValProblemsField = new Text(maxGroup, SWT.SINGLE | SWT.BORDER);
			GridData maxValProblemsData = new GridData(GridData.FILL_HORIZONTAL);
			maxValProblemsField.setLayoutData(maxValProblemsData);
			maxValProblemsField.addKeyListener(new KeyListener() {
				/**
				 * Sent when a key is pressed on the system keyboard.
				 * 
				 * @param e
				 *            an event containing information about the key press
				 */
				public void keyPressed(KeyEvent e) {
					//do nothing
				}

				/**
				 * Sent when a key is released on the system keyboard.
				 * 
				 * @param e
				 *            an event containing information about the key release
				 */
				public void keyReleased(KeyEvent e) {
					try {
						pagePreferences.setMaximumNumberOfMessages(checkInteger());
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});
			WorkbenchHelp.setHelp(maxValProblemsField, ContextIds.VALIDATION_PROPERTIES_PAGE_MAX_MESSAGES);

			// Have to set the tab order or only the first checkbox in a Composite can
			// be tab-ed to. (Seems to apply only to checkboxes. Have to use the arrow
			// key to navigate the checkboxes.)
			validatorGroup.setTabList(new Control[]{overrideGlobalButton, valWhenBuildButton, autoButton, validatorList.getTable(), selectAllButton, deselectAllButton});

			updateWidgets();

			page.setSize(page.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			return page;
		}

		protected void updateTable() throws InvocationTargetException {
			TableItem[] items = validatorList.getTable().getItems();
			for (int i = 0; i < items.length; i++) {
				TableItem item = items[i];
				ValidatorMetaData vmd = (ValidatorMetaData) item.getData();

				// Should the validator be enabled? Read the user's preferences from last time,
				// if they exist, and set from that. If they don't exist, use the Validator class'
				// default value.
				validatorList.setChecked(vmd, pagePreferences.isEnabled(vmd));
			}
		}

		public boolean performDefaults() throws InvocationTargetException {
			pagePreferences.resetToDefault();
			updateWidgets();
			checkInteger(); // clear the "max must be a positive integer" message if it exists
			getDefaultsButton().setFocus();
			return true;
		}

		public boolean performSelectAll() throws InvocationTargetException {
			validatorList.setAllChecked(true);
			pagePreferences.setEnabledValidators(ValidationConfiguration.convertToArray(validatorList.getCheckedElements()));
			updateWidgets();
			selectAllButton.setFocus();
			return true;
		}

		public boolean performDeselectAll() throws InvocationTargetException {
			validatorList.setAllChecked(false);
			pagePreferences.setEnabledValidators(ValidationConfiguration.convertToArray(validatorList.getCheckedElements()));
			updateWidgets();
			deselectAllButton.setFocus();
			return true;
		}

		protected int checkInteger() throws InvocationTargetException {
			ProjectConfiguration pc = ConfigurationManager.getManager().getProjectConfiguration(getProject());
			String text = maxValProblemsField.getText();
			if (text == null) {
				setErrorMessage(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_ERROR_INT));
				return pc.getMaximumNumberOfMessages();
			}
			try {
				Integer tempInt = new Integer(text.trim());

				// no exception? It's an int, then.
				if (tempInt.intValue() <= 0) {
					setErrorMessage(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_ERROR_INT));
					return pc.getMaximumNumberOfMessages();
				}
				setErrorMessage(null);
				return Integer.valueOf(maxValProblemsField.getText().trim()).intValue();
			} catch (NumberFormatException exc) {
				setErrorMessage(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_ERROR_INT));
				return pc.getMaximumNumberOfMessages();
			}

		}

		void updateWidgets() throws InvocationTargetException {
			// Since the setting of the "override" button enables/disables the other widgets on the
			// page,
			// update the enabled state of the other widgets from the "override" button.
			boolean overridePreferences = canOverride && pagePreferences.doesProjectOverride();

			overrideGlobalButton.setEnabled(canOverride);
			overrideGlobalButton.setSelection(overridePreferences);

			validatorList.getTable().setEnabled(overridePreferences);
			validatorList.setAllGrayed(!overridePreferences);

			selectAllButton.setEnabled(overridePreferences); // since help messsage isn't
			// context-sensitive, it's set in the
			// createPage method
			deselectAllButton.setEnabled(overridePreferences);

			maxValProblemsField.setEnabled(overridePreferences);
			maxValProblemsField.setText(String.valueOf(pagePreferences.getMaximumNumberOfMessages()));

			updateTable();

			// Never check if builder is configured because if it isn't, the user needs to be able
			// to add the builder via the instructions on the F1 infopops.
			// In the case when the builder isn't configured, show the checkbox as enabled but
			// cleared
			// The only time that these two checkboxes are disabled is when no validators are
			// enabled in the list.
			boolean valEnabled = (pagePreferences.numberOfEnabledValidators() > 0);
			valWhenBuildButton.setEnabled(overridePreferences && valEnabled);
			valWhenBuildButton.setSelection(pagePreferences.isBuildValidate() && valEnabled && isBuilderConfigured);

			boolean incValEnabled = (pagePreferences.numberOfEnabledIncrementalValidators() > 0);
			autoButton.setEnabled(overridePreferences && isAutoBuildEnabled && incValEnabled);
			autoButton.setSelection(pagePreferences.isAutoValidate() && incValEnabled && isAutoBuildEnabled && isBuilderConfigured);

			updateHelp();
		}

		protected void updateHelp() throws InvocationTargetException {
			// Whenever a widget is disabled, it cannot get focus.
			// Since it can't have focus, its context-sensitive F1 help can't come up.
			// From experimentation, I know that the composite parent of the widget
			// can't have focus either. So, fudge the focus by making the table the widget
			// surrogate so that the F1 help can be shown, with its instructions on how to
			// enable the disabled widget. The table never has F1 help associated with it other
			// than the page F1, so this fudge doesn't remove any context-sensitive help
			// from the table widget.

			if (autoButton.getEnabled()) {
				// set the table's help back to what it was
				WorkbenchHelp.setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE);
				WorkbenchHelp.setHelp(autoButton, ContextIds.VALIDATION_PROPERTIES_PAGE_AUTO_ENABLED);
			} else {
				// The order of the following if statement is important!
				// If the user cannot enable automatic validation on the project, then the user
				// should not be told, for example, to turn auto-build on. Let the user know that
				// no matter what they do they cannot run auto-validate on the project. IF the
				// project
				// supports auto-validate, THEN check for the items which the user can change.
				validatorList.getTable().setFocus();
				if (pagePreferences.numberOfIncrementalValidators() == 0) {
					WorkbenchHelp.setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_NOINCVALCONFIG);
				} else if (!ValidatorManager.getManager().isGlobalAutoBuildEnabled()) {
					WorkbenchHelp.setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_AUTOBUILD);
				} else {
					// Incremental validators configured but not selected
					WorkbenchHelp.setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_NOINCVALSELECTED);
				}
			}

			// if autoButton AND build button are disabled, show the build button's "to enable" text
			if (valWhenBuildButton.getEnabled()) {
				// Do NOT set the table's help back to what it was.
				// Only if auto-validate is enabled should the page go back.
				WorkbenchHelp.setHelp(valWhenBuildButton, ContextIds.VALIDATION_PROPERTIES_PAGE_REBUILD_ENABLED);
			} else {
				//				page.getParent().setFocus();
				validatorList.getTable().setFocus();
				WorkbenchHelp.setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_BUILD_NOVALSELECTED);
			}

			// if the override button is disabled, show its "to enable" text.
			if (overrideGlobalButton.getEnabled()) {
				// Do NOT set the table's help back to what it was.
				// Only if auto-validate is enabled should the page go back.
				boolean doesProjectSupportBuildValidation = ValidatorManager.doesProjectSupportBuildValidation(getProject());
				GlobalConfiguration gp = ConfigurationManager.getManager().getGlobalConfiguration();
				boolean isPrefAuto = gp.isAutoValidate();
				boolean isPrefManual = gp.isBuildValidate();
				if (doesProjectSupportBuildValidation) {
					// Project supports build validation, so it doesn't matter what the preferences
					// are
					WorkbenchHelp.setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED);
				} else if (!doesProjectSupportBuildValidation && (isPrefAuto && isPrefManual)) {
					// Project doesn't support build validation, and the user prefers both auto and
					// manual build validation
					WorkbenchHelp.setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_BOTH);
				} else if (!doesProjectSupportBuildValidation && isPrefAuto) {
					// Project doesn't support build validation, and the user prefers auto build
					// validation
					WorkbenchHelp.setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_AUTO);
				} else if (!doesProjectSupportBuildValidation && isPrefManual) {
					// Project doesn't support build validation, and the user prefers manual build
					// validation
					WorkbenchHelp.setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_MANUAL);
				} else if (!doesProjectSupportBuildValidation && !isPrefAuto && !isPrefManual) {
					// Project doesn't support build validation, but that doesn't matter because the
					// user prefers no build validation.
					WorkbenchHelp.setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED);
				}
			} else {
				validatorList.getTable().setFocus();
				// Preference page doesn't allow projects to override
				WorkbenchHelp.setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_OVERRIDE);
			}
		}

		/*
		 * Store the current values of the controls into the preference store.
		 */
		private void storeValues() throws InvocationTargetException {
			pagePreferences.setDoesProjectOverride(overrideGlobalButton.getSelection());

			if (pagePreferences.doesProjectOverride()) {
				// project override = user's preference + does the global preference allow the
				// project to override

				// If the manual build button is disabled because no validators are selected in the
				// task list, don't overwrite the user's preference.
				if (valWhenBuildButton.isEnabled()) {
					pagePreferences.setBuildValidate(valWhenBuildButton.getSelection());
				}

				// If the auto build button is disabled because no validators are selected in the
				// task list, or because auto-build is disabled, don't overwrite the user's
				// preference.
				if (autoButton.isEnabled()) {
					pagePreferences.setAutoValidate(autoButton.getSelection());
				}

				pagePreferences.setMaximumNumberOfMessages(checkInteger());
				pagePreferences.setEnabledValidators(ValidationConfiguration.convertToArray(validatorList.getCheckedElements()));
			} else {
				pagePreferences.resetToDefault(); // If the project can't or doesn't override,
				// update its values to match the global
				// preference values.
			}

			pagePreferences.passivate();
		}

		/**
		 * Reads the list of validators, enables the validators which are selected, disables the
		 * validators which are not selected, and if the auto-validate checkbox is chosen, performs
		 * a full validation.
		 */
		public boolean performOk() throws InvocationTargetException {
			// addBuilder MUST be called before storeValues
			// addBuilder adds a builder to the project, and that changes the project description.
			// Changing a project's description triggers the validation framework's "natureChange"
			// migration, and a nature change requires that the list of validators be recalculated.
			// If the builder is added after the values are stored, the stored values are
			// overwritten.
			addBuilder();

			// If this method is being called because an APPLY was hit instead of an OK,
			// recalculate the "can build be enabled" status because the builder may have
			// been added in the addBuilder() call above.
			// Also recalculate the values that depend on the isBuilderConfigured value.
			isBuilderConfigured = ValidatorManager.doesProjectSupportBuildValidation(getProject());

			// Persist the values.
			storeValues();

			if (autoButton.getSelection()) {
				int enabledIncrementalValidators = pagePreferences.numberOfEnabledIncrementalValidators();
				int enabledValidators = pagePreferences.numberOfEnabledValidators();
				if (enabledValidators != enabledIncrementalValidators) {
					// Then some of the enabled validators are not incremental
					int iIconType = org.eclipse.swt.SWT.ICON_INFORMATION;
					Display display = Display.getCurrent();
					Shell shell = (display == null) ? null : display.getActiveShell();
					MessageBox messageBox = new MessageBox(shell, org.eclipse.swt.SWT.OK | iIconType);
					messageBox.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_MSSGBOX_TITLE_NONINC));

					ValidatorMetaData[] vmds = pagePreferences.getEnabledValidators();
					StringBuffer buffer = new StringBuffer(NEWLINE_AND_TAB);
					for (int i = 0; i < vmds.length; i++) {
						ValidatorMetaData vmd = vmds[i];

						if (!vmd.isIncremental()) {
							buffer.append(vmd.getValidatorDisplayName());
							buffer.append(NEWLINE_AND_TAB);
						}
					}
					messageBox.setMessage(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_AUTO_ON_NONINC, new String[]{buffer.toString()}));
					messageBox.open();
				}
			}

			if (pagePreferences.hasEnabledValidatorsChanged(oldVmd, false) || ValidatorManager.getManager().isMessageLimitExceeded(getProject())) { // false
				// means
				// that
				// the
				// preference
				// "allow"
				// value
				// hasn't
				// changed
				ValidatorManager.getManager().updateTaskList(getProject()); // Do not remove the
				// exceeded message;
				// only
				// ValidationOperation
				// should do that
				// because it's about to
				// run validation. If
				// the limit is
				// increased, messages
				// may still be missing,
				// so don't remove the
				// "messages may be
				// missing" message.
			}

			return true;
		}

		/**
		 * If the current project doesn't have the validation builder configured on it, add the
		 * builder. Otherwise return without doing anything.
		 */
		private void addBuilder() {
			if (overrideGlobalButton.getSelection()) { // do not add the builder unless the user
				// overrides the preferences
				if (autoButton.getSelection() || valWhenBuildButton.getSelection()) {
					ValidatorManager.addProjectBuildValidationSupport(getProject());
				}
			}
		}

		public Composite getControl() {
			return page;
		}

		public void dispose() {
			maxValProblemsField.dispose();
			maxValProblemsFieldLabel.dispose();
			selectAllButton.dispose();
			deselectAllButton.dispose();
			autoButton.dispose();
			valWhenBuildButton.dispose();
			validatorList.getTable().dispose();
			messageLabel.dispose();
			//			layout.dispose();
			//			data.dispose();
			emptyRowPlaceholder.dispose();
			overrideGlobalButton.dispose();
			page.dispose();
		}
	}

	/**
	 * ValidationPreferencePage constructor comment.
	 */
	public ValidationPropertiesPage() {
		// Some of the initialization is done in the "initialize" method, which is
		// called by the "getPageType" method, because the current project must
		// be known in order to initialize those fields.
	}

	/**
	 * Given a parent (the Properties guide), create the Validators page to be added to it.
	 */
	protected Control createContents(Composite parent) {
		IProject project = getProject();

		if ((project == null) || !project.isOpen()) {
			_pageImpl = new InvalidPage(parent);
		} else {
			try {
				if (ConfigurationManager.getManager().getProjectConfiguration(project).numberOfValidators() == 0) {
					_pageImpl = new NoValidatorsPage(parent);
				} else {
					_pageImpl = new ValidatorListPage(parent);
				}
			} catch (InvocationTargetException exc) {
				_pageImpl = new InvalidPage(parent);
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			} catch (Throwable exc) {
				_pageImpl = new InvalidPage(parent);
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			}
		}

		return _pageImpl.getControl();
	}

	/**
	 * Since the pages are inner classes of a child PreferencePage, not a PreferencePage itself,
	 * DialogPage's automatic disposal of its children's widgets cannot be used. Instead, dispose of
	 * each inner class' widgets explicitly.
	 */
	public void dispose() {
		super.dispose();
		try {
			_pageImpl.dispose();
		} catch (Throwable exc) {
			logError(exc);
		}
	}

	/**
	 * Returns the highlighted item in the workbench.
	 */
	public IProject getProject() {
		Object element = getElement();

		if (element == null) {
			return null;
		}

		if (element instanceof IProject) {
			return (IProject) element;
		}

		return null;
	}

	protected void noDefaultAndApplyButton() {
		super.noDefaultAndApplyButton();
	}

	/**
	 * Performs special processing when this page's Defaults button has been pressed.
	 * <p>
	 * This is a framework hook method for sublcasses to do special things when the Defaults button
	 * has been pressed. Subclasses may override, but should call <code>super.performDefaults</code>.
	 * </p>
	 */
	protected void performDefaults() {
		super.performDefaults();
		try {
			_pageImpl.performDefaults();
		} catch (InvocationTargetException exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
		} catch (Throwable exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
		}
	}

	/**
	 * When the user presses the "OK" or "Apply" button on the Properties Guide/Properties Page,
	 * respectively, some processing is performed by this PropertyPage. If the page is found, and
	 * completes successfully, true is returned. Otherwise, false is returned, and the guide doesn't
	 * finish.
	 */
	public boolean performOk() {
		try {
			return _pageImpl.performOk();
		} catch (InvocationTargetException exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			return false;
		} catch (Throwable exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			return false;
		}
	}

	void logError(Throwable exc) {
		Logger logger = WTPUIPlugin.getLogger();
		if (logger.isLoggingLevel(Level.SEVERE)) {
			LogEntry entry = ValidationUIPlugin.getLogEntry();
			entry.setSourceIdentifier("ValidationPropertiesPage.displayAndLogError"); //$NON-NLS-1$
			entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL_PAGE);
			entry.setTargetException(exc);
			logger.write(Level.SEVERE, entry);

			if (exc instanceof InvocationTargetException) {
				if (((InvocationTargetException) exc).getTargetException() != null) {
					entry.setTargetException(((InvocationTargetException) exc).getTargetException());
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}

	void displayAndLogError(String title, String message, Throwable exc) {
		logError(exc);
		displayMessage(title, message, org.eclipse.swt.SWT.ICON_ERROR);
	}

	private void displayMessage(String title, String message, int iIconType) {
		MessageBox messageBox = new MessageBox(getShell(), org.eclipse.swt.SWT.OK | iIconType | org.eclipse.swt.SWT.APPLICATION_MODAL);
		messageBox.setMessage(message);
		messageBox.setText(title);
		messageBox.open();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#getDefaultsButton()
	 */
	protected Button getDefaultsButton() {
		return super.getDefaultsButton();
	}
}