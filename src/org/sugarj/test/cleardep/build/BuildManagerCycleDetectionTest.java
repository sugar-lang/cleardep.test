package org.sugarj.test.cleardep.build;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.sugarj.cleardep.SimpleCompilationUnit;
import org.sugarj.cleardep.SimpleMode;
import org.sugarj.cleardep.build.BuildCycleException;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.build.RequiredBuilderFailed;
import org.sugarj.cleardep.stamp.ContentHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;

public class BuildManagerCycleDetectionTest {

	public static final BuilderFactory<AbsolutePath, SimpleCompilationUnit, TestBuilder> testFactory = new BuilderFactory<AbsolutePath, SimpleCompilationUnit, BuildManagerCycleDetectionTest.TestBuilder>() {

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
			Builder<AbsolutePath, SimpleCompilationUnit> {

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
		protected Class<SimpleCompilationUnit> resultClass() {
			return SimpleCompilationUnit.class;
		}

		@Override
		protected Stamper defaultStamper() {
			return ContentHashStamper.instance;
		}

		@Override
		protected void build(SimpleCompilationUnit result) throws IOException {
			require(testFactory, input, new SimpleMode());
		}

	}

	@Test
	public void testCyclesDetected() throws IOException {

		try {
			BuildManager manager = new BuildManager();
			TestBuilder builder = testFactory.makeBuilder(new AbsolutePath(
					new File("testdata/Test.txt").getAbsolutePath()), manager);
			manager.require(builder, new SimpleMode());
		} catch (RequiredBuilderFailed e) {
			assertTrue("Cause is not a cycle",
					e.getCause() instanceof BuildCycleException);
		}

	}

}
