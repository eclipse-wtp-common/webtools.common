/*
 * Created on Jun 30, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.tests;

import java.util.ArrayList;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/**
 * @author jsholl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LogUtility implements ILogListener {

    private static LogUtility instance = new LogUtility();
    private ArrayList loggedMessages = new ArrayList();
    private boolean logging = false;

    private LogUtility() {
        registerPlugins();
    }

    private void registerPlugins() {
//      TODO DCB Disable for now due to other plugins failing.
//        IExtensionRegistry registry = Platform.getPluginRegistry();
//        IPluginDescriptor[] descriptors = registry.getPluginDescriptors();
//        for (int i = 0; i < descriptors.length; i++) {
//            try {
//                Plugin plugin = descriptors[i].getPlugin();
//                ILog log = plugin.getLog();
//                log.addLogListener(this);
//            } catch (Exception e) {
//            }
//        }
    }

    public static LogUtility getInstance() {
        return instance;
    }

    public void clearLogs() {
        loggedMessages.clear();
    }

    public void resetLogging() {
        stopLogging();
        clearLogs();
        startLogging();
    }

    public void startLogging() {
        logging = true;
    }

    public void stopLogging() {
        logging = false;
    }

    public void verifyNoWarnings() {
    	//TODO DCB Disable for now due to other plugins failing.
//        String warnings = "";
//        for (int i = 0; i < loggedMessages.size(); i++) {
//            IStatus status = (IStatus) loggedMessages.get(i);
//            if (status.getSeverity() == IStatus.WARNING || status.getSeverity() == IStatus.ERROR) {
//                warnings += "\nLogUtility: " + ((status.getSeverity() == IStatus.WARNING) ? "WARNING " : "ERROR ");
//                warnings += "\nFrom plugin: " + ((null != status.getPlugin()) ? status.getPlugin() : "null");
//                warnings += "\nMessage: " + ((null != status.getMessage()) ? status.getMessage() : "null");
//                warnings += "\nStack:\n";
//                try {
//                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                    status.getException().printStackTrace(new PrintStream(outputStream));
//                    warnings += outputStream.toString();
//                } catch (Exception e) {
//                    warnings += " Stack not available";
//                }
//
//            }
//        }
//        if (!warnings.equals("")) {
//            Assert.fail(warnings);
//        }
    }

    public void logging(IStatus status, String plugin) {
        if (logging) {
            loggedMessages.add(status);
        }
    }
}
