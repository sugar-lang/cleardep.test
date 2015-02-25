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
import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public class BuildSimpleTest {

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
	public void clean() throws IOException {
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

	private void buildMainFile(TrackingBuildManager manager) throws IOException {
		System.out.println("====== Build Project .... ======");
		TestRequirement req = new TestRequirement(SimpleBuilder.factory, new TestBuilderInput(testBasePath, mainFile));
		manager.require(req);
	}

	private void addInputFileContent(RelativePath path, String newContent)
			throws IOException {
		List<String> lines = FileCommands.readFileLines(path);
		lines.add(newContent);
		FileCommands.writeLinesFile(path, lines);
	}
	
	private void addInputFileDep(RelativePath path, RelativePath dep)
			throws IOException {
		List<String> lines = FileCommands.readFileLines(path);
		lines.add("Dep:"+dep.getRelativePath());
		FileCommands.writeLinesFile(path, lines);
	}

	private CompilationUnit unitForFile(RelativePath path)
			throws IOException {
		TestRequirement req = new TestRequirement(SimpleBuilder.factory,new TestBuilderInput(testBasePath, path));
		
		CompilationUnit unit = CompilationUnit.read(
				CompilationUnit.class, req.factory.makeBuilder(req.input, new BuildManager()).persistentPath(), req);
		return unit;
	}

	private List<RelativePath> inputToFileList(List<Serializable> inputs) {
		ArrayList<RelativePath> fileList = new ArrayList<>();
		for (Serializable s : inputs) {
			if (s instanceof TestBuilderInput) {
				fileList.add(((TestBuilderInput) s).getInputPath());
			} else {
				fail("Illegal input");
			}
		}
		return fileList;
	}

	private void validateOrder(List<RelativePath> paths, RelativePath before,
			RelativePath after) {
		int beforeIndex = paths.indexOf(before);
		int afterIndex = paths.indexOf(after);
		
		assertTrue(beforeIndex != -1);
		assertTrue(afterIndex != -1);
		assertTrue(
				before.getRelativePath() + " not before "
						+ after.getRelativePath(), beforeIndex < afterIndex);
	}

	@Test
	public void testBuildClean() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		buildMainFile(manager);
		// Now require that all compilationUnits are consistent
		for (RelativePath file : allFiles) {
			CompilationUnit unit = unitForFile(file);
			assertNotNull(
					"No unit was persisted for path: " + file.getRelativePath(),
					unit);
			assertTrue("Unit for " + file.getRelativePath()
					+ " is not consistent",
					unit.isConsistent(null));
		}
		List<RelativePath> requiredFiles = inputToFileList(manager
				.getRequiredInputs());
		validateOrder(requiredFiles, mainFile, dep2File);
		validateOrder(requiredFiles, mainFile, dep1File);
		validateOrder(requiredFiles, dep2File, dep2_1File);

		List<RelativePath> executedFiles = inputToFileList(manager
				.getRequiredInputs());
		validateOrder(executedFiles, mainFile, dep2File);
		validateOrder(executedFiles, mainFile, dep1File);
		validateOrder(executedFiles, dep2File, dep2_1File);
	}

	@Test
	public void testBuildRootInconsistent() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		buildMainFile(manager);

		addInputFileContent(mainFile, "New content");
		assertFalse("Main file is not inconsistent after change",
				unitForFile(mainFile).isConsistent(null));
		// Rebuild
		manager = new TrackingBuildManager();
		buildMainFile(manager);
		List<RelativePath> requiredFiles = inputToFileList(manager
				.getRequiredInputs());
		assertEquals("Wrong filed required", set(mainFile, dep1File, dep2File),
				set(requiredFiles));
		validateOrder(requiredFiles, mainFile, dep2File);
		validateOrder(requiredFiles, mainFile, dep1File);

		List<RelativePath> executedFiles = inputToFileList(manager
				.getExecutedInputs());
		assertEquals("Wrong files executed", set(mainFile), set(executedFiles));
	}

	@Test
	public void testBuildLeafInconsistent() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		buildMainFile(manager);

		addInputFileContent(dep2_1File, "New content");
		assertFalse("dep2_1File file is not inconsistent after change",
				unitForFile(dep2_1File).isConsistent(null));
		// Rebuild
		manager = new TrackingBuildManager();
		buildMainFile(manager);
		List<RelativePath> requiredFiles = inputToFileList(manager
				.getRequiredInputs());
		assertEquals("Wrong files required", set(mainFile, dep2_1File),
				set(requiredFiles));
		validateOrder(requiredFiles, mainFile, dep2_1File);

		List<RelativePath> executedFiles = inputToFileList(manager
				.getExecutedInputs());
		assertEquals("Wrong files executed", set(dep2_1File),
				set(executedFiles));
	}

	@Test
	public void testBuildLeafInconsistentNewDep() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		buildMainFile(manager);
		
		

		addInputFileContent(dep1File, "New content");
		
		addInputFileDep(dep2_1File, dep1File);
		assertFalse("dep2_1File file is not inconsistent after change",
				unitForFile(dep2_1File).isConsistent(null));
		assertFalse("dep1File file is not inconsistent after change",
				unitForFile(dep1File).isConsistent(null));
		
		// Rebuild
		manager = new TrackingBuildManager();
		buildMainFile(manager);
		List<RelativePath> requiredFiles = inputToFileList(manager
				.getRequiredInputs());
		assertEquals("Wrong files required", set(mainFile, dep2_1File,dep1File),
				set(requiredFiles));
		validateOrder(requiredFiles, mainFile, dep2_1File);

		List<RelativePath> executedFiles = inputToFileList(manager
				.getExecutedInputs());
		assertEquals("Wrong files executed", set(dep2_1File, dep1File),
				set(executedFiles));
		// Cannot validate the order, because dep1File may be scheduled before 2_1
		// but 2_1 can require dep1 if 2_1 is scheduled before 1;
	}

}
