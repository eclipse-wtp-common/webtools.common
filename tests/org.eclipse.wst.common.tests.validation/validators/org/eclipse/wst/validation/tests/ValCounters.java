package org.eclipse.wst.validation.tests;

/**
 * Some miscellaneous counters.
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
	
	@Override
	public String toString() {
		return "ValCounters: startingCount=" + startingCount + ", finishedCount="+finishedCount+
			", startProjectCount="+startingProjectCount+", finishedProjectCount="+finishedProjectCount;
	}
}
