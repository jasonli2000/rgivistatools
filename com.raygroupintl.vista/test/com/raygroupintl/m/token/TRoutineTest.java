package com.raygroupintl.m.token;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.raygroupintl.m.parsetree.Fanout;
import com.raygroupintl.m.parsetree.Routine;
import com.raygroupintl.m.parsetree.visitor.ErrorRecorder;
import com.raygroupintl.m.parsetree.visitor.FanoutRecorder;
import com.raygroupintl.m.parsetree.visitor.OccuranceRecorder;
import com.raygroupintl.m.struct.LineLocation;
import com.raygroupintl.m.struct.MError;
import com.raygroupintl.m.struct.ObjectInRoutine;
import com.raygroupintl.m.struct.MRoutineContent;
import com.raygroupintl.m.token.MVersion;
import com.raygroupintl.m.token.TFRoutine;
import com.raygroupintl.m.token.TLine;
import com.raygroupintl.m.token.TRoutine;

public class TRoutineTest {
	private static MTFSupply supplyStd95;
	private static MTFSupply supplyCache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		supplyStd95 = MTFSupply.getInstance(MVersion.ANSI_STD_95);
		supplyCache = MTFSupply.getInstance(MVersion.CACHE);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		supplyStd95 = null;
		supplyCache = null;
	}
	
	private TRoutine getRoutineToken(String fileName, MTFSupply m) {
		TFRoutine tf = TFRoutine.getInstance(m);
		InputStream is = this.getClass().getResourceAsStream(fileName);
		MRoutineContent content = MRoutineContent.getInstance(fileName.split(".m")[0], is);
		TRoutine r = tf.tokenize(content);
		return r;
	}
	
	private List<ObjectInRoutine<MError>> getErrors(String fileName, MTFSupply m) {
		TRoutine token = this.getRoutineToken(fileName, m);
		Routine r = token.getNode();
		ErrorRecorder v = new ErrorRecorder();
		List<ObjectInRoutine<MError>> result = v.visitErrors(r);
		return result;
	}
	
	public void testBeautify(MTFSupply m) {
		TRoutine original = this.getRoutineToken("resource/BEAT0SRC.m", m);
		TRoutine source = this.getRoutineToken("resource/BEAT0SRC.m", m);
		TRoutine result = this.getRoutineToken("resource/BEAT0RST.m", m);
		
		source.beautify();
		List<TLine> originalLines = original.asList();
		List<TLine> sourceLines = source.asList();
		List<TLine> resultLines = result.asList();
		int n = resultLines.size();
		Assert.assertEquals( originalLines.size(), resultLines.size());
		Assert.assertEquals(sourceLines.size(), resultLines.size());
		for (int i=1; i<n; ++i) {
			String sourceLineValue = sourceLines.get(i).getStringValue();
			String resultLineValue = resultLines.get(i).getStringValue();
			Assert.assertEquals(sourceLineValue, resultLineValue);
			if ((i > 4) && (i < 12)) {
				Assert.assertFalse(sourceLineValue.equals(originalLines.get(i).getStringValue()));				
			}
		}
	}
	
	@Test
	public void testBeautify() {
		testBeautify(supplyCache);
		testBeautify(supplyStd95);
	}
		
	public void testNonErrorFiles(MTFSupply m) {
		String[] fileNames = {"resource/XRGITST0.m", "resource/CMDTEST0.m"};
		for (String fileName : fileNames) {
			List<ObjectInRoutine<MError>> result = this.getErrors(fileName, m);
			Assert.assertEquals(0, result.size());
		}
	}

	@Test
	public void testNonErrorFiles() {
		testNonErrorFiles(supplyCache);
		testNonErrorFiles(supplyStd95);
	}
	
	private void testErrTest0Error(ObjectInRoutine<MError> error, String expectedTag, int expectedOffset) {
		LineLocation location = error.getLocation();
		Assert.assertEquals(expectedTag, location.getTag());
		Assert.assertEquals(expectedOffset, location.getOffset());		
	}
	
	private void testErrTest0(MTFSupply m) {
		String fileName = "resource/ERRTEST0.m";
		List<ObjectInRoutine<MError>> result = this.getErrors(fileName, m);
		Assert.assertEquals(3, result.size());
		testErrTest0Error(result.get(0), "MULTIPLY", 2);
		testErrTest0Error(result.get(1), "MAIN", 3);
		testErrTest0Error(result.get(2), "MAIN", 5);
	}

	@Test
	public void testErrTest0() {
		testErrTest0(supplyCache);
		testErrTest0(supplyStd95);		
	}
	
	private void checkFanouts(List<Fanout> result, String[] labels, String[] routines) {
		Assert.assertEquals(routines.length, result.size());
		int index = 0;
		for (Fanout fout : result) {
			if (routines[index] == null) {
				Assert.assertNull(fout.getRoutineName());
			} else {
				Assert.assertEquals(routines[index], fout.getRoutineName());
			}
			if (labels[index] == null) {
				Assert.assertNull(fout.getTag());
			} else {
				Assert.assertEquals(labels[index], fout.getTag());				
			}
			++index;
		}
	}
	
	private void testCmdTest0(MTFSupply m) {
		String fileName = "resource/CMDTEST0.m";
		TRoutine token = this.getRoutineToken(fileName, m);
		Routine r = token.getNode();

		OccuranceRecorder or = OccuranceRecorder.record(r);		
		Assert.assertEquals(0, or.getErrorNodeCount());
		Assert.assertEquals(6, or.getDoBlockCount());
		Assert.assertEquals(21, or.getDoCount());
		Assert.assertEquals(29, or.getAtomicDoCount());
		Assert.assertEquals(8, or.getExternalDoCount());
		Assert.assertEquals(12, or.getIndirectionCount());
		
/*		FanoutRecorder foutr = new FanoutRecorder();
		Map<LineLocation, List<Fanout>> fanouts = foutr.getFanouts(r);	
		List<Fanout> do1 = fanouts.get(new LineLocation("DO", 1));
		this.checkFanouts(do1, new String[]{"L0", "L1", "L2"}, new String[]{"R0", "R1", "R3"});
		List<Fanout> do3 = fanouts.get(new LineLocation("DO", 3));
		this.checkFanouts(do3, new String[]{"T0", "T1", "T2"}, new String[]{null, null, null});
		List<Fanout> do4 = fanouts.get(new LineLocation("DO", 4));
		this.checkFanouts(do4, new String[]{"T2"}, new String[]{null});
		List<Fanout> do5 = fanouts.get(new LineLocation("DO", 5));
		this.checkFanouts(do5, new String[]{"T0", "T1"}, new String[]{null, null});
		List<Fanout> do9 = fanouts.get(new LineLocation("DO", 9));
		this.checkFanouts(do9, new String[]{"T5"}, new String[]{"R5"});
		List<Fanout> do10 = fanouts.get(new LineLocation("DO", 10));
		this.checkFanouts(do10, new String[]{"2", "3", "7", "T8"}, new String[]{"R6", null, "R6", "R8"});
		List<Fanout> do13 = fanouts.get(new LineLocation("DO", 13));
		this.checkFanouts(do13, new String[]{null}, new String[]{"R10"});
		Assert.assertEquals(15, fanouts.size());
*/	}

	@Test
	public void testCmdTest0() {
		testCmdTest0(supplyCache);
		testCmdTest0(supplyStd95);		
	}
}
