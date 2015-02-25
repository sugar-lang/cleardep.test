package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.BuildRequirement;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;

public class TrackingBuildManager extends BuildManager {
	
	private List<Serializable> requiredInputs = new ArrayList<Serializable>();
	private List<Serializable> executedInputs = new ArrayList<Serializable>();
	
	@Override
	public <T extends Serializable, E extends CompilationUnit, B extends Builder<T, E>, F extends BuilderFactory<T, E, B>> E require(
			BuildRequirement<T, E, B, F> buildReq) throws IOException {
		requiredInputs.add(buildReq.input);
		return super.require(buildReq);
	}
	
	
	@Override
	protected <T extends Serializable, E extends CompilationUnit, B extends Builder<T, E>, F extends BuilderFactory<T, E, B>> E executeBuilder(
			Builder<T, E> builder, E depResult,
			BuildRequirement<T, E, ?, ?> buildReq) throws IOException {
		executedInputs.add(buildReq.input);
		return super.executeBuilder(builder, depResult, buildReq);
	}
	
	public List<Serializable> getRequiredInputs() {
		return requiredInputs;
	}
	public List<Serializable> getExecutedInputs() {
		return executedInputs;
	}

}
