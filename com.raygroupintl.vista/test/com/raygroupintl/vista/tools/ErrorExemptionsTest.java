package com.raygroupintl.vista.tools;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.raygroupintl.m.struct.LineLocation;
import com.raygroupintl.vista.tools.ErrorExemptions;

public class ErrorExemptionsTest {
	@Test
	public void test() {
		ErrorExemptions exemptions = new ErrorExemptions();
		exemptions.addLine("ANRVRRL", "BEGIN", 3);
		exemptions.addLine("ANRVRRL", "A1R", 2);
		Set<LineLocation> locations = exemptions.getLines("ANRVRRL");
		Assert.assertTrue(locations.contains(new LineLocation("BEGIN", 3)));		
		Assert.assertFalse(locations.contains(new LineLocation("BEGINX", 3)));		
		Assert.assertFalse(locations.contains(new LineLocation("BEGIN", 4)));		
	}

}
