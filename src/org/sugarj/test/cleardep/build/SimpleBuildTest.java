package org.sugarj.test.cleardep.build;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.sugarj.cleardep.build.BuildRequest;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public abstract class SimpleBuildTest {

	

	@Rule
	public TestName name = new TestName();
	

	private AbsolutePath basePath ;
	protected AbsolutePath testBasePath;
	
	protected RelativePath getRelativeFile(String name) {
		return new RelativePath(testBasePath, name);
	}
	
	@Before
	public void initializeTestEnvironment() throws IOException {
		basePath = new AbsolutePath(new File(
				"testdata/"+this.getClass().getSimpleName()).getAbsolutePath());
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
		
		System.out.println();
		System.out.println("====== Execute test " + name.getMethodName() + " ======");
	}
	

	protected TrackingBuildManager buildMainFile() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		buildMainFile(manager);
		return manager;
	}

	protected void buildMainFile(TrackingBuildManager manager) throws IOException {
		System.out.println("====== Build Project .... ======");
		BuildRequest<?,?,?,?> req = requirementForInput(new TestBuilderInput(testBasePath, getRelativeFile("main.txt")));
		manager.build(req);
	}
	
	protected abstract BuildRequest<?,?,?,?> requirementForInput(TestBuilderInput input);
	
	
}
