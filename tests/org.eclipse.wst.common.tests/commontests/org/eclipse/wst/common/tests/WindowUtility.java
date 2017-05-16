/*
 * Created on Jun 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.tests;


/**
 * @author jsholl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WindowUtility {

//    public static void closeAllOpenWindows() {
//        IWorkbench workbench = WorkbenchPlugin.getDefault().getWorkbench();
//        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
//
//        for (int i = 0; i < windows.length; i++) {
//            final IWorkbenchWindow window = windows[i];
//            final Shell shell = window.getShell();
//            shell.getDisplay().syncExec(new Runnable() {
//                public void run() {
//                    Shell[] otherShells = shell.getDisplay().getShells();
//                    //this can be imporoved, but basically this is to work out shell dependencies.
//                    for (int j = 0; j < otherShells.length; j++) {
//                        //step through shells backwards in case one shell opens another -- this is how they are usually odered
//                        for (int i = otherShells.length - 1; i > 0; i--) {
//                        	int index = (i+j) % otherShells.length; // mix things up a little to work out shell dependencies
//                            if (otherShells[index] != shell && otherShells[index].isVisible() && !otherShells[index].isDisposed()) {
//                                otherShells[index].close();
//                            }
//                        }
//                    }
//                }
//            });
//        }
//    }

}
