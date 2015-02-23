package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.Mode;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.Builder;

public class TrackingBuildManager extends BuildManager {
	
	private List<Serializable> requiredInputs = new ArrayList<Serializable>();
	private List<Serializable> executedInputs = new ArrayList<Serializable>();
	
	@Override
	public <T extends Serializable, E extends CompilationUnit> E require(
			Builder< T, E> builder,  Mode<E> mode) throws IOException {
		requiredInputs.add(builder.getInput());
		return super.require(builder, mode);
	}
	
	@Override
	protected < T extends Serializable, E extends CompilationUnit> E executeBuilder(
			Builder< T, E> builder,Mode<E> mode) throws IOException {
		executedInputs.add(builder.getInput());
		return super.executeBuilder(builder, mode);
	}
	
	public List<Serializable> getRequiredInputs() {
		return requiredInputs;
	}
	public List<Serializable> getExecutedInputs() {
		return executedInputs;
	}

}
