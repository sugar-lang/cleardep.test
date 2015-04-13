package org.sugarj.test.cleardep.build.cycle.once.test;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildRequest;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.build.CompileCycleAtOnceBuilder;
import org.sugarj.cleardep.output.None;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.DefaultNamedScopedPath;
import org.sugarj.test.cleardep.build.ScopedPath;
import org.sugarj.test.cleardep.build.SimpleBuildTest;
import org.sugarj.test.cleardep.build.once.SimpleBuildUtilities;
import org.sugarj.test.cleardep.build.once.SimpleCyclicAtOnceBuilder;
import org.sugarj.test.cleardep.build.once.SimpleBuilder.TestBuilderInput;
import org.sugarj.test.cleardep.build.TrackingBuildManager;

public class NestedCycleAtOnceTest extends SimpleBuildTest{

	@DefaultNamedScopedPath
	private RelativePath main;
	
	@DefaultNamedScopedPath
	private RelativePath subcycleEntry;
	
	@DefaultNamedScopedPath
	private RelativePath subcyclePart1;
	
	@DefaultNamedScopedPath
	private RelativePath subcyclePart2;
	
	
	@Override
	protected BuildRequest<?,?,?,?> requirementForInput(TestBuilderInput input) {
		return new BuildRequest<ArrayList<TestBuilderInput>,None, SimpleCyclicAtOnceBuilder, BuilderFactory<ArrayList<TestBuilderInput>, None, SimpleCyclicAtOnceBuilder>> (SimpleCyclicAtOnceBuilder.factory, CompileCycleAtOnceBuilder.singletonArrayList(input));
	}
	
	@Test(timeout=2000)
	public void testCleanRebuild() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		BuildUnit<?> result = manager.require(null, requirementForInput(new TestBuilderInput(testBasePath, main)));
		
	}
	
	@Test(timeout=2000)
	public void testRebuildWithChangedCycleStructure() throws IOException {
		testCleanRebuild();
		
		SimpleBuildUtilities.removeInputFileDep(subcycleEntry, subcyclePart1);
	}

}
