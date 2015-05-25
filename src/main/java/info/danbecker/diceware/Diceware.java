package info.danbecker.diceware;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Diceware
 * <p>
 * Implements Diceware algorithms for pass-phrase generation
 * <p>
 * A number of words are selected from a dictionary using random numbers.
 * The number of words and the size of the dictionary make the pass-phrase
 * more difficult to guess than normal passwords.
 * See the algorthim here at <a href="http://world.std.com/~reinhold/diceware.html">Diceware FAQ</a>.
 * 
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class Diceware {
	public static final String STD_LIST_NAME = "diceware.txt";
	public static final String EXT_LIST_NAME = "diceware8k.txt";	

	public static final int MAX_5DICE = 7775;  // aka "66666"	
	public static final int MAX_8K = 8191;
	
	private List<String> wordList = new ArrayList<String>(9000);
	
	public static final org.slf4j.Logger jLogger = 
		org.slf4j.LoggerFactory.getLogger(Diceware.class);

	public static void main(String[] args) throws Exception {
	}

	/** 
	 * Converts an int such as 1, 6, 123, 654321 to base 6 String.
	 * The string is ordinal adjusted to 0 => "1"
	 * Throws exception with illegals.
	 */
	public static String toBase6( int base6 ) {
		if ( base6 < 0 )
			throw new IllegalArgumentException( "Illegal input=" + base6);
		String base6String = Integer.toString( base6, 6 );
		// Adjust chars one higher.
		char [] base6Chars = base6String.toCharArray();
		for (int i = 0; i < base6Chars.length; i++){
		    base6Chars[ i ] = (char)((int)base6Chars[ i ] + 1);
		}
		return new String( base6Chars );
	}
	
	/** 
	 * Converts a string such as 1, 6, 123, 654321 in int.
	 * Throws exception with non-numerics.
	 */
	public static int parseBase6( String base6String ) {
		if (( null == base6String ) || ( base6String.length() < 1 ))
			throw new IllegalArgumentException( "Illegal string=" + base6String );
		int value = 0;
		for (int i = 0; i < base6String.length(); i++){
		    char c = base6String.charAt(i);
		    if ( -1 == "123456".indexOf( c )) 
				throw new IllegalArgumentException( "Illegal char at position=" + i + ", string=" + base6String );
		    int digit = Character.getNumericValue(c);
		    value = value * 6 + ( digit - 1 );
		}
		return value;
	}
	
	/** Load a word list (one word per line) from the given location. 
	 * Assumes the name is a resource from the classpath or JAR.
	 */
	public static void loadList( List<String> wordList, String resourceName ) throws FileNotFoundException, IOException {
		InputStream is = Diceware.class.getClassLoader().getResourceAsStream( resourceName ); // can read from relative path
	    try(BufferedReader br = new BufferedReader( new InputStreamReader( is ))) {
	        String line = br.readLine();

	        while (line != null) {
	            wordList.add( line ); // one word per line
	            line = br.readLine();
	        }
	    }
	}
	
}
