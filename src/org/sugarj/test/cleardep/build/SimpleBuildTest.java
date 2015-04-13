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
		return buildMainFile(new TrackingBuildManager());
	}

	protected TrackingBuildManager buildMainFile(TrackingBuildManager manager) throws IOException {
		return buildFile(getRelativeFile("main.txt"), manager);
	}
	
	protected final TrackingBuildManager buildFile(RelativePath path) throws IOException {
		return buildFile(path, new TrackingBuildManager());
	}
	
	protected final TrackingBuildManager buildFile(RelativePath path, TrackingBuildManager manager) throws IOException {
		System.out.println("====== Build " + path.getRelativePath()+" ======");
		BuildRequest<?,?,?,?> req = requirementForInput(new TestBuilderInput(testBasePath, getRelativeFile("main.txt")));
		manager.require(null, req);
		return manager;
	}
	
	protected abstract BuildRequest<?,?,?,?> requirementForInput(TestBuilderInput input);
	
	
}
