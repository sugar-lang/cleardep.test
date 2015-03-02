package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.BuildRequest;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.common.path.Path;

public class TrackingBuildManager extends BuildManager {
	
	private List<Serializable> requiredInputs = new ArrayList<Serializable>();
	private List<Serializable> executedInputs = new ArrayList<Serializable>();
	
	@Override
	public <T extends Serializable, E extends BuildUnit, B extends Builder<T, E>, F extends BuilderFactory<T, E, B>> E require(
			BuildRequest<T, E, B, F> buildReq) throws IOException {
		requiredInputs.add(buildReq.input);
		return super.require(buildReq);
	}
	
	
	@Override
	 protected <T extends Serializable, E extends BuildUnit, B extends Builder<T, E>, F extends BuilderFactory<T, E, B>> E executeBuilder(Builder<T, E> builder, Path dep, BuildRequest<T, E, B, F> buildReq) throws IOException {

		executedInputs.add(buildReq.input);
		return super.executeBuilder(builder, dep, buildReq);
	}
	
	public List<Serializable> getRequiredInputs() {
		return requiredInputs;
	}
	public List<Serializable> getExecutedInputs() {
		return executedInputs;
	}

}
