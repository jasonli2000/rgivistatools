package com.raygroupintl.bnf.annotation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.raygroupintl.bnf.SyntaxErrorException;
import com.raygroupintl.bnf.TSequence;
import com.raygroupintl.bnf.TFCharacter;
import com.raygroupintl.bnf.TFSequenceStatic;
import com.raygroupintl.bnf.Text;
import com.raygroupintl.bnf.Token;
import com.raygroupintl.bnf.TokenFactory;
import com.raygroupintl.charlib.CharPredicate;
import com.raygroupintl.charlib.Predicate;

public class DescriptionSpecTest {
	private static DescriptionSpec spec;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parser parser = new Parser();
		spec = parser.parse(DescriptionSpec.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		spec = null;
	}

	@Test
	public void testSymbol() {
		try {
			Text text = new Text("token");
			Token symbol = spec.symbol.tokenize(text);
			Assert.assertNotNull(symbol);
			Assert.assertTrue(symbol instanceof TSymbol);
			Assert.assertEquals("token", symbol.getStringValue());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testOptionalSymbols() {
		try {
			Text text = new Text("[a, b, ([c], [d])]");
			Token optionalSymbols = spec.optionalsymbols.tokenize(text);
			Assert.assertNotNull(optionalSymbols);
			Assert.assertTrue(optionalSymbols instanceof TOptionalSymbols);
			Assert.assertEquals("[a, b, ([c], [d])]", optionalSymbols.getStringValue());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testRequiredSymbols() {
		try {
			Text text = new Text("([a, b], [c], [d])");
			Token requiredSymbols = spec.requiredsymbols.tokenize(text);
			Assert.assertNotNull(requiredSymbols);
			Assert.assertTrue(requiredSymbols instanceof TRequiredSymbols);
			Assert.assertEquals("([a, b], [c], [d])", requiredSymbols.getStringValue());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	private void updateMap(Map<String, TokenFactory> map, char ch) {
		Predicate p = new CharPredicate(ch);
		TokenFactory f = new TFCharacter(String.valueOf(ch), p);
		map.put(String.valueOf(ch), f);		
	}
	
	private void testTDescription(TokenFactory f, String v) {
		try {
			Text text = new Text(v);
			Token result = f.tokenize(text);
			Assert.assertNotNull(result);
			Assert.assertTrue(result instanceof TSequence);
			Assert.assertEquals(v, result.getStringValue());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}
	
	private void testErrorTDescription(TokenFactory f, String v) {
		try {
			Text text = new Text(v);
			f.tokenize(text);
			fail("Expected exception did not fire");			
		} catch (SyntaxErrorException se) {
		}
	}

	private Map<String, TokenFactory> getMap() {
		Map<String, TokenFactory> map = new HashMap<String, TokenFactory>();
		char[] chs = {'x', 'y', 'a', 'b', 'c', 'd', 'e'};
		for (char ch : chs) {
			updateMap(map, ch);
		}
		return map;
	}
	
	@Test
	public void testList() {
		try {
			Text text = new Text("x, {y:',':'(':')'}, [{a}, [{b:':'}], [c], d], e");
			TDescription description = (TDescription) spec.description.tokenize(text);
			Assert.assertNotNull(description);
			Assert.assertTrue(description instanceof TDescription);
			Assert.assertEquals("x, {y:',':'(':')'}, [{a}, [{b:':'}], [c], d], e", description.getStringValue());
			Map<String, TokenFactory> map = this.getMap();
			TokenFactory f = description.getFactory("test", map);
			Assert.assertNotNull(f);
			Assert.assertTrue(f instanceof TFSequenceStatic);
			testTDescription(f, "x(y)e"); 
			testTDescription(f, "x(y,y)e"); 
			testTDescription(f, "x(y,y,y)e"); 
			testTDescription(f, "x(y)aaab:bde"); 
			testTDescription(f, "x(y,y)abde"); 
			testTDescription(f, "x(y,y)abcde"); 
			testTDescription(f, "x(y,y,y)acde"); 
			testTDescription(f, "x(y,y,y)aaaade"); 
			testErrorTDescription(f, "xye"); 
			testErrorTDescription(f, "x(ya)de"); 
			testErrorTDescription(f, "x(yy)abde"); 
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testList0() {
		try {
			Text text = new Text("{y:',':'(':')'}, {a}");
			TDescription description = (TDescription) spec.description.tokenize(text);
			Assert.assertNotNull(description);
			Assert.assertTrue(description instanceof TDescription);
			Assert.assertEquals("{y:',':'(':')'}, {a}", description.getStringValue());
			Map<String, TokenFactory> map = this.getMap();
			TokenFactory f = description.getFactory("test", map);
			Assert.assertNotNull(f);
			Assert.assertTrue(f instanceof TFSequenceStatic);
			testTDescription(f, "(y)a"); 
			testTDescription(f, "(y,y)aa"); 
			testErrorTDescription(f, "(y,x)aa"); 
			testErrorTDescription(f, "(y,y)"); 
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testSequence() {
		try {
			Text text = new Text("x, y, [(a, b), [c], d], e");
			TDescription description = (TDescription) spec.description.tokenize(text);
			Assert.assertNotNull(description);
			Assert.assertTrue(description instanceof TDescription);
			Assert.assertEquals("x, y, [(a, b), [c], d], e", description.getStringValue());
			Map<String, TokenFactory> map = this.getMap();
			TokenFactory f = description.getFactory("test", map);
			Assert.assertNotNull(f);
			Assert.assertTrue(f instanceof TFSequenceStatic);
			testTDescription(f, "xye"); 
			testTDescription(f, "xyabde"); 
			testTDescription(f, "xyabcde"); 
			testErrorTDescription(f, "xyae"); 
			testErrorTDescription(f, "xyde"); 
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}
}
