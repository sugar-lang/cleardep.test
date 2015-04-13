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
import org.sugarj.test.cleardep.build.once.SimpleBuilder.TestBuilderInput;

public abstract class SimpleBuildTest extends ScopedBuildTest{
	
	@Override
	protected String getTestFolderName() {
		return this.getClass().getSimpleName();
	}

	protected TrackingBuildManager buildMainFile() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		buildMainFile(manager);
		return manager;
	}

	protected void buildMainFile(TrackingBuildManager manager) throws IOException {
		System.out.println("====== Build Project .... ======");
		BuildRequest<?,?,?,?> req = requirementForInput(new TestBuilderInput(testBasePath, getRelativeFile("main.txt")));
		manager.require(null, req);
	}
	
	protected abstract BuildRequest<?,?,?,?> requirementForInput(TestBuilderInput input);
	
	
}
