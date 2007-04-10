/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.customize.DrawerEntryPage;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.IHelpContextIds;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawer;
import org.eclipse.wst.common.snippets.internal.util.VisibilityUtil;

public class SnippetDrawerEntryPage extends DrawerEntryPage {

	class ContentTypeLabelProvider implements ILabelProvider {

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			String text = ""; //$NON-NLS-1$

			if (element != null && element instanceof IContentType)
				text = ((IContentType) element).getName();

			return text;
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	class ContentTypeSelectionDialog extends ElementListSelectionDialog {
		public ContentTypeSelectionDialog(Shell parent, ILabelProvider renderer) {
			super(parent, renderer);
		}
	}

	class ContentTypeStructuredContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			IContentTypeManager manager = Platform.getContentTypeManager();
			IContentType[] contentTypes = manager.getAllContentTypes();
			Arrays.sort(contentTypes, new Comparator() {
				public int compare(Object arg0, Object arg1) {
					return ((IContentType) arg0).getName().compareTo(((IContentType) arg1).getName());
				}
			});

			return contentTypes;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected Button fAlwaysHideButton;
	protected Button fAlwaysShowButton;
	protected Button fBrowseButton;
	protected Object[] fContentTypes = new Object[0];
	protected Table fContentTypeTable = null;
	protected Text fContentTypeText;
	protected Button fCustomButton;
	protected Table fFileTypeTable = null;
	protected List fFileTypeTableItems = new ArrayList();
	protected List fImagesToDispose = new ArrayList();

	protected void browsePressed() {
		ListSelectionDialog dialog = new ListSelectionDialog(getControl().getShell(), Platform.getContentTypeManager(), new ContentTypeStructuredContentProvider(), new ContentTypeLabelProvider(), null);
		dialog.setTitle(SnippetsMessages.SnippetDrawerEntryPage_5); //$NON-NLS-1$
		dialog.setMessage(SnippetsMessages.SnippetDrawerEntryPage_6); //$NON-NLS-1$
		ArrayList initialSelections = new ArrayList();
		IContentType[] contentTypes = Platform.getContentTypeManager().getAllContentTypes();
		ISnippetsEntry snippetEntry = (ISnippetsEntry) getEntry();
		String[] filters = snippetEntry.getFilters();
		IContentTypeManager manager = Platform.getContentTypeManager();

		if (filters.length >= 1) {
			String firstFilter = filters[0];
			if (firstFilter.compareTo("-") != 0) { //$NON-NLS-1$
				if (firstFilter.compareTo("*") == 0) { //$NON-NLS-1$
					for (int i = 0; i < contentTypes.length; i++) {
						initialSelections.add(contentTypes[i]);
					}
				}
				else {
					for (int i = 0; i < contentTypes.length; i++) {
						for (int j = 0; j < filters.length; j++) {
							IContentType filterContentType = manager.getContentType(filters[j]);
							if (filterContentType != null) {
								if (contentTypes[i].getName().compareTo(filterContentType.getName()) == 0) {
									initialSelections.add(contentTypes[i]);
									break;
								}
							}
						}
					}
				}
			}
		}
		dialog.setInitialSelections(initialSelections.toArray());

		// in order to set the infopop for the content type selection dialog,
		// create the dialog
		// first to get the dialog shell and set the infopop on it
		dialog.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IHelpContextIds.DIALOG_CONTENT_TYPE_SELECTION);

		if (dialog.open() == Window.OK) {
			fContentTypes = dialog.getResult();
			refreshContentTypeText();
			refreshEntryFilters();
		}
	}

	protected Composite createContentTypeRadios(Composite panel) {
		Composite radioGroup = new Composite(panel, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		radioGroup.setLayout(layout);
		GridData radioGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		radioGroupGridData.horizontalIndent = 15;
		radioGroup.setLayoutData(radioGroupGridData);
		SelectionListener updateEnablement = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
				refreshEntryFilters();
			}
		};

		fAlwaysShowButton = new Button(radioGroup, SWT.RADIO);
		fAlwaysShowButton.setText(SnippetsMessages.SnippetDrawerEntryPage_1); //$NON-NLS-1$
		fAlwaysShowButton.setSelection(true);
		fAlwaysShowButton.addSelectionListener(updateEnablement);

		fAlwaysHideButton = new Button(radioGroup, SWT.RADIO);
		fAlwaysHideButton.setText(SnippetsMessages.SnippetDrawerEntryPage_2); //$NON-NLS-1$
		fAlwaysHideButton.addSelectionListener(updateEnablement);

		fCustomButton = new Button(radioGroup, SWT.RADIO);
		fCustomButton.setText(SnippetsMessages.SnippetDrawerEntryPage_3); //$NON-NLS-1$
		fCustomButton.addSelectionListener(updateEnablement);

		return radioGroup;
	}

	protected Composite createContentTypeTextRow(Composite panel) {
		Composite contentTypeTextRow = new Composite(panel, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		layout.numColumns = 2;
		contentTypeTextRow.setLayout(layout);
		GridData contentTypeTextRowGridData = new GridData(GridData.FILL_HORIZONTAL);
		contentTypeTextRowGridData.horizontalIndent = 33;
		contentTypeTextRowGridData.widthHint = 0;
		contentTypeTextRow.setLayoutData(contentTypeTextRowGridData);

		fContentTypeText = new Text(contentTypeTextRow, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		fContentTypeText.setEnabled(false);
		GridData contentTypeTextGridData = new GridData(GridData.FILL_HORIZONTAL);
		fContentTypeText.setLayoutData(contentTypeTextGridData);

		fBrowseButton = new Button(contentTypeTextRow, SWT.PUSH);
		fBrowseButton.setText(SnippetsMessages.SnippetDrawerEntryPage_4); //$NON-NLS-1$
		fBrowseButton.setEnabled(false);
		GridData browseButtonGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		fBrowseButton.setLayoutData(browseButtonGridData);
		fBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browsePressed();
			}
		});

		return contentTypeTextRow;
	}

	public void createControl(Composite parent, PaletteEntry entry) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.DIALOG_EDIT_CATEGORY);
		super.createControl(parent, entry);
		Composite panel = (Composite) getControl();
		Control[] tablist = new Control[panel.getTabList().length + 2];
		System.arraycopy(panel.getTabList(), 0, tablist, 0, tablist.length - 2);

		createLabel(panel, SWT.NONE, SnippetsMessages.SnippetDrawerEntryPage_0); //$NON-NLS-1$
		tablist[tablist.length - 2] = createContentTypeRadios(panel);

		tablist[tablist.length - 1] = createContentTypeTextRow(panel);

		initContentTypeOption();

		panel.setTabList(tablist);

		panel.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (fImagesToDispose != null) {
					for (Iterator iterator = fImagesToDispose.iterator(); iterator.hasNext();) {
						((Image) iterator.next()).dispose();
					}
					fImagesToDispose = null;
				}
			}
		});
	}

	protected void initContentTypeOption() {
		ISnippetsEntry snippetEntry = (ISnippetsEntry) getEntry();
		String[] filters = snippetEntry.getFilters();

		if (filters == null)
			fAlwaysShowButton.setSelection(true);
		else if (filters.length >= 1) {
			String firstFilter = filters[0];
			if (firstFilter.compareTo("*") == 0) { //$NON-NLS-1$
				fAlwaysShowButton.setSelection(true);
				fAlwaysHideButton.setSelection(false);
				fCustomButton.setSelection(false);
				fContentTypeText.setEnabled(false);
				fBrowseButton.setEnabled(false);

				fContentTypeText.setText(""); //$NON-NLS-1$
			}
			else if (firstFilter.compareTo("!") == 0) { //$NON-NLS-1$
				fAlwaysShowButton.setSelection(false);
				fAlwaysHideButton.setSelection(true);
				fCustomButton.setSelection(false);
				fContentTypeText.setEnabled(false);
				fBrowseButton.setEnabled(false);

				fContentTypeText.setText(""); //$NON-NLS-1$
			}
			else if (firstFilter.compareTo("-") == 0) { //$NON-NLS-1$
				fAlwaysShowButton.setSelection(false);
				fAlwaysHideButton.setSelection(false);
				fCustomButton.setSelection(true);
				fContentTypeText.setEnabled(true);
				fBrowseButton.setEnabled(true);

				fContentTypeText.setText(""); //$NON-NLS-1$
			}
			else {
				fAlwaysShowButton.setSelection(false);
				fAlwaysHideButton.setSelection(false);
				fCustomButton.setSelection(true);
				fContentTypeText.setEnabled(true);
				fBrowseButton.setEnabled(true);

				IContentTypeManager manager = Platform.getContentTypeManager();
				String text = ""; //$NON-NLS-1$
				for (int i = 0; i < filters.length; i++) {
					IContentType contentType = manager.getContentType(filters[i]);
					if (contentType != null) {
						if (text.length() > 0)
							text += ","; //$NON-NLS-1$
						text += contentType.getName();
					}
				}
				fContentTypeText.setText(text);
			}
		}
	}

	protected void refreshContentTypeText() {
		switch (fContentTypes.length) {
			case 0 :
				fContentTypeText.setText(""); //$NON-NLS-1$
				break;

			case 1 :
				fContentTypeText.setText(((IContentType) fContentTypes[0]).getName());
				break;

			default :
				String text = ""; //$NON-NLS-1$
				for (int i = 0; i < fContentTypes.length; i++) {
					if (text.length() > 0)
						text += ","; //$NON-NLS-1$
					text += ((IContentType) fContentTypes[i]).getName();
				}
				fContentTypeText.setText(text);
				break;
		}
	}

	protected void refreshEntryFilters() {
		SnippetPaletteDrawer snippetDrawer = (SnippetPaletteDrawer) getEntry();
		if (fAlwaysShowButton.getSelection()) {
			snippetDrawer.setFilters(new String[]{"*"}); //$NON-NLS-1$
			snippetDrawer.setVisible(true);
		}
		else if (fAlwaysHideButton.getSelection()) {
			snippetDrawer.setFilters(new String[]{"!"}); //$NON-NLS-1$
			snippetDrawer.setVisible(false);
		}
		else {
			if (fContentTypes.length == 0) {
				snippetDrawer.setFilters(new String[]{"-"}); //$NON-NLS-1$
				snippetDrawer.setVisible(false);
			}
			else {
				String[] filters = new String[fContentTypes.length];
				for (int i = 0; i < fContentTypes.length; i++) {
					filters[i] = ((IContentType) fContentTypes[i]).getId();
				}

				boolean visible = false;
				IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if (activeEditor == null)
					visible = true;
				else {
					IEditorInput input = activeEditor.getEditorInput();
					visible = VisibilityUtil.isContentType(input, filters);
				}

				snippetDrawer.setFilters(filters);
				snippetDrawer.setVisible(visible);
			}
		}
	}

	protected void updateEnablement() {
		if (fAlwaysShowButton.getSelection()) {
			fAlwaysShowButton.setSelection(true);
			fAlwaysHideButton.setSelection(false);
			fCustomButton.setSelection(false);
			fContentTypeText.setEnabled(false);
			fBrowseButton.setEnabled(false);

			fContentTypeText.setText(""); //$NON-NLS-1$
		}
		else if (fAlwaysHideButton.getSelection()) {
			fAlwaysShowButton.setSelection(false);
			fAlwaysHideButton.setSelection(true);
			fCustomButton.setSelection(false);
			fContentTypeText.setEnabled(false);
			fBrowseButton.setEnabled(false);

			fContentTypeText.setText(""); //$NON-NLS-1$
		}
		else {
			fAlwaysShowButton.setSelection(false);
			fAlwaysHideButton.setSelection(false);
			fCustomButton.setSelection(true);
			fContentTypeText.setEnabled(true);
			fBrowseButton.setEnabled(true);

			refreshContentTypeText();
		}
	}
}
