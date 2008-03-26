package org.eclipse.wst.validation.internal;

/**
 * A simple deserializer that knows how to retrieve booleans, integers and strings, from a string
 * that was encoded by the Serializer class.
 * 
 *  @see Serializer
 * @author karasiuk
 *
 */
public class Deserializer {
	private char[] 	_buffer;
	private int		_posn;
	
	public Deserializer(String value){
		_buffer = value.toCharArray();
	}
	
	public boolean getBoolean(){
		boolean result = false;
		if (_buffer[_posn] == 'T')result = true;
		else if (_buffer[_posn] == 'F')result = false;
		else throw new IllegalStateException(ValMessages.DecodeError1);
		
		_posn++;
		return result;
	}
	
	public String getString(){
		int len = getInt();
		String result = new String(_buffer, _posn, len);
		_posn += len;
		return result;		
	}
	
	public int getInt(){
		Integer len = new Integer(String.valueOf(_buffer[_posn]));
		_posn++;
		
		String s = String.valueOf(_buffer, _posn, len+1);
		_posn += len+1;
		return Integer.parseInt(s);
	}

	/**
	 * Are there any more items?
	 * @return
	 */
	public boolean hasNext() {
		if (_posn >= _buffer.length)return false;
		return true;
	}
}
