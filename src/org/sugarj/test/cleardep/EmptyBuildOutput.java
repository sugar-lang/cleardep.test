package org.sugarj.test.cleardep;

import org.sugarj.cleardep.output.BuildOutput;

public final class EmptyBuildOutput implements BuildOutput {

	/**
	 * 
	 */
	private static final long serialVersionUID = -931710373384110638L;
	
	public static final EmptyBuildOutput instance = new EmptyBuildOutput();
	
	private EmptyBuildOutput(){}

	@Override
	public boolean isConsistent() {
		return true;
	}

}
