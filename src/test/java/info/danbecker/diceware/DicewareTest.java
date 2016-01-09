package info.danbecker.diceware;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class DicewareTest {
	@Before
    public void setup() {
	}
	
	@Test
    public void testBase6() {
		try { 
			Diceware.toBase6( -1 );
		} catch ( Exception e ) {
			assertEquals( "base 6", IllegalArgumentException.class, e.getClass() ); 
		}
		
		assertEquals( "base 6", "1", Diceware.toBase6( 0 ) );
		assertEquals( "base 6", "6", Diceware.toBase6( 5 ) );
		
		assertEquals( "base 6", "66666", Diceware.toBase6( Diceware.MAX_5DICE ) );
		assertEquals( "base 6", "212642", Diceware.toBase6( Diceware.MAX_8K) );
	}

	@Test
    public void testParseBase6() {
		try { 
			Diceware.parseBase6( null );
		} catch ( Exception e ) {
			assertEquals( "parse base 6", IllegalArgumentException.class, e.getClass() ); 
		}
		try { 
			Diceware.parseBase6( "" );		
		} catch ( Exception e ) {
			assertEquals( "parse base 6", IllegalArgumentException.class, e.getClass() ); 
		}
		try { 
			Diceware.parseBase6( "a" );
		} catch ( Exception e ) {
			assertEquals( "parse base 6", IllegalArgumentException.class, e.getClass() ); 
		}
		try { 
			Diceware.parseBase6( "9" );		
		} catch ( Exception e ) {
			assertEquals( "parse base 6", IllegalArgumentException.class, e.getClass() ); 
		}
		try { 
			Diceware.parseBase6( "0" );
		} catch ( Exception e ) {
			assertEquals( "parse base 6", IllegalArgumentException.class, e.getClass() ); 
		}
		
		assertEquals( "parse base 6", 0, Diceware.parseBase6( "1" ) );
		assertEquals( "parse base 6", 5, Diceware.parseBase6( "6" ) );
		
		assertEquals( "parse base 6", Diceware.MAX_5DICE, Diceware.parseBase6( "66666" ) );
		assertEquals( "parse base 6", Diceware.MAX_8K, Diceware.parseBase6( "212642" ) );
	}

	@Test
    public void testWordList() {
		try {
			List<String> wordList = new ArrayList<String>(9000);
			
			Diceware.loadList( wordList, Diceware.DICT_STD_NAME );			
			assertEquals( "word list", 7776, wordList.size() );
			wordList.clear();
			
			Diceware.loadList( wordList, Diceware.DICT_D8K_NAME );			
			assertEquals( "word list", 8192, wordList.size() );
			wordList.clear();
		} catch ( Throwable e ) {
			System.out.println( "exception=" + e);
			assertNull( "word list", e);
		}
		
	}

	@Test
    public void testOptions() {
		String [] cmdLine = new String[] { "java", "Diceware", "--numWords=5", "--specialCharEntropy=ALL", "--strictMode" };

		try{ 
			Diceware.parseGatherOptions( cmdLine );
			assertEquals( "options", 5, Diceware.numWords );
			assertEquals( "options", Diceware.WordOption.ALL, Diceware.specialCharEntropy );
			assertEquals( "options", true, Diceware.strictMode );
		} catch( ParseException e ) {
			assertNull( "options", e);
		}
		
	}
	
	@Test
    public void testSpecialChars() {
		assertEquals( "special chars", 36, Diceware.SPECIAL_CHARS.length() );
		assertEquals( "special chars", '~', Diceware.SPECIAL_CHARS.charAt( 0 ) );
		assertEquals( "special chars", '9', Diceware.SPECIAL_CHARS.charAt( Diceware.SPECIAL_CHARS.length() - 1 ) );		
		assertEquals( "special chars", '\\', Diceware.SPECIAL_CHARS.charAt( 15 ));		
	}
	
	@Test
    public void testGetPassPhrase() {
		try {
			List<String> wordList = new ArrayList<String>(9000);
			Diceware.loadList( wordList, Diceware.DICT_D8K_NAME );			
			assertEquals( "pass phrase", 8192, wordList.size() );
			
			final int NUM_WORDS = 6;
			String passPhrase = Diceware.getPassPhrase( wordList, NUM_WORDS, Diceware.WordOption.NONE );
			System.out.println( "Diceware random passPhrase=" + passPhrase );
			assertNotNull( "pass phrase", passPhrase );
			
			StringTokenizer st = new StringTokenizer( passPhrase, Diceware.WORD_DELIM );
			assertEquals( "pass phrase", NUM_WORDS, st.countTokens() );
			
			while( st.hasMoreTokens() ) {
				String word = st.nextToken();
				// Hmm, some single chars in there
				assertTrue( "pass phrase", word.length() > 0  );
				
				// Hmm, some words have numbers or special chars like ~!@#$%^&*()_+
				// for (int i = 0; i < word.length(); i++){
				//     char c = word.charAt(i);
				// 	assertTrue( "pass phrase alpha word=" + word + ", position=" + i, Character.isAlphabetic( c )  );
				// 	assertTrue( "pass phrase lower word=" + word + ", position=" + i, Character.isLowerCase( c )  );
				// }
			}
			
			passPhrase = Diceware.getPassPhrase( wordList, NUM_WORDS, Diceware.WordOption.ALL );
			System.out.println( "Diceware random passPhrase=" + passPhrase );
			assertNotNull( "pass phrase", passPhrase );
			
			st = new StringTokenizer( passPhrase, Diceware.WORD_DELIM );
			assertEquals( "pass phrase", NUM_WORDS, st.countTokens() );
			
			while( st.hasMoreTokens() ) {
				String word = st.nextToken();
				assertTrue( "pass phrase", word.length() > 0  );

				int specialCount = 0;
				for (int i = 0; i < word.length(); i++){
				    char c = word.charAt(i);
				    if ( -1 != Diceware.SPECIAL_CHARS.indexOf( c ))
				    	specialCount++;
				}
				assertTrue( "pass phrase special count", specialCount > 0 );
			}

		} catch ( Throwable e ) {
			System.out.println( "exception=" + e);
			assertNull( "pass phrase", e);
		}
		
	}

	@Test
    public void testCharRandomization() {
		final String TEST_WORD = "foo";
		final String RANDOMIZED_WORD = Diceware.updateRandomChar( TEST_WORD, 0 );
		
		assertNotEquals( "random char", TEST_WORD, RANDOMIZED_WORD );		
		assertNotEquals( "random char", TEST_WORD.charAt(0), RANDOMIZED_WORD.charAt(0));		
		assertEquals( "random char", TEST_WORD.charAt(1), RANDOMIZED_WORD.charAt(1));		
		assertEquals( "random char", TEST_WORD.charAt(2), RANDOMIZED_WORD.charAt(2));		
		assertNotEquals( "random char", -1, Diceware.SPECIAL_CHARS.indexOf( RANDOMIZED_WORD.charAt(0) ));
		
		final String RANDOMIZED_WORD2 = Diceware.updateRandomChar( TEST_WORD, 2 );
		assertNotEquals( "random char", TEST_WORD, RANDOMIZED_WORD2 );		
		assertEquals( "random char", TEST_WORD.charAt(0), RANDOMIZED_WORD2.charAt(0));		
		assertEquals( "random char", TEST_WORD.charAt(1), RANDOMIZED_WORD2.charAt(1));		
		assertNotEquals( "random char", TEST_WORD.charAt(2), RANDOMIZED_WORD2.charAt(2));		
		assertNotEquals( "random char", -1, Diceware.SPECIAL_CHARS.indexOf( RANDOMIZED_WORD2.charAt(2) ));

		final String RANDOMIZED_WORD3 = Diceware.updateRandomChar( TEST_WORD );
		System.out.println( "Original/Randomized word=" + TEST_WORD + "/" + RANDOMIZED_WORD3);
		assertNotEquals( "random char", TEST_WORD, RANDOMIZED_WORD3 );
		for ( int i = 0; i < TEST_WORD.length(); i++ ) {
			assertTrue( "random char", (TEST_WORD.charAt( i ) == RANDOMIZED_WORD3.charAt( i )) ||
									   -1 != Diceware.SPECIAL_CHARS.indexOf( RANDOMIZED_WORD3.charAt(i) ));
		}

	}

	@Test
    public void testLeetCharRandomization() {
		final String TEST_WORD = "foo";
		final String RANDOMIZED_WORD = Diceware.updateLeetChar( TEST_WORD, 1 );
		assertEquals( "random char", "f0o", RANDOMIZED_WORD);		
		
		final String RANDOMIZED_WORD2 = Diceware.updateLeet( TEST_WORD );
		assertEquals( "random char", "=00", RANDOMIZED_WORD2);		
	}

}
