package org.sugarj.test.cleardep.build;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.build.BuildCycleException;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.BuildRequirement;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.build.RequiredBuilderFailed;
import org.sugarj.cleardep.stamp.ContentHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;

public class BuildManagerCycleDetectionTest {

	public static final BuilderFactory<AbsolutePath, CompilationUnit, TestBuilder> testFactory = new BuilderFactory<AbsolutePath, CompilationUnit, BuildManagerCycleDetectionTest.TestBuilder>() {

		/**
	 * 
	 */
		private static final long serialVersionUID = 3231801709410953205L;

		@Override
		public TestBuilder makeBuilder(AbsolutePath input, BuildManager manager) {
			return new TestBuilder(input, manager);
		}

	};

	private static class TestBuilder extends
			Builder<AbsolutePath, CompilationUnit> {

		private TestBuilder(AbsolutePath input, BuildManager manager) {
			super(input, testFactory, manager);
		}

		@Override
		protected String taskDescription() {
			return "Test Builder";
		}

		@Override
		protected Path persistentPath() {
			return FileCommands.replaceExtension(input, "dep");
		}

		@Override
		protected Class<CompilationUnit> resultClass() {
			return CompilationUnit.class;
		}

		@Override
		protected Stamper defaultStamper() {
			return ContentHashStamper.instance;
		}

		@Override
		protected void build(CompilationUnit result) throws IOException {
			require(testFactory, input);
		}

	}

	@Test
	public void testCyclesDetected() throws IOException {

		try {
			BuildManager manager = new BuildManager();
			manager.require(new BuildRequirement<AbsolutePath, CompilationUnit, TestBuilder, BuilderFactory<AbsolutePath, CompilationUnit, TestBuilder>>(testFactory, new AbsolutePath(
					new File("testdata/Test.txt").getAbsolutePath())));
		} catch (RequiredBuilderFailed e) {
			assertTrue("Cause is not a cycle",
					e.getCause() instanceof BuildCycleException);
		}

	}

}
