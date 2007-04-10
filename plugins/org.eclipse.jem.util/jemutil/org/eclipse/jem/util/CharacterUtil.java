/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*


 */
package org.eclipse.jem.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ibm.icu.text.UTF16;
 

/**
 * Static helper to handle characters in the new UTF multi-char format.
 * It is needed because ICU4J currently doesn't handle some correctly yet that we
 * need to have working. As ICU4J gets them working, the methods here will become
 * deprecated.
 * 
 * @since 1.2.0
 */
public class CharacterUtil {

	private CharacterUtil() {
		
	}
	
	
	/**
	 * TODO So until ICU4J does work correctly this util will be needed. It will
	 * stay around because it is API, but at that time it will be marked deprecated. It should
	 * also then reroute to ICU4J instead of doing the method reflections.
	 */
	private static Method METHOD_JAVA_IDENTIFIER_START, METHOD_JAVA_IDENTIFIER_PART;
	
	static {
		// Try to get the Character.isJavaIdentifier(int) method. If there, then we are in 1.5 or above. Else use the char form.
		try {
			METHOD_JAVA_IDENTIFIER_START = Character.class.getMethod("isJavaIdentifierStart", new Class[] {Integer.TYPE});
		} catch (SecurityException e) {
			// Default to use (char) type instead.
			METHOD_JAVA_IDENTIFIER_START = null;
		} catch (NoSuchMethodException e) {
			// Default to use (char) type instead.
			METHOD_JAVA_IDENTIFIER_START = null;
		}
		try {
			METHOD_JAVA_IDENTIFIER_PART = Character.class.getMethod("isJavaIdentifierPart", new Class[] {Integer.TYPE});
		} catch (SecurityException e) {
			// Default to use (char) type instead.
			METHOD_JAVA_IDENTIFIER_PART = null;
		} catch (NoSuchMethodException e) {
			// Default to use (char) type instead.
			METHOD_JAVA_IDENTIFIER_PART = null;
		}		
	}

	/**
	 * Is start of java identifier
	 * @param intChar int character (UTF multi-char is valid)
	 * @return <code>true</code> if start of java identifier.
	 * 
	 * @see Character#isJavaIdentifierStart(char)
	 * @since 1.2.0
	 */
	public static boolean isJavaIdentifierStart(int intChar) {
		if (METHOD_JAVA_IDENTIFIER_START != null) {
			try {
				return ((Boolean) METHOD_JAVA_IDENTIFIER_START.invoke(null, new Object[] {new Integer(intChar)})).booleanValue();
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		return Character.isJavaIdentifierStart((char) intChar);
	}
	
	/**
	 * Is start of java identifier
	 * @param intChar int character (UTF multi-char is valid)
	 * @return <code>true</code> if start of java identifier.
	 * 
	 * @see Character#isJavaIdentifierStart(char)
	 * @since 1.2.0
	 */
	public static boolean isJavaIdentifierPart(int intChar) {
		if (METHOD_JAVA_IDENTIFIER_PART != null) {
			try {
				return ((Boolean) METHOD_JAVA_IDENTIFIER_PART.invoke(null, new Object[] {new Integer(intChar)})).booleanValue();
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		return Character.isJavaIdentifierPart((char) intChar);
	}
	
	public static abstract class AbstractCharIterator {

		
		protected final CharSequence charSeq;
		private int pos = 0;
		private int lastCharIndex = 0;

		/**
		 * Create with a string.
		 * @param charSeq
		 * 
		 * @since 1.2.0
		 */
		public AbstractCharIterator(CharSequence charSeq) {
			this.charSeq = charSeq;
		}
		
		/**
		 * Set the next char index.
		 * @param index
		 * 
		 * @since 1.2.0
		 */
		public void setIndex(int index) {
			pos = index;
		}

		/**
		 * Has another char.
		 * @return <code>true</code> if there is another char to return.
		 * 
		 * @since 1.2.0
		 */
		public boolean hasNext() {
			return pos < charSeq.length();
		}
		
		/**
		 * Has another char before the current position. Doing previous
		 * will return the char that was just returned.
		 * @return
		 * 
		 * @since 1.2.0
		 */
		public boolean hasPrevious() {
			return pos > 0;
		}
		
		/**
		 * Return next char from the one that was just returned.
		 * @return next char.
		 * 
		 * @since 1.2.0
		 */
		public int next() {
			if (!hasNext())
				throw new IllegalStateException();
			
			int next = utfCharAt(pos);
			lastCharIndex = pos;
			pos += UTF16.getCharCount(next);
			return next;
		}
		
		/**
		 * Return the UTF-32 char at the given position.
		 * @param pos
		 * @return
		 * 
		 * @since 1.2.0
		 */
		protected abstract int utfCharAt(int pos);
		
		/**
		 * Return the previous character from the one that was just returned.
		 * @return
		 * 
		 * @since 1.2.0
		 */
		public int previous() {
			if (!hasPrevious())
				throw new IllegalStateException();

			int next;
			if (UTF16.isTrailSurrogate(charSeq.charAt(--pos))) {
				if (pos > 0)
					next = utfCharAt(--pos);
				else
					next = charSeq.charAt(pos);
			} else {
				next = charSeq.charAt(pos);
			}
			lastCharIndex = pos;
			return next;
		}
		
		/**
		 * Return the UTF16 character position of the char that was just returned from either
		 * previous or next.
		 * This is the (char) position not the
		 * position of logical int chars returned. For example a standard string of
		 * <code>"abc"</code> the position of the char 'b' is 1. But take the string
		 * <code>"ab1b2c"</code> where "b1b2" is one UTF-32 char, then the position
		 * of 'c' is 3. It would not be 2, which is what the logical char position
		 * would be if taking UFT32 into account.
		 * @return
		 * 
		 * @since 1.2.0
		 */
		public int getPosition() {
			return lastCharIndex;
		}
	
	}
	
	/**
	 * Special char iterator that returns ints instead of chars for
	 * walking strings that can contain UTF multi-chars. This is
	 * a limited version of {@link java.text.CharacterIterator}.
	 * 
	 * @since 1.2.0
	 */
	public static class StringIterator extends AbstractCharIterator {
		

		/**
		 * Create with a string.
		 * @param str
		 * 
		 * @since 1.2.0
		 */
		public StringIterator(String str) {
			super(str);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jem.util.CharacterUtil.AbstractCharIterator#utfCharAt(int)
		 */
		protected int utfCharAt(int pos) {
			return UTF16.charAt((String) charSeq, pos);
		}
		
	}
	
	/**
	 * Special char iterator that returns ints instead of chars for
	 * walking strings that can contain UTF multi-chars. This is
	 * a limited version of {@link java.text.CharacterIterator}.
	 * 
	 * @since 1.2.0
	 */
	public static class StringBufferIterator extends AbstractCharIterator {
		

		/**
		 * Create with a string.
		 * @param strBuffer
		 * 
		 * @since 1.2.0
		 */
		public StringBufferIterator(StringBuffer strBuffer) {
			super(strBuffer);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jem.util.CharacterUtil.AbstractCharIterator#utfCharAt(int)
		 */
		protected int utfCharAt(int pos) {
			return UTF16.charAt((StringBuffer) charSeq, pos);
		}
		
	}	
}
