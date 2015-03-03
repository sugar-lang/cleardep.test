package org.sugarj.test.cleardep.build;

import org.sugarj.cleardep.output.BuildOutput;

public class TestOutput implements BuildOutput {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3021587324091793419L;

	@Override
	public boolean isConsistent() {
		return true;
	}
	
}