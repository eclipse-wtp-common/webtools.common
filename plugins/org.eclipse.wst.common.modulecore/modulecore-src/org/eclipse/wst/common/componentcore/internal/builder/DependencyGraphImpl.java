package org.eclipse.wst.common.componentcore.internal.builder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.impl.WTPModulesResourceFactory;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.osgi.framework.Bundle;

public class DependencyGraphImpl implements IDependencyGraph {

	/**
	 * Don't read or write the graph without first obtaining the graphLock.
	 */
	private Object graphLock = new Object();

	/**
	 * If projects A and and B both depend on C an entry in this graph would be {C ->
	 * {A, B} }
	 */
	private Map<IProject, Set<IProject>> graph = null;
	
	private long modStamp = 0;
	
	private ListenerList listeners = new ListenerList();
	
	/**
	 * This is not public; only {@link IDependencyGraph#INSTANCE} should be
	 * used.
	 * 
	 * @return
	 */
	static IDependencyGraph getInstance() {
		if (instance == null) {
			instance = new DependencyGraphImpl();
			instance.initGraph();
		}
		return instance;
	}

	private static DependencyGraphImpl instance = null;

	private DependencyGraphImpl() {
	}

	public long getModStamp() {
		synchronized (graphLock) {
			return modStamp;
		}
	}
	
	private void incrementModStamp(){
		synchronized (graphLock) {
			modStamp++;
		}
	}
	/**
	 * Returns the set of projects whose components reference the specified
	 * target project's component. For example if projects A and B both
	 * reference C. Passing C as the targetProject will return {A, B}
	 */
	public Set<IProject> getReferencingComponents(IProject targetProject) {
		waitForAllUpdates(null);
		synchronized (graphLock) {
			Set<IProject> set = graph.get(targetProject);
			if (set == null) {
				return Collections.EMPTY_SET;
			} else {
				DependencyGraphEvent event = null;
				for (Iterator<IProject> iterator = set.iterator(); iterator.hasNext();) {
					IProject sourceProject = iterator.next();
					if (!sourceProject.isAccessible()) {
						iterator.remove();
						if(event == null){
							incrementModStamp();
							event = new DependencyGraphEvent();
							event.setModStamp(getModStamp());
						}
						event.removeReference(sourceProject, targetProject);
					}
				}
				if(event != null){
					notifiyListeners(event);
				}
				Set<IProject> copy = new HashSet<IProject>();
				copy.addAll(set);
				return copy;
			}
		}
	}

	private class DependencyGraphResourceChangedListener implements IResourceChangeListener, IResourceDeltaVisitor {
		// only registered for post change events
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				preUpdate();
				event.getDelta().accept(this);
			} catch (CoreException e) {
				ModulecorePlugin.logError(e);
			} finally {
				postUpdate();
			}
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (resource.getType()) {
			case IResource.ROOT:
				return true;
			case IResource.PROJECT: {
				int kind = delta.getKind();
				if ((IResourceDelta.ADDED & kind) != 0) {
					update((IProject) resource, IDependencyGraph.ADDED);
					return false;
				} else if ((IResourceDelta.REMOVED & kind) != 0) {
					update((IProject) resource, IDependencyGraph.REMOVED);
					return false;
				} else if ((IResourceDelta.CHANGED & kind) != 0) {
					int flags = delta.getFlags();
					if ((IResourceDelta.OPEN & flags) != 0) {
						boolean isOpen = ((IProject) resource).isOpen();
						if (isOpen) {
							update((IProject) resource, IDependencyGraph.ADDED);
						} else {
							update((IProject) resource, IDependencyGraph.REMOVED);
						}
						return false;
					}
					return true;
				}
				return false;
			}
			case IResource.FOLDER:
				if (resource.getName().equals(IModuleConstants.DOT_SETTINGS)) {
					return true;
				}
				return false;
			case IResource.FILE:
				String name = resource.getName();
				if (name.equals(WTPModulesResourceFactory.WTP_MODULES_SHORT_NAME)) {
					if((delta.getKind() & IResourceDelta.ADDED) != 0){
						update(resource.getProject(), IDependencyGraph.ADDED);
					} else {
						update(resource.getProject(), IDependencyGraph.MODIFIED);
					}
				} else if(name.equals(".project")){
					update(resource.getProject(), IDependencyGraph.ADDED);
				}
			default:
				return false;
			}
		}
	};

	private DependencyGraphResourceChangedListener listener = null;

	/**
	 * The graph is built lazily once. Afterwards, the graph is updated as
	 * necessary.
	 */
	private void initGraph() {
		synchronized (graphLock) {
			try {
				preUpdate();
				graph = new HashMap<IProject, Set<IProject>>();
				listener = new DependencyGraphResourceChangedListener();
				ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
				initAll();
			} finally {
				postUpdate();
			}
		}
	}
	
	private void initAll(){
		synchronized (graphLock) {
			try{
				preUpdate();
				if(restoreGraph() == null){
					rebuild();
				}
			} finally{
				postUpdate();
			}
			
		}
	}

	private void rebuild() {
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		rebuild(allProjects);
	}

	private void rebuild(IProject[] allProjects) {
		for (IProject sourceProject : allProjects) {
			update(sourceProject, IDependencyGraph.ADDED);
		}
	}
	
	private void removeAllReferences(DependencyGraphEvent event){
		synchronized (graphLock) {
			IProject [] allReferenceKeys = new IProject [graph.keySet().size()];
			graph.keySet().toArray(allReferenceKeys);
			for(IProject project : allReferenceKeys){
				removeAllReferences(project, event);
			}
		}
	}

	private void removeAllReferences(IProject targetProject, DependencyGraphEvent event) {
		synchronized (graphLock) {
			boolean removed = false;
			Set<IProject> removedSet = graph.remove(targetProject);
			if(removedSet != null && !removedSet.isEmpty()){
				removed = true;
				for(Iterator<IProject>iterator = removedSet.iterator(); iterator.hasNext();){
					event.removeReference(iterator.next(), targetProject);
				}
			}
			for(Iterator <Entry<IProject,Set<IProject>>>iterator = graph.entrySet().iterator(); iterator.hasNext();){
				Entry<IProject,Set<IProject>> entry = iterator.next();
				if(!entry.getValue().isEmpty() && entry.getValue().remove(targetProject)){
					removed = true;
					event.removeReference(targetProject, entry.getKey());
				}
			}
			if(removed){
				incrementModStamp();
				event.setModStamp(getModStamp());
			}
		}
	}

	private void removeReference(IProject sourceProject, IProject targetProject, DependencyGraphEvent event) {
		synchronized (graphLock) {
			Set<IProject> referencingProjects = graph.get(targetProject);
			if (referencingProjects != null) {
				if(referencingProjects.remove(sourceProject)){
					event.removeReference(sourceProject, targetProject);
					incrementModStamp();
					event.setModStamp(getModStamp());
				}
			}
		}
	}

	private void addReference(IProject sourceProject, IProject targetProject, DependencyGraphEvent event) {
		synchronized (graphLock) {
			Set<IProject> referencingProjects = graph.get(targetProject);
			if (referencingProjects == null) {
				referencingProjects = new HashSet<IProject>();
				graph.put(targetProject, referencingProjects);
			}
			boolean added = referencingProjects.add(sourceProject);
			if(added){
				event.addRefererence(sourceProject, targetProject);
				incrementModStamp();
				event.setModStamp(getModStamp());
			}
		}
	}

	public static final Object GRAPH_UPDATE_JOB_FAMILY = new Object();

	private static final int JOB_DELAY = 100;

	private final GraphUpdateJob graphUpdateJob = new GraphUpdateJob();
	private final Object jobLock = new Object();
	
	//This lock is used for deadlock avoidance.  The specific scenario
	//is during waitForAllUpdates(); if a deadlock is detected during 
	//the lock's acquire() method, this thread will temporarily release
	//its already acquired ILocks to allow other threads to continue.
	//When execution is returned to this thread, any release ILocks will
	//be reacquired before proceeding
	private final ILock jobILock = Job.getJobManager().newLock();

	private class GraphUpdateJob extends Job {

		public GraphUpdateJob() {
			super(Resources.JOB_NAME);
			setSystem(true);
		}

		public boolean belongsTo(Object family) {
			if (family == GRAPH_UPDATE_JOB_FAMILY) {
				return true;
			}
			return super.belongsTo(family);
		}

		// We use the listener list as a thread safe queue.
		private class Queue extends ListenerList {
			public synchronized Object[] getListeners() {
				Object[] data = super.getListeners();
				clear();
				return data;
			}

			public synchronized boolean isEmpty() {
				return super.isEmpty();
			}
		};

		private Queue projectsAdded = new Queue();

		private Queue projectsRemoved = new Queue();

		private Queue projectsUpdated = new Queue();

		public void queueProjectAdded(IProject project) {
			incrementModStamp();
			projectsAdded.add(project);
		}

		public void queueProjectDeleted(IProject project) {
			incrementModStamp();
			projectsRemoved.add(project);
		}

		public void queueProjectUpdated(IProject project) {
			incrementModStamp();
			projectsUpdated.add(project);
		}

		@Override
		public boolean shouldSchedule() {
			boolean isEmpty = projectsAdded.isEmpty() && projectsRemoved.isEmpty() && projectsUpdated.isEmpty();
			return !isEmpty;
		}
		
		private boolean running = false;
		private void setRunning(boolean b) {
			synchronized (runLock) {
				running = b;
				if(running){
					runStamp++;
				}
			}
		}
		
		private boolean isRunning(){
			synchronized (runLock) {
				return running;
			}
		}
		
		private boolean didRun(long stamp){
			synchronized (runLock) {
				return stamp != runStamp;
			}
		}
		
		//keeps track of when each run
		private long runStamp = 0;
		private Object runLock = new Object();
		
		public void waitForRun(long maxWaitTime) {
			if(isRunning()){
				return;
			} else {
				final long startTime = System.currentTimeMillis();
				long localRunStamp = 0;
				synchronized (graphLock) {
					localRunStamp = runStamp;
				}
				while(true){
					if(System.currentTimeMillis() - startTime > maxWaitTime){
						return;
					}
					if(didRun(localRunStamp)){
						return;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
			}
		}
		
		protected IStatus run(IProgressMonitor monitor) {
			try{
				jobILock.acquire();
				setRunning(true);
				
				if(ResourcesPlugin.getPlugin().getBundle().getState() == Bundle.STARTING ){
					graphUpdateJob.schedule(JOB_DELAY);
					return Status.OK_STATUS;
				}
				
				final Object[] removed = projectsRemoved.getListeners();
				final Object[] updated = projectsUpdated.getListeners();
				final Object[] added = projectsAdded.getListeners();
				if (removed.length == 0 && updated.length == 0 && added.length == 0) {
					return Status.OK_STATUS;
				}
				incrementModStamp();
				if(ResourcesPlugin.getPlugin().getBundle().getState() != Bundle.ACTIVE){
					return Status.OK_STATUS;
				}
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						ModulecorePlugin.logError(e);
					}
	
					public void run() throws Exception {
						final DependencyGraphEvent event = new DependencyGraphEvent();
						
						//all references will be rebuilt during an add
						if(added.length == 0){
							// this is the simple case; just remove them all
							synchronized (graphLock) {
								for (Object o : removed) {
									IProject project = (IProject) o;
									removeAllReferences(project, event);
								}
							}
						}
						// get the updated queue in case there are any adds
						// if there are any added projects, then unfortunately the
						// entire workspace needs to be processed
						if (added.length > 0) {
							removeAllReferences(event);
							IProject[] allProjects = null;
							int state = ResourcesPlugin.getPlugin().getBundle().getState();
							if (state == Bundle.ACTIVE) {
								allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
							} else {
								return;
							}
							
							for (IProject sourceProject : allProjects) {
								IVirtualComponent component = ComponentCore.createComponent(sourceProject);
								if (component != null && component instanceof VirtualComponent) {
									((VirtualComponent)component).flushCache();
								}
							}
							
							for (IProject sourceProject : allProjects) {
								IVirtualComponent component = ComponentCore.createComponent(sourceProject);
								if (component != null && component instanceof VirtualComponent) {
									IVirtualReference[] references = ((VirtualComponent)component).getRawReferences();
									for (IVirtualReference ref : references) {
										IVirtualComponent targetComponent = ref.getReferencedComponent();
										if (targetComponent != null) {
											IProject targetProject = targetComponent.getProject();
											if (targetProject != null && !targetProject.equals(sourceProject)) {
												addReference(sourceProject, targetProject, event);
											}
										}
									}	
								}
							}
						} else if (updated.length > 0) {
							IProject[] allProjects = null;
							int state = ResourcesPlugin.getPlugin().getBundle().getState();
							if (state == Bundle.ACTIVE) {
								allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
							} else {
								return;
							}
							
							Set<IProject> validRefs = new HashSet<IProject>();
							for (Object o : updated) {
								IProject sourceProject = (IProject) o;
								IVirtualComponent component = ComponentCore.createComponent(sourceProject);
								if (component != null && component instanceof VirtualComponent) {
									validRefs.clear();
									((VirtualComponent)component).flushCache();
									IVirtualReference[] references = ((VirtualComponent)component).getRawReferences();
									for (IVirtualReference ref : references) {
										IVirtualComponent targetComponent = ref.getReferencedComponent();
										if (targetComponent != null) {
											IProject targetProject = targetComponent.getProject();
											if (targetProject != null && !targetProject.equals(sourceProject)) {
												validRefs.add(targetProject);
											}
										}
									}
									synchronized (graphLock) {
										for (IProject targetProject : allProjects) {
											// if the reference was identified
											// above, be sure to add it
											// otherwise, remove it
											if (validRefs.remove(targetProject)) {
												addReference(sourceProject, targetProject, event);
											} else {
												removeReference(sourceProject, targetProject, event);
											}
										}
									}
								} else {
									// if this project is not a component, then it
									// should be completely removed.
									removeAllReferences(sourceProject, event);
								}
							}
						}
						notifiyListeners(event);
					}
				});
				return Status.OK_STATUS;
			} finally {
				setRunning(false);
				jobILock.release();
			}
		}
	};

	public void addListener(IDependencyGraphListener listener) {
		listeners.add(listener);		
	}
	
	public void removeListener(IDependencyGraphListener listener) {
		listeners.remove(listener);
	}

	private void notifiyListeners(final DependencyGraphEvent event) {
		if(event.getType() == 0){
			return;
		}
		//fire notifications on a different job so they do not block waitForAllUpdates()
		Job notificationJob = new Job(Resources.NOTIFICATION_JOB_NAME){
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				SafeRunner.run(new ISafeRunnable() {
					public void run() throws Exception {
						for(Object listener : listeners.getListeners()){
							((IDependencyGraphListener)listener).dependencyGraphUpdate(event);
						}
						saveGraph(); //trigger a future save
						monitor.done();
					}

					public void handleException(Throwable exception) {
						ModulecorePlugin.logError(exception);
					}
				});
				return Status.OK_STATUS;
			}
		};
		notificationJob.setSystem(true);
		notificationJob.setRule(null);
		notificationJob.schedule();
	}
	
	
	/**
	 * Use: update(project, IDependencyGraph.ADDED);
	 * @deprecated use {@link #update(IProject, int)}
	 */
	public void queueProjectAdded(IProject project) {
		update(project, IDependencyGraph.ADDED);
	}

	/**
	 * Use: update(project, IDependencyGraph.REMOVED);
	 * @deprecated use {@link #update(IProject, int)}
	 */
	public void queueProjectDeleted(IProject project) {
		update(project, IDependencyGraph.REMOVED);
	}

	/**
	 * Use: update(project, IDependencyGraph.MODIFIED);
	 * @deprecated use {@link #update(IProject, int)}
	 */
	public void update(IProject project) {
		update(project, IDependencyGraph.MODIFIED);
	}

	public void update(IProject project, final int updateType){
		switch(updateType){
		case IDependencyGraph.MODIFIED:
			graphUpdateJob.queueProjectUpdated(project);
			break;
		case IDependencyGraph.ADDED:
			graphUpdateJob.queueProjectAdded(project);
			break;
		case IDependencyGraph.REMOVED:
			graphUpdateJob.queueProjectDeleted(project);
			break;
		}
		synchronized (jobLock) {
			if (pauseCount > 0) {
				return;
			}
		}
		graphUpdateJob.schedule(JOB_DELAY);
	}
	
	
	
	private int pauseCount = 0;

	/**
	 * Pauses updates; any caller of this method must ensure through a
	 * try/finally block that resumeUpdates is subsequently called.
	 */
	public void preUpdate() {
		synchronized (jobLock) {
			pauseCount++;
		}
	}

	public void postUpdate() {
		synchronized (jobLock) {
			if (pauseCount > 0) {
				pauseCount--;
			}
			if (pauseCount > 0) {
				return;
			}
		}
		graphUpdateJob.schedule(JOB_DELAY);
	}

	public void waitForAllUpdates(IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor);
		subMonitor.subTask(Resources.WAITING);
		
		Job currentJob = Job.getJobManager().currentJob();
		if (currentJob != graphUpdateJob) {
			if(Job.getJobManager().isSuspended()){
				Job.getJobManager().resume();
			}
			while(isUpdateNecessary()){
				if(subMonitor.isCanceled()){
					throw new OperationCanceledException();
				}
				
				// Ensure any pending work has caused the job to become scheduled
				if(graphUpdateJob.shouldSchedule()){
					graphUpdateJob.schedule();
				}
				// Wake up any sleeping jobs since we're going to wait on the job, 
				// there is no sense to wait for a sleeping job
				graphUpdateJob.wakeUp();
				if(currentJob != null && !ResourcesPlugin.getWorkspace().isTreeLocked()){
					currentJob.yieldRule(subMonitor.newChild(100));
				}
				
				graphUpdateJob.waitForRun(500);
				
				boolean interrupted = false;
				try {
					if(jobILock.acquire(500)){
						jobILock.release();
						//only exit if the job has had a chance to run
						if(!isUpdateNecessary()){
							break;	
						}
					}
				} catch (InterruptedException e) {
					interrupted = true;
				} finally{
					if(interrupted){
						// Propagate interrupts
						Thread.currentThread().interrupt();
					}
				}
			}
		}		
		if(null != monitor){
			monitor.done();
		}
	}

	// necessary if the job is running, waiting, or sleeping
	// or there is anything in the graphqueue
	private boolean isUpdateNecessary() {
		return graphUpdateJob.getState() != Job.NONE || graphUpdateJob.shouldSchedule();
	}

	private class RestoredGraphResults {
		private DependencyGraphEvent event;
		private HashMap <String, Set<String>>graph;
	}
	
	/**
	 * This file will be stored here:
	 * .metadata\.plugins\org.eclipse.wst.common.modulecore\dependencyCache.index
	 */
	private static final String DEPENDENCY_GRAPH_CACHE = "dependencyCache.index";
	
	/**
	 * Restores the graph if possible and returns a {@link RestoredGraphResults}
	 * if successful.
	 * 
	 * It is essential this method do everything possible to avoid restoring bad
	 * data to ensure bad data does not corrupt the current workspace instance. If
	 * bad data is detected, it is deleted to avoid a repeat failure the next
	 * time the workspace is restarted.
	 * 
	 * @return {@link RestoredGraphResults} if successful in restoring the
	 *         graph, <code>null</code> if not successful
	 */
	private RestoredGraphResults restoreGraph(){
		try {
			synchronized (graphLock) {
				graph = new HashMap<IProject, Set<IProject>>();
				IPath stateLocation = ModulecorePlugin.getDefault().getStateLocation();
				java.io.File file = stateLocation.append(DEPENDENCY_GRAPH_CACHE).toFile();
				if(!file.exists()){
					return null; // no state to restore from
				} else {
					HashMap <String, Set<String>>savedMap = null;
					FileInputStream fIn = null;
					try{ 
						fIn = new FileInputStream(file);
						BufferedInputStream bIn = new BufferedInputStream(fIn);
						ObjectInputStream oIn = new ObjectInputStream(bIn);
						savedMap = (HashMap<String, Set<String>>)oIn.readObject();
						oIn.close();
					} catch (FileNotFoundException e) {
						ModulecorePlugin.logError(e);
						return null;
					} catch (IOException e) {
						ModulecorePlugin.logError(e);
						file.delete();
						return null;
					} catch (ClassNotFoundException e) {
						ModulecorePlugin.logError(e);
						file.delete();
						return null;
					} finally{
						if(fIn != null){
							try {
								fIn.close();
							} catch (IOException e) {
								ModulecorePlugin.logError(e);
							}
						}
					}
					if(savedMap != null){ // we have something to restore the state from
						//first check to ensure all projects are still present
						IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot(); 
						for(String sourceProjectName:savedMap.keySet()){
							IProject sourceProject = root.getProject(sourceProjectName);
							if(!sourceProject.exists()){
								return null;
							} else {
								Set <String> targetProjectNames = savedMap.get(sourceProjectName);
								for(String targetProjectName:targetProjectNames){
									IProject targetProject = root.getProject(targetProjectName);
									if(!targetProject.exists()){
										return null;
									}
								}
							}
						}
						
						//next add the references
						DependencyGraphEvent event = new DependencyGraphEvent();
						incrementModStamp();
						event = new DependencyGraphEvent();
						event.setModStamp(getModStamp());
						
						Set <Entry<String, Set<String>>> entries = savedMap.entrySet();
						for(Iterator<Entry<String, Set<String>>> iterator = entries.iterator(); iterator.hasNext();){
							Entry<String, Set<String>> entry = iterator.next();
							IProject sourceProject = root.getProject(entry.getKey());
							for(String targetProjectName : entry.getValue()){
								IProject targetProject = root.getProject(targetProjectName);
								addReference(targetProject, sourceProject, event);
							}
						}
						
						RestoredGraphResults results = new RestoredGraphResults();
						results.event = event;
						results.graph = savedMap;
						
						//finally ensure the results are accurate
						checkRestoredResults(results);
						return results;
					}
				}
			}
		} catch (Exception e){
			try{
				ModulecorePlugin.logError(e);
				IPath stateLocation = ModulecorePlugin.getDefault().getStateLocation();
				java.io.File file = stateLocation.append(DEPENDENCY_GRAPH_CACHE).toFile();
				file.delete();
			} catch (Exception e2){
				//eat it
			}
		}
		return null;
	}
	
	
	private class PersistJob extends Job {

		public PersistJob() {
			super(Resources.GRAPH_SAVE_JOB_NAME);
			setSystem(true);
			setRule(null);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			SafeRunner.run(new ISafeRunnable() {
				public void run() throws Exception {
					persist();
				}

				public void handleException(Throwable exception) {
					ModulecorePlugin.logError(exception);
				}
			});
			return Status.OK_STATUS;
		}
	}

	private static int persistDelay = 60000; // 1 minute
	private PersistJob persistJob = new PersistJob();

	/**
	 * Farm out the I/O to a job to minimize the necessary processing.
	 */
	private void saveGraph() {
		persistJob.schedule(persistDelay);
	}
	
	private void persist(){
		HashMap <String, Set<String>> savedMap = new HashMap<String, Set<String>>();
		synchronized (graphLock) {
			for(IProject sourceProject:graph.keySet()){
				Set <String> savedTargets = new HashSet<String>();
				for(IProject targetProject:graph.get(sourceProject)){
					savedTargets.add(targetProject.getName());
				}
				savedMap.put(sourceProject.getName(), savedTargets);
			}
		}
		IPath stateLocation = ModulecorePlugin.getDefault().getStateLocation();
		java.io.File file = stateLocation.append(DEPENDENCY_GRAPH_CACHE).toFile();
		if(savedMap.isEmpty()){
			//if there is nothing to persist, delete the file.
			if(file.exists()){
				file.delete(); 
			}
		} else {
			FileOutputStream fOut = null;
			try{
				fOut = new FileOutputStream(file);
				BufferedOutputStream bOut = new BufferedOutputStream(fOut);
				ObjectOutputStream oOut = new ObjectOutputStream(bOut);
				oOut.writeObject(savedMap);
				oOut.close();
			} catch (FileNotFoundException e) {
				ModulecorePlugin.logError(e);
			} catch (IOException e) {
				ModulecorePlugin.logError(e);
			} finally{
				if(fOut != null){
					try {
						fOut.close();
					} catch (IOException e) {
						ModulecorePlugin.logError(e);
					}
				}
			}
		}
	}

	private void checkRestoredResults(final RestoredGraphResults restoredGraphResults) {
		Job checkRestoreDataJob = new Job(Resources.CHECK_GRAPH_RESTORE_JOB_NAME){
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				SafeRunner.run(new ISafeRunnable() {
					public void run() throws Exception {
						try {
							IProject[] allProjects = null;
							int state = ResourcesPlugin.getPlugin().getBundle().getState();
							if (state == Bundle.ACTIVE) {
								allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
							} else {
								return;
							}
							if(isStale(allProjects)){
								rebuild(allProjects);
								saveGraph(); //trigger a future save
							}
						} finally {
							monitor.done();
						}				
					}
					private boolean isStale(IProject[] allProjects){
						if(restoredGraphResults.event.getModStamp() != getModStamp()){
							return true;
						}
						
						HashMap<String, Set<String>> computedGraph = new HashMap<String, Set<String>>();
						
						for (IProject sourceProject : allProjects) {
							if(restoredGraphResults.event.getModStamp() != getModStamp()){
								return true;
							}
							IVirtualComponent component = ComponentCore.createComponent(sourceProject);
							if (component != null && component instanceof VirtualComponent) {
								IVirtualReference[] references = ((VirtualComponent)component).getRawReferences();
								for (IVirtualReference ref : references) {
									if(restoredGraphResults.event.getModStamp() != getModStamp()){
										return true;
									}
									IVirtualComponent targetComponent = ref.getReferencedComponent();
									if (targetComponent != null) {
										IProject targetProject = targetComponent.getProject();
										if (targetProject != null && !targetProject.equals(sourceProject)) {
											String targetProjectName = targetProject.getName();
											String sourceProjectName = sourceProject.getName();
											Set <String> targetProjects = computedGraph.get(targetProjectName);
											if(targetProjects == null){
												targetProjects = new HashSet <String>();
												computedGraph.put(targetProjectName, targetProjects);
											}
											targetProjects.add(sourceProjectName);
										}
									}
								}	
							}
						}
						if(restoredGraphResults.event.getModStamp() != getModStamp()){
							return true;
						}
						if(!restoredGraphResults.graph.equals(computedGraph)){
							return true;
						}
						if(restoredGraphResults.event.getModStamp() != getModStamp()){
							return true;
						}
						return false;
					}

					public void handleException(Throwable exception) {
						ModulecorePlugin.logError(exception);
					}
				});
				return Status.OK_STATUS;
			}
		};
		checkRestoreDataJob.setSystem(true);
		checkRestoreDataJob.setRule(null);
		checkRestoreDataJob.schedule();
	}
			
	public String toString() {
		synchronized (graphLock) {
			StringBuffer buff = new StringBuffer("Dependency Graph:\n{\n");
			for (Iterator<Map.Entry<IProject, Set<IProject>>> iterator = graph.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<IProject, Set<IProject>> entry = iterator.next();
				buff.append("  " + entry.getKey().getName() + " -> {");
				for (Iterator<IProject> mappedProjects = entry.getValue().iterator(); mappedProjects.hasNext();) {
					buff.append(mappedProjects.next().getName());
					if (mappedProjects.hasNext()) {
						buff.append(", ");
					}
				}
				buff.append("}\n");
			}
			buff.append("}");
			return buff.toString();
		}

	}
	
	public static final class Resources extends NLS {
	    public static String WAITING;
	    public static String JOB_NAME;
	    public static String NOTIFICATION_JOB_NAME;
	    public static String CHECK_GRAPH_RESTORE_JOB_NAME;
	    public static String GRAPH_SAVE_JOB_NAME;
	    
	    static
	    {
	        initializeMessages( DependencyGraphImpl.class.getName(), Resources.class );
	    }
	}

}
