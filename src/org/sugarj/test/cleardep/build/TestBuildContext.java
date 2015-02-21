package org.sugarj.test.cleardep.build;

import org.sugarj.cleardep.build.BuildContext;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.common.path.Path;

public class TestBuildContext extends BuildContext {

	
	private Path basePath;
	
	public TestBuildContext(BuildManager manager, Path basePath) {
		super(manager);
		this.basePath = basePath;
	}
	
	public Path getBasePath() {
		return basePath;
	}

}
