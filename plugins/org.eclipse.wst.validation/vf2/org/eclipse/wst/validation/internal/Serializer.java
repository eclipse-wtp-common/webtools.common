package org.eclipse.wst.validation.internal;

/**
 * A simple encoder that knows how to convert booleans, integers and strings, into a single string.
 * 
 * @see Deserializer
 * @author karasiuk
 *
 */
public class Serializer {
	private StringBuffer _buffer;
	
	public Serializer(int size){
		_buffer = new StringBuffer(size);
	}
	
	public void put(boolean bool){
		_buffer.append(bool ? 'T' : 'F');
	}
	
	public void put(String string){
		put(string.length());
		_buffer.append(string);
	}
	
	public void put(int anInt){
		String s = String.valueOf(anInt);
		int len = s.length();
		_buffer.append(len-1);
		_buffer.append(s);
	}
	
	@Override
	public String toString() {
		return _buffer.toString();
	}

}
