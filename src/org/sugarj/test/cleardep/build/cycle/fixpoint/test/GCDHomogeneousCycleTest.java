package org.sugarj.test.cleardep.build.cycle.fixpoint.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.sugarj.cleardep.BuildUnit;
import org.sugarj.test.cleardep.build.ScopedBuildTest;
import org.sugarj.test.cleardep.build.TrackingBuildManager;
import org.sugarj.test.cleardep.build.cycle.fixpoint.FileInput;
import org.sugarj.test.cleardep.build.cycle.fixpoint.IntegerOutput;
import org.sugarj.test.cleardep.build.cycle.fixpoint.ModuloBuilder;

public class GCDHomogeneousCycleTest extends ScopedBuildTest {

	@Override
	protected String getTestFolderName() {
		return FixpointCycleTestSuite.FIXPOINT_BUILDER_CYCLE_TEST;
	}
	
	@Test
	public void testBuildModuloCycle() throws IOException {
		BuildUnit<IntegerOutput> resultUnit = new TrackingBuildManager().require(ModuloBuilder.factory, new FileInput(testBasePath, "cyclemodmain.modulo"));
		assertEquals("No cycle produced wrong output", 0, resultUnit.getBuildResult().getResult());
	}

}
