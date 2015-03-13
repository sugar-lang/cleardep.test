package org.sugarj.test.cleardep.build.cycle.fixpoint.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.BuildRequest;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.ScopedBuildTest;
import org.sugarj.test.cleardep.build.TrackingBuildManager;
import org.sugarj.test.cleardep.build.cycle.fixpoint.FileInput;
import org.sugarj.test.cleardep.build.cycle.fixpoint.FileUtils;
import org.sugarj.test.cleardep.build.cycle.fixpoint.IntegerOutput;
import org.sugarj.test.cleardep.build.cycle.fixpoint.ModuloBuilder;

import static org.sugarj.test.cleardep.build.cycle.fixpoint.test.FixpointCycleTestSuite.*;
import static org.sugarj.test.cleardep.build.Validators.*;


public class GCDHomogeneousCycleTest extends ScopedBuildTest {

	private RelativePath mainFile;
	private RelativePath cycle_gcd1File;
	private RelativePath cycle_gcd2File;
	private BuildRequest<FileInput, IntegerOutput, ModuloBuilder, BuilderFactory<FileInput, IntegerOutput, ModuloBuilder>> mainBuildRequest;
	
	@Before
	public void initFiles() {
		mainFile = getRelativeFile("cyclemodmain.modulo");
		cycle_gcd1File = getRelativeFile("cycle_gcd1.gcd");
		cycle_gcd2File = getRelativeFile("cycle_gcd2.gcd");
		mainBuildRequest  = new BuildRequest<>(
				ModuloBuilder.factory, new FileInput(testBasePath, mainFile));
	}
	
	@Override
	protected String getTestFolderName() {
		return FixpointCycleTestSuite.FIXPOINT_BUILDER_CYCLE_TEST;
	}
	
	private void assertAllFilesConsistent() throws IOException{
		for (RelativePath path : Arrays.asList(mainFile, cycle_gcd1File, cycle_gcd2File)) {
			assertTrue("File " + path.getRelativePath() + " is not consistent", unitForFile(path).isConsistent(null));
		}
 	}

	@Test (timeout = 1000)
	public void testBuildGCDCycle() throws IOException {
		BuildUnit<IntegerOutput> resultUnit = new TrackingBuildManager()
				.require(null,mainBuildRequest);
		assertEquals("Compiliding GCD cycle has wrong result", 0, resultUnit
				.getBuildResult().getResult());
		assertAllFilesConsistent();
	}

	@Test (timeout = 1000)
	public void testRebuildRootUnitInconsistent() throws IOException {

		// Do a first clean build
		BuildManager.build(mainBuildRequest);

		// Then make the root inconsistent
		FileUtils.writeIntToFile(19, mainFile);

		TrackingBuildManager manager = new TrackingBuildManager();
		BuildUnit<IntegerOutput> resultUnit = manager.require(null,mainBuildRequest);
		// Assert that the new result is correct
		assertEquals("Rebuilding GCD cycle with inconsistent has wrong result", 4, resultUnit
				.getBuildResult().getResult());
		
		// Primitive check
		assertAllFilesConsistent();
		
		// Check that only main is executed
		validateThat(executedFilesOf(manager).containsSameElements(mainFile));
		
		// And that main is required before the other ones. Because main refers to gcd1, this should be before gcd2
		validateThat(in(requiredFilesOf(manager)).is(mainFile).before(cycle_gcd1File));
		validateThat(in(requiredFilesOf(manager)).is(cycle_gcd1File).before(cycle_gcd2File));

	}
	
	@Test (timeout = 1000)
	public void testRebuildCycle1UnitInconsistent() throws IOException {

		// Do a first clean build
		BuildManager.build(mainBuildRequest);
		assertAllFilesConsistent();

		// Then make the cycle1 inconsistent
		FileCommands.delete(unitForFile(cycle_gcd1File).getBuildResult().getResultFile());

		TrackingBuildManager manager = new TrackingBuildManager();
		BuildUnit<IntegerOutput> resultUnit = manager.require(null,mainBuildRequest);
		// Assert that the new result is correct
		assertEquals("Rebuilding GCD cycle with inconsistent has wrong result", 0, resultUnit
				.getBuildResult().getResult());
		
		// Primitive check
		assertAllFilesConsistent();
		
		// Check that only the cycle is executed
		// Main must not be executed because the cycle produces the same result as before
		validateThat(executedFilesOf(manager).containsSameElements(cycle_gcd1File, cycle_gcd2File));
		
		// And that main is required before the other ones. Because main refers to gcd1, this should be before gcd2
		validateThat(in(requiredFilesOf(manager)).is(mainFile).before(cycle_gcd1File));
		validateThat(in(requiredFilesOf(manager)).is(cycle_gcd1File).before(cycle_gcd2File));

	}

}
