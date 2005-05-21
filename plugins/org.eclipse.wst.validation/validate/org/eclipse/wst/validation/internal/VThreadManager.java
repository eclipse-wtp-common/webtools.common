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
package org.eclipse.wst.validation.internal;


import java.util.Vector;
import java.util.logging.Level;

import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;


/**
 * This class manages (queue, invoke, etc.) the Runnables that perform the validation for a
 * particular validator.
 */
public class VThreadManager {
	private static VThreadManager _manager = null;
	private static final int MAX_NUM_OF_RESTART = 5; // the maximum number of times that the thread
	// should attempt to ignore a Throwable and
	// carry on

	private Thread _validationThread = null; // This thread checks if the current Runnable is
	// finished, and if so, loads the first Runnable off of
	// the queue and starts it.
	volatile int restart = 0; // how many times has the thread been restarted?
	volatile Jobs _jobs = null;

	private VThreadManager() {
		_jobs = new Jobs();

		// Start the validation thread to check for queued ValidationOperation
		Runnable validationRunnable = new Runnable() {
			public void run() {
				while (true) {
					try {
						if (restart > MAX_NUM_OF_RESTART) {
							// something has gone seriously, seriously wrong
							Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
							if (logger.isLoggingLevel(Level.SEVERE)) {
								LogEntry entry = ValidationPlugin.getLogEntry();
								entry.setSourceID("VThreadManager::validationRunnable"); //$NON-NLS-1$
								entry.setText("restart = " + restart); //$NON-NLS-1$
								logger.write(Level.SEVERE, entry);
							}
							break;
						}

						Runnable job = getJobs().dequeue(); // If currentRunnable is null, there's
						// nothing on the queue. Shouldn't
						// happen with a semaphore.
						if (job != null) {
							getJobs().setActive(true);
							job.run();
							getJobs().setActive(false);
						}
					} catch (Throwable exc) {
						// This exception is added as FINE instead of SEVERE because it's not
						// improbable
						// that an exception will be thrown
						restart++;
						getJobs().setActive(false);

						Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
						if (logger.isLoggingLevel(Level.FINE)) {
							LogEntry entry = ValidationPlugin.getLogEntry();
							entry.setSourceID("VThreadManager::validationRunnable"); //$NON-NLS-1$
							entry.setTargetException(exc);
							logger.write(Level.FINE, entry);
						}
					} finally {
						//do nothing
					}
				}
			}
		};

		_validationThread = new Thread(validationRunnable, "ValidationThread"); //$NON-NLS-1$
		_validationThread.start();
	}

	public static VThreadManager getManager() {
		if (_manager == null) {
			_manager = new VThreadManager();
		}
		return _manager;
	}

	Jobs getJobs() {
		return _jobs;
	}

	public void queue(Runnable runnable) {
		getJobs().queue(runnable);
	}

	/**
	 * Return true if all of the Runnables have been run.
	 */
	public boolean isDone() {
		return getJobs().isDone();
	}

	private class Jobs {
		private Vector __jobs = null; // The queued Runnables that need to be run.
		private boolean _isActive = false; // Is a job being run in the validation thread?

		public Jobs() {
			__jobs = new Vector();
		}

		public synchronized void queue(Runnable runnable) {
			// If there is a thread running already, then it must finish before another validation
			// thread is launched, or the validation messages could reflect the last thread
			// finished,
			// instead of the last state of changes.

			// Have to wait for the current Runnable to finish, so add this to the end of the queue
			__jobs.add(runnable);
			notifyAll();
		}

		/**
		 * Pop the Runnable off of the head of the queue.
		 */
		synchronized Runnable dequeue() {
			while (__jobs.size() == 0) {
				try {
					wait();
				} catch (InterruptedException exc) {
					//Ignore
				}
			} // Block on the semaphore; break when a job has been added to the queue.

			Runnable job = null;
			if (__jobs.size() > 0) {
				job = (Runnable) __jobs.get(0);
				if (job != null) {
					__jobs.remove(0);
				}
			}
			return job;
		}

		public synchronized boolean isActive() {
			return _isActive;
		}

		public synchronized void setActive(boolean active) {
			_isActive = active;
		}

		/**
		 * Return true if all of the Runnables have been run.
		 */
		public synchronized boolean isDone() {
			return ((__jobs.size() == 0) && !isActive());
		}
	}
}