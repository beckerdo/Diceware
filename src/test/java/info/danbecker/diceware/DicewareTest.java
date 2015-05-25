package info.danbecker.diceware;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
			
			Diceware.loadList( wordList, Diceware.STD_LIST_NAME );			
			assertEquals( "word list", 7776, wordList.size() );
			wordList.clear();
			
			Diceware.loadList( wordList, Diceware.EXT_LIST_NAME );			
			assertEquals( "word list", 8192, wordList.size() );
			wordList.clear();
		} catch ( Throwable e ) {
			System.out.println( "exception=" + e);
			assertNull( "word list", e);
		}
		
	}

}
