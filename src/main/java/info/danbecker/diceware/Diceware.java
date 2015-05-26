package info.danbecker.diceware;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.security.SecureRandom;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
	public static final String DICT_STD_NAME = "diceware.txt";
	public static final String DICT_D8K_NAME = "diceware8k.txt";	
	
	public static final String WORD_DELIM = " ";
	public static final String SPECIAL_CHARS = "~!#$%^&*()-=+[]\\{}:;\"'<>?/0123456789";

	public static final int MAX_5DICE = 7775;  // aka "66666"	
	public static final int MAX_8K = 8191;
	
	private static final List<String> wordList = new ArrayList<String>(9000);
	private static final Random random = new SecureRandom();
	
	public static final org.slf4j.Logger jLogger = 
		org.slf4j.LoggerFactory.getLogger(Diceware.class);

	public enum WordOption {
		NONE, ONE, ALL
	}
	
	public enum DictionaryOption {
		STD, D8K, OTHER
	}
	
	// options
	public static boolean strictMode = false; // uses dice method, not computer shortCuts
	public static WordOption specialCharEntropy = WordOption.NONE; // use EnumSet.of( ONE, TWO ) for multiples	
    public static int numWords = 6;
	public static DictionaryOption dictionary = DictionaryOption.STD;
	public static String dictionaryName = DICT_STD_NAME;
	
	public static void main(String[] args) throws Exception {
		// Parse command line options
		parseGatherOptions( args );
		System.out.println( "Diceware numWords=" + numWords );
		System.out.println( "Diceware specialCharEntropy=" + specialCharEntropy.toString() );
		System.out.println( "Diceware stricMode=" + strictMode );
		System.out.println( "Diceware dictionary=" + dictionary );
		System.out.println( "Diceware dictionary name=" + dictionaryName );
		
		// load dictionary
		loadList( Diceware.wordList, dictionaryName );
		
		String passPhrase = getPassPhrase( Diceware.wordList, numWords, specialCharEntropy );
		System.out.println( "Diceware passPhrase=" + passPhrase );
	}

	/** Command line options for this application. */
	public static void parseGatherOptions( String [] args ) throws ParseException {
		// Parse the command line arguments
		Options options = new Options();
		options.addOption( "h", "help", false, "print the command line options." );
		options.addOption( "n", "numWords", true, "uses this many words in the pass-phrase." );
		options.addOption( "e", "specialCharEntropy", true, "uses special characters in NONE, ONE, ALL words." );
		options.addOption( "s", "strictMode", false, "avoids computer random short cuts, uses diceware dice simulation." );
		options.addOption( "d", "dictionary", true, "dictionary STD (standard), D8K (8k list), OTHER (name provided)." );
		options.addOption( "r", "dictionaryName", true, "dictionary name" );

		CommandLineParser cliParser = new DefaultParser();
		CommandLine line = cliParser.parse( options, args );
		
	    // Gather command line arguments for execution
	    if( line.hasOption( "help" ) ) {
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "java -jar diceware.jar <options> info.danbecker.diceware.Diceware", options );
	    	System.exit( 0 );
	    }
	    if( line.hasOption( "numWords" ) ) {
	    	numWords = Integer.parseInt( line.getOptionValue( "numWords" ) );
	    }	  		
	    
	    if( line.hasOption( "strictMode" ) ) {
	    	strictMode = true;	
	    }	  		
	    if( line.hasOption( "specialCharEntropy" ) ) {
	    	specialCharEntropy = WordOption.valueOf( line.getOptionValue( "specialCharEntropy" ) );
	    }	  		
	    if( line.hasOption( "dictionaryName" ) ) {
	    	dictionaryName = line.getOptionValue( "dictionaryName" );
	    }	  		
	    if( line.hasOption( "dictionary" ) ) {
	    	dictionary = DictionaryOption.valueOf( line.getOptionValue( "dictionary" ) );
	    	
	    	switch (dictionary) {
    			case STD: dictionaryName = DICT_STD_NAME;
    			case D8K: dictionaryName = DICT_D8K_NAME;
    			case OTHER: // do nothing, use provided name
	    	}
	    }	  		
	}
	
	// TODO Implement strict mode
	/** Generates a random passphrase from the wordList. 
	 * 
	 * @param wordList dictionary of random words
	 * @param numWords number of words in passphrase
	 * @param specialChars cardinality of special characters to use
	 * @return
	 */
	public static String getPassPhrase( final List<String> wordList, int numWords, WordOption specialChars) {
		StringBuilder sb = new StringBuilder();
		
		// Choose a single random word.
		int randomWord = -1;
		if ( WordOption.ONE == specialChars ) {
			randomWord = random.nextInt(numWords);
		}
		
		for ( int i = 0; i < numWords; i++ ) {
			if ( i > 0 )
				sb.append( Diceware.WORD_DELIM );
			
			String word = wordList.get( random.nextInt( wordList.size() ));
			
			// Special characters?
			if (( WordOption.ALL == specialChars ) || (( WordOption.ONE == specialChars ) && ( i == randomWord ))) {
				int randomPosition = random.nextInt(  word.length() );
				char [] chars = word.toCharArray();
				chars[ randomPosition ] = getRandomSpecialChar();
				word = new String( chars );
			}
			
			sb.append( word );			
		}
		
		return sb.toString();
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
	
	/** Returns a single random character from the SPECIAL_CHARS string. */
    public static char getRandomSpecialChar() {
    	int pos = random.nextInt( SPECIAL_CHARS.length() );
    	return SPECIAL_CHARS.charAt( pos );
    }
}
