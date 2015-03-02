package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildRequest;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.build.CompileCycleAtOnceBuilder;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public class CycleAtOnceBuilderTest extends SimpleBuildTest{


	@Override
	protected BuildRequest<?,?,?,?> requirementForInput(TestBuilderInput input) {
		return new BuildRequest<ArrayList<TestBuilderInput>,BuildUnit, SimpleCyclicAtOnceBuilder, BuilderFactory<ArrayList<TestBuilderInput>, BuildUnit, SimpleCyclicAtOnceBuilder>> (SimpleCyclicAtOnceBuilder.factory, CompileCycleAtOnceBuilder.singletonArrayList(input));
	}
	
	@Test
	public void buildCycle() throws IOException {
		TrackingBuildManager manager = buildMainFile();
	}

	
}
