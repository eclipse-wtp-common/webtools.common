package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.impl.WTPModulesResourceFactory;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class DependencyGraphImpl implements IDependencyGraph {

	/**
	 * Don't read or write the graph without first obtaining the graphLock.
	 */
	private Object graphLock = new Object();

	/**
	 * If projects and and B both depend on C an entry in this graph would be {C ->
	 * {A, B} }
	 */
	private Map<IProject, Set<IProject>> graph = null;

	private long modStamp = 0;

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

	public Set<IProject> getReferencingComponents(IProject targetProject) {
		waitForAllUpdates(null);
		synchronized (graphLock) {
			Set<IProject> set = graph.get(targetProject);
			if (set == null) {
				return Collections.EMPTY_SET;
			} else {
				for (Iterator<IProject> iterator = set.iterator(); iterator.hasNext();) {
					IProject project = iterator.next();
					if (!project.isAccessible()) {
						iterator.remove();
					}
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
					queueProjectAdded((IProject) resource);
					return false;
				} else if ((IResourceDelta.REMOVED & kind) != 0) {
					queueProjectDeleted((IProject) resource);
					return false;
				} else if ((IResourceDelta.CHANGED & kind) != 0) {
					int flags = delta.getFlags();
					if ((IResourceDelta.OPEN & flags) != 0) {
						boolean isOpen = ((IProject) resource).isOpen();
						if (isOpen) {
							queueProjectAdded((IProject) resource);
						} else {
							queueProjectDeleted((IProject) resource);
						}
						return false;
					}
					return true;
				}
				return false;
			}
			case IResource.FOLDER:
				if (resource.getName().equals(".settings")) {
					return true;
				}
				return false;
			case IResource.FILE:
				String name = resource.getName();
				if (name.equals(WTPModulesResourceFactory.WTP_MODULES_SHORT_NAME)) {
					update(resource.getProject());
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
				IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (IProject sourceProject : allProjects) {
					queueProjectAdded(sourceProject);
				}
			} finally {
				postUpdate();
			}
		}
	}

	private void removeAllReferences(IProject project) {
		synchronized (graphLock) {
			graph.remove(project);
			for (Iterator<Set<IProject>> iterator = graph.values().iterator(); iterator.hasNext();) {
				iterator.next().remove(project);
			}
			modStamp++;
		}
	}

	private void removeReference(IProject sourceProject, IProject targetProject) {
		synchronized (graphLock) {
			Set<IProject> referencingProjects = graph.get(targetProject);
			if (referencingProjects != null) {
				referencingProjects.remove(sourceProject);
			}
			modStamp++;
		}
	}

	private void addReference(IProject sourceProject, IProject targetProject) {
		synchronized (graphLock) {
			Set<IProject> referencingProjects = graph.get(targetProject);
			if (referencingProjects == null) {
				referencingProjects = new HashSet<IProject>();
				graph.put(targetProject, referencingProjects);
			}
			referencingProjects.add(sourceProject);
			modStamp++;
		}
	}

	public static final Object GRAPH_UPDATE_JOB_FAMILY = new Object();

	private static final int JOB_DELAY = 100;

	private final GraphUpdateJob graphUpdateJob = new GraphUpdateJob();
	private final Object jobLock = new Object();

	private class GraphUpdateJob extends Job {

		public GraphUpdateJob() {
			super("Graph Update Job");
			setSystem(true);
			setRule(null);
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
			synchronized (graphLock) {
				modStamp++;
			}
			projectsAdded.add(project);
		}

		public void queueProjectDeleted(IProject project) {
			synchronized (graphLock) {
				modStamp++;
			}
			projectsRemoved.add(project);
		}

		public void queueProjectUpdated(IProject project) {
			synchronized (graphLock) {
				modStamp++;
			}
			projectsUpdated.add(project);
		}

		@Override
		public boolean shouldSchedule() {
			boolean isEmpty = projectsAdded.isEmpty() && projectsRemoved.isEmpty() && projectsUpdated.isEmpty();
			return !isEmpty;
		}

		protected IStatus run(IProgressMonitor monitor) {
			final Object[] removed = projectsRemoved.getListeners();
			final Object[] updated = projectsUpdated.getListeners();
			final Object[] added = projectsAdded.getListeners();
			if (removed.length == 0 && updated.length == 0 && added.length == 0) {
				return Status.OK_STATUS;
			}
			synchronized (graphLock) {
				modStamp++;
			}
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					ModulecorePlugin.logError(e);
				}

				public void run() throws Exception {
					// this is the simple case; just remove them all
					synchronized (graphLock) {
						for (Object o : removed) {
							IProject project = (IProject) o;
							removeAllReferences(project);
						}
					}
					// get the updated queue in case there are any adds
					// if there are any added projects, then unfortunately the
					// entire workspace needs to be processed
					if (added.length > 0) {
						IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
						for (IProject sourceProject : allProjects) {
							IVirtualComponent component = ComponentCore.createComponent(sourceProject);
							if (component != null) {
								IVirtualReference[] references = component.getReferences();
								for (IVirtualReference ref : references) {
									IVirtualComponent targetComponent = ref.getReferencedComponent();
									if (targetComponent != null) {
										IProject targetProject = targetComponent.getProject();
										if (targetProject != null && !targetProject.equals(sourceProject)) {
											addReference(sourceProject, targetProject);
										}
									}
								}
							}
						}
					} else if (updated.length > 0) {
						IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
						Set<IProject> validRefs = new HashSet<IProject>();
						for (Object o : updated) {
							IProject sourceProject = (IProject) o;
							IVirtualComponent component = ComponentCore.createComponent(sourceProject);
							if (component != null) {
								validRefs.clear();
								IVirtualReference[] references = component.getReferences();
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
											addReference(sourceProject, targetProject);
										} else {
											removeReference(sourceProject, targetProject);
										}
									}
								}
							} else {
								// if this project is not a component, then it
								// should be completely removed.
								removeAllReferences(sourceProject);
							}
						}
					}
				}
			});
			// System.err.println(IDependencyGraph.INSTANCE);
			return Status.OK_STATUS;
		}
	};

	/**
	 * @deprecated use {@link #update(IProject, int)}
	 */
	public void queueProjectAdded(IProject project) {
		update(project, IDependencyGraph.ADDED);
	}

	/**
	 * @deprecated use {@link #update(IProject, int)}
	 */
	public void queueProjectDeleted(IProject project) {
		update(project, IDependencyGraph.REMOVED);
	}

	/**
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

	/**
	 * Blocks until the graph is finished updating
	 */
	public void waitForAllUpdates(IProgressMonitor monitor) {
		if(graphUpdateJob.getThread() != Thread.currentThread()) {
			try {
				graphUpdateJob.join();
			} catch (InterruptedException e) {
				ModulecorePlugin.logError(e);
			}
		}
			
		if(graphUpdateJob.shouldSchedule()){
			graphUpdateJob.run(monitor);
		}
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

}
