package org.eclipse.wst.validation.tests;

/**
 * Some misc counters.
 * @author karasiuk
 *
 */
public class ValCounters {
	public int 	startingCount;
	public int	finishedCount;
	public int 	startingProjectCount;
	public int	finishedProjectCount;
	
	public void reset(){
		startingCount = 0;
		startingProjectCount = 0;
		finishedCount = 0;
		finishedProjectCount = 0;
	}
}
