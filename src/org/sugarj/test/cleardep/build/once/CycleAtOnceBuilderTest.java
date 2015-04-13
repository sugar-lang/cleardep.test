package org.sugarj.test.cleardep.build.once;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.sugarj.cleardep.build.BuildRequest;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.build.CompileCycleAtOnceBuilder;
import org.sugarj.cleardep.output.None;
import org.sugarj.test.cleardep.build.SimpleBuildTest;
import org.sugarj.test.cleardep.build.TrackingBuildManager;
import org.sugarj.test.cleardep.build.once.SimpleBuilder.TestBuilderInput;

public class CycleAtOnceBuilderTest extends SimpleBuildTest{


	@Override
	protected BuildRequest<?,?,?,?> requirementForInput(TestBuilderInput input) {
		return new BuildRequest<ArrayList<TestBuilderInput>,None, SimpleCyclicAtOnceBuilder, BuilderFactory<ArrayList<TestBuilderInput>, None, SimpleCyclicAtOnceBuilder>> (SimpleCyclicAtOnceBuilder.factory, CompileCycleAtOnceBuilder.singletonArrayList(input));
	}
	
	@Test
	public void buildCycle() throws IOException {
		TrackingBuildManager manager = buildMainFile();
		// TODO do some assertions here
	}

	
}
