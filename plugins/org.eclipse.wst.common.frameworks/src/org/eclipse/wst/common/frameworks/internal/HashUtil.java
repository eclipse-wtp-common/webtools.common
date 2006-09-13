package org.eclipse.wst.common.frameworks.internal;

public class HashUtil {

	public static int SEED = 11;
	private static int MULTI = 31;
	
	public static int hash(int seed, int i){
		return seed * MULTI + i;
	}
	
	public static int hash(int seed, Object obj){
		return hash(seed, null != obj ? obj.hashCode() : SEED);
	}
	
	public static int hash(int seed, boolean b){
		return hash(seed, b ? 1 : SEED);
	}
}
