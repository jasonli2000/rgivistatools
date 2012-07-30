package com.raygroupintl.vista.tools;

import org.junit.Test;

public class CLIExamples {
	//@Test
	public void testCreateParseTreeFiles() {
		// Create files for parse tree
		String args1[] = {
				"-t", "serial",
				"-ptd", "C:\\Sandbox\\serial"};
		MRoutineAnalyzer.main(args1);		
	}
	
	
	/* This prints code quality report for all the tags in Problem List API
	 * routines GMPLAPI*, GMPLSITE, GMPLDAL*, GMPLEXT*.  It assumes environment
	 * variable VistA-FOIA that point to the VistA-FOIA library. It assumes
	 * testCreateParseTreeFiles is run.
	 */
	@Test
	public void testProblemListAPITags() {
	    // Create entry points for the routines.
		String args0[] = {
				"-t", "entry",
				"-p", "PROBLEM LIST", 
				"-r", "GMPLAPI.*", "-r", "GMPLSITE", "-r", "GMPLDAL.*", "-r", "GMPLEXT.*", 
				"-o", "C:\\Sandbox\\j_gmplapitest_all_in.dat"};
		MRoutineAnalyzer.main(args0);
		
		// Write the API
		String args2[] = {
				"-t", "apis", 
				"-i", "C:\\Sandbox\\j_gmplapitest_all_in.dat", 
				"-o", "C:\\Sandbox\\j_gmplapitest_all.dat",
				"-ptd", "C:\\Sandbox\\serial"};
		MRoutineAnalyzer.main(args2);
	}
	
	//@Test
	public void testGeneric() {
		// Write the API
		String args2[] = {
				"-t", "apis", 
				"-e", "ASK^DIC", 
				"-o", "C:\\Sandbox\\j_test.dat",
				"-ptd", "C:\\Sandbox\\serial"};
		MRoutineAnalyzer.main(args2);
	}
}