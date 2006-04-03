/**
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.wst.validation.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;

/**
 * A generic save files dialog. The bulk of the code
 * for this dialog was taken from the JDT refactoring
 * support in org.eclipse.jdt.internal.ui.refactoring.RefactoringSaveHelper.
 * This class is a good candidate for reuse amoung components.
 */
public class SaveFilesDialog extends ListDialog {
	
	public SaveFilesDialog(Shell parent)
	{
	  super(parent);
	  setTitle(ValidationUIMessages.SaveFilesDialog_save_all_resources); 
	  setAddCancelButton(true);
	  setLabelProvider(createDialogLabelProvider());
	  setMessage(ValidationUIMessages.SaveFilesDialog_must_save); 
	  setContentProvider(new ListContentProvider());
	}

	protected Control createDialogArea(Composite container) 
	{
		Composite result= (Composite) super.createDialogArea(container);
		boolean fAllowSaveAlways = true;
		if (fAllowSaveAlways) {
			final Button check= new Button(result, SWT.CHECK);
			check.setText(ValidationUIMessages.SaveFilesDialog_always_save); 
			//check.setSelection(RefactoringSavePreferences.getSaveAllEditors());
			check.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
				  try
				  {
				    GlobalConfiguration config = ConfigurationManager.getManager().getGlobalConfiguration();
				    config.setSaveAutomatically(check.getSelection());
				    config.store();
				  }
				  catch(InvocationTargetException exc)
				  {
					Logger.getLogger(ValidationUIPlugin.getBundleName()).log(Level.WARNING, "Unable to set save automatically preference in save files for validation dialog: " + exc);
				  }
				  
				}
			});
			applyDialogFont(result);
		}
		return result;
	}
	
	private ILabelProvider createDialogLabelProvider() {
		return new LabelProvider() {
			public Image getImage(Object element) {
				return ((IEditorPart) element).getTitleImage();
			}
			public String getText(Object element) {
				return ((IEditorPart) element).getTitle();
			}
		};
	}	
}
