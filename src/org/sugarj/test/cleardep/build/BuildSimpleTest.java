package org.sugarj.test.cleardep.build;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.SimpleCompilationUnit;
import org.sugarj.cleardep.SimpleMode;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.TestBuilder.TestBuilderInput;

import static org.sugarj.test.cleardep.CompilationUnitTestUtils.*;


public class BuildSimpleTest {
	
	private static AbsolutePath basePath = new AbsolutePath(new File("testdata/BuildSimpleTest/").getAbsolutePath());
	
	@Rule public TestName name = new TestName();
	
	private AbsolutePath testBasePath;
	 
	private RelativePath mainFile;
	private RelativePath dep1File;
	private RelativePath dep2File;
	private RelativePath dep2_1File ;
	
	private List<RelativePath> allFiles ;
	
		
	@Before
	public void clean() throws IOException {
		testBasePath = new AbsolutePath(basePath.getAbsolutePath() + "/" + name.getMethodName());
		
		FileCommands.delete(testBasePath);
		FileCommands.createDir(testBasePath);
		
		for (RelativePath path : FileCommands.listFiles(basePath, new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		})) {
			FileCommands.copyFile(path, new RelativePath(testBasePath, path.getRelativePath()));
		}
		
		 mainFile = new RelativePath(testBasePath, "main.txt");
		dep1File = new RelativePath(testBasePath, "dep1.txt");
		dep2File = new RelativePath(testBasePath, "dep2.txt");
		dep2_1File = new RelativePath(testBasePath, "dep2-1.txt");
		allFiles = Arrays.asList(mainFile, dep1File, dep2File, dep2_1File);
	}
	
	private TestBuilder buildMainFile(TestBuildManager manager) throws IOException{
		TestBuildContext context = new TestBuildContext(manager, testBasePath);
		TestBuilder builder = TestBuilder.factory.makeBuilder(context);
		builder.require(new TestBuilderInput(mainFile), new SimpleMode());
		return builder;
	}
	
	private void addInputFileContent(RelativePath path, String newContent) throws IOException{
		java.nio.file.Path ioPath = Paths.get(path.getAbsolutePath());
		List<String> lines = Files.readAllLines(ioPath);
		lines.add(newContent);
		Files.write(ioPath, lines);
	}
	
	private SimpleCompilationUnit unitForFile(RelativePath path, TestBuilder builder) throws IOException{
		Path depPath = builder.persistentPath(new TestBuilderInput(path));
		SimpleCompilationUnit unit = CompilationUnit.read(SimpleCompilationUnit.class, new SimpleMode(), null, depPath);
		return unit;
	}
	
	private List<RelativePath> inputToFileList(List<Serializable> inputs) {
		ArrayList<RelativePath> fileList = new ArrayList<>();
		for (Serializable s : inputs) {
			if (s instanceof TestBuilderInput) {
				fileList.add(((TestBuilderInput)s).getInputPath());
			} else {
				fail("Illegal input");
			}
		}
		return fileList;
	}
	
	private void validateOrder(List<RelativePath> paths, RelativePath before, RelativePath after ) {
		int beforeIndex = paths.indexOf(before);
		int afterIndex = paths.indexOf(after);
		assertNotEquals(-1, beforeIndex);
		assertNotEquals(-1, afterIndex);
		assertTrue(before.getRelativePath() + " not before " + after.getRelativePath(), beforeIndex < afterIndex);
	}
	
	@Test
	public void buildClean() throws IOException {
		TestBuildManager manager = new TestBuildManager();
		TestBuilder builder = buildMainFile(manager);
		// Now require that all compilationUnits are consistent
		for (RelativePath file : allFiles) {
			SimpleCompilationUnit unit = unitForFile(file, builder);
			assertNotNull("No unit was persisted for path: " + file.getRelativePath(), unit);
			assertTrue("Unit for " + file.getRelativePath() + " is not consistent", unit.isConsistent(null, new SimpleMode()));
		}
		List<RelativePath> requiredFiles = inputToFileList(manager.getRequiredInputs());
		validateOrder(requiredFiles, mainFile, dep2File);
		validateOrder(requiredFiles, mainFile, dep1File);
		validateOrder(requiredFiles, dep2File, dep2_1File);
		
		List<RelativePath> executedFiles = inputToFileList(manager.getRequiredInputs());
		validateOrder(executedFiles, mainFile, dep2File);
		validateOrder(executedFiles, mainFile, dep1File);
		validateOrder(executedFiles, dep2File, dep2_1File);
	}
	
	@Test
	public void buildRootInconsistent() throws IOException {
		TestBuildManager manager = new TestBuildManager();
		TestBuilder builder =buildMainFile(manager);
		addInputFileContent(mainFile, "New content");
		assertFalse("Main file is not inconsistent after change", unitForFile(mainFile, builder).isConsistent(null, new SimpleMode()));
		// Rebuild
		manager = new TestBuildManager();
		builder = buildMainFile(manager);
		List<RelativePath> requiredFiles = inputToFileList(manager.getRequiredInputs());
		assertEquals("Wrong filed required", set(mainFile, dep1File, dep2File), set(requiredFiles));
		validateOrder(requiredFiles, mainFile, dep2File);
		validateOrder(requiredFiles, mainFile, dep1File);
		
		List<RelativePath> executedFiles = inputToFileList(manager.getExecutedInputs());
		assertEquals("Wrong files executed", set(mainFile), set(executedFiles));
	}

}