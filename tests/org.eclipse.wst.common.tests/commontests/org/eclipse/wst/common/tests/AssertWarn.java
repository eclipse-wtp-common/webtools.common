package org.eclipse.wst.common.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;


public class AssertWarn extends Assert {
	private static List<AssertionFailedError> warnings = new ArrayList<AssertionFailedError>();
	
	public static void warnEquals(boolean expected, boolean actual) {
		try{
			assertEquals(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(byte expected, byte actual) {
		try{
			assertEquals(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(char expected, char actual) {
		try{
			assertEquals(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(double expected, double actual, double delta) {
		try{
			assertEquals(expected, actual, delta);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(float expected, float actual, float delta) {
		try{
			assertEquals(expected, actual, delta);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(int expected, int actual) {
		try{
			assertEquals(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(Object expected, Object actual) {
		try{
			assertEquals(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(long expected, long actual) {
		try{
			assertEquals(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(short expected, short actual) {
		try{
			assertEquals(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, boolean expected, boolean actual) {
		try{
			assertEquals(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, byte expected, byte actual) {
		try{
			assertEquals(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, char expected, char actual) {
		try{
			assertEquals(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, double expected, double actual, double delta) {
		try{
			assertEquals(message, expected, actual, delta);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, float expected, float actual, float delta) {
		try{
			assertEquals(message, expected, actual, delta);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, int expected, int actual){
		try{
			assertEquals(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, Object expected, Object actual){
		try{
			assertEquals(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, String expected, String actual){
		try{
			assertEquals(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, long expected, long actual){
		try{
			assertEquals(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnEquals(String message, short expected, short actual){
		try{
			assertEquals(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnFalse(boolean condition){
		try{
			assertFalse(condition);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnFalse(String message, boolean condition){
		try{
			assertFalse(message, condition);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnNotNull(Object object) {
		try{
			assertNotNull(object);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnNotNull(String message, Object object) {
		try{
			assertNotNull(message, object);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnNotSame(Object expected, Object actual) {
		try{
			assertNotSame(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnNotSame(String message, Object expected, Object actual) {
		try{
			assertNotSame(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnNull(Object object) {
		try{
			assertNull(object);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnNull(String message, Object object) {
		try{
			assertNull(message, object);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnSame(Object expected, Object actual) {
		try{
			assertSame(expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnSame(String message, Object expected, Object actual) {
		try{
			assertSame(message, expected, actual);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnTrue(boolean condition){
		try{
			assertTrue(condition);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warnTrue(String message, boolean condition){
		try{
			assertTrue(message, condition);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warn() {
		try{
			fail();
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void warn(String message) {
		try{
			fail(message);
		} catch(AssertionFailedError e){
			warnings.add(e);
		}
	}
	
	public static void clearWarnings(){
		warnings.clear();
	}
	
	public static List <AssertionFailedError> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}
	
}
