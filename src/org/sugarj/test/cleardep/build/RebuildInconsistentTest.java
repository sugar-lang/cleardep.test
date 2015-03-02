package org.sugarj.test.cleardep.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.sugarj.test.cleardep.CompilationUnitTestUtils.set;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

import static org.sugarj.test.cleardep.build.SimpleBuildUtilities.*;

import static org.sugarj.test.cleardep.build.Validators.*;
public class RebuildInconsistentTest {

	private static AbsolutePath basePath = new AbsolutePath(new File(
			"testdata/BuildSimpleTest/").getAbsolutePath());

	@Rule
	public TestName name = new TestName();

	private AbsolutePath testBasePath;

	private RelativePath mainFile;
	private RelativePath dep1File;
	private RelativePath dep2File;
	private RelativePath dep2_1File;

	private List<RelativePath> allFiles;

	@Before
	public void makeConsistentState() throws IOException{
		clean();
		buildClean();
	}
	
	
	private void clean() throws IOException {
		testBasePath = new AbsolutePath(basePath.getAbsolutePath() + "/"
				+ name.getMethodName());

		FileCommands.delete(testBasePath);
		FileCommands.createDir(testBasePath);

		for (RelativePath path : FileCommands.listFiles(basePath,
				new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return pathname.isFile();
					}
				})) {
			FileCommands.copyFile(path,
					new RelativePath(testBasePath, path.getRelativePath()));
		}

		mainFile = new RelativePath(testBasePath, "main.txt");
		dep1File = new RelativePath(testBasePath, "dep1.txt");
		dep2File = new RelativePath(testBasePath, "dep2.txt");
		dep2_1File = new RelativePath(testBasePath, "dep2-1.txt");
		allFiles = Arrays.asList(mainFile, dep1File, dep2File, dep2_1File);
		
		System.out.println("====== Execute test " + name.getMethodName() + " ======");
		
	}
	
	private void buildClean() throws IOException {
		TrackingBuildManager manager = buildMainFile();
		// Now require that all compilationUnits are consistent
		for (RelativePath file : allFiles) {
			BuildUnit unit = unitForFile(file, testBasePath);
			assertNotNull(
					"No unit was persisted for path: " + file.getRelativePath(),
					unit);
			assertTrue("Unit for " + file.getRelativePath()
					+ " is not consistent",
					unit.isConsistent(null));
		}
		validateThat(in(requiredFilesOf(manager)).is(mainFile).before(dep2File, dep1File));
		validateThat(in(requiredFilesOf(manager)).is(dep2File).before(dep2_1File));

		validateThat(in(executedFilesOf(manager)).is(mainFile).before(dep1File, dep2File));
		validateThat(in(executedFilesOf(manager)).is(dep2File).before(dep2_1File));
	}
	
	private TrackingBuildManager buildMainFile() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		buildMainFile(manager);
		return manager;
	}

	private void buildMainFile(TrackingBuildManager manager) throws IOException {
		System.out.println("====== Build Project .... ======");
		TestRequirement req = new TestRequirement(SimpleBuilder.factory, new TestBuilderInput(testBasePath, mainFile));
		manager.require(req);
	}

	@Test
	public void testBuildRootInconsistent() throws IOException {

		addInputFileContent(mainFile, "New content");
		assertFalse("Main file is not inconsistent after change",
				unitForFile(mainFile, testBasePath).isConsistent(null));
		// Rebuild
		TrackingBuildManager manager = new TrackingBuildManager();
		buildMainFile(manager);

		validateThat(in(requiredFilesOf(manager)).is(mainFile).before(dep2File, dep1File));
		validateThat(executedFilesOf(manager).containsSameElements(mainFile));
	}

	@Test
	public void testBuildLeafInconsistent() throws IOException {

		addInputFileContent(dep2_1File, "New content");
		assertFalse("dep2_1File file is not inconsistent after change",
				unitForFile(dep2_1File, testBasePath).isConsistent(null));
		// Rebuild
		TrackingBuildManager manager = buildMainFile();
	
		validateThat(in(requiredFilesOf(manager)).is(mainFile).before(dep2_1File));
		validateThat(executedFilesOf(manager).containsSameElements(dep2_1File));

	}

	@Test
	public void testBuildLeafInconsistentNewDep() throws IOException {


		addInputFileContent(dep1File, "New content");
		
		addInputFileDep(dep2_1File, dep1File);
		assertFalse("dep2_1File file is not inconsistent after change",
				unitForFile(dep2_1File, testBasePath).isConsistent(null));
		assertFalse("dep1File file is not inconsistent after change",
				unitForFile(dep1File, testBasePath).isConsistent(null));
		
		// Rebuild
		TrackingBuildManager manager = buildMainFile();
		
		validateThat(in(requiredFilesOf(manager)).is(mainFile).before(dep2_1File));
		validateThat(executedFilesOf(manager).containsSameElements(dep2_1File, dep1File));
		
		// Cannot validate the order, because dep1File may be scheduled before 2_1
		// but 2_1 can require dep1 if 2_1 is scheduled before 1;
	}

}
