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

public class NoCycleTest extends ScopedBuildTest{

	@Override
	protected String getTestFolderName() {
		return FixpointCycleTestSuite.FIXPOINT_BUILDER_CYCLE_TEST;
	}
	
	@Test
	public void testBuildNoCycle() throws IOException {
		BuildUnit<IntegerOutput> resultUnit = new TrackingBuildManager().require(ModuloBuilder.factory, new FileInput(testBasePath, "main1.modulo"));
		assertEquals("No cycle produced wrong output", 1, resultUnit.getBuildResult().getResult());
	}

}
