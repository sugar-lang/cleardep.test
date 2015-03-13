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
	
	private List<Serializable> requiredInputs = new ArrayList<>();
	private List<Serializable> executedInputs = new ArrayList<>();
	private List<Serializable> successfullyExecutedInputs = new ArrayList<>();
	
	public TrackingBuildManager() {
		super(null);
	}
	
	
	public <In extends Serializable, Out extends Serializable, B extends Builder<In, Out>, F extends BuilderFactory<In, Out, B>> BuildUnit<Out> require(
			BuildUnit<?> source, F factory, In input) throws IOException {
		requiredInputs.add(input);
		return super.require(source, new BuildRequest<>(factory, input));
	}
	
	@Override
	public <In extends Serializable, Out extends Serializable, B extends Builder<In, Out>, F extends BuilderFactory<In, Out, B>> BuildUnit<Out> require(
			BuildUnit<?> source, BuildRequest<In, Out, B, F> buildReq) throws IOException {
		requiredInputs.add(buildReq.input);
		return super.require(source, buildReq);
	}
	
	@Override
	protected <In extends Serializable, Out extends Serializable, B extends Builder<In, Out>, F extends BuilderFactory<In, Out, B>> BuildUnit<Out> executeBuilder(
			Builder<In, Out> builder, Path dep,
			BuildRequest<In, Out, B, F> buildReq) throws IOException {
		executedInputs.add(buildReq.input);
		BuildUnit<Out> result =  super.executeBuilder(builder, dep, buildReq);
		successfullyExecutedInputs.add(buildReq.input);
		return result;
	}
	
	public List<Serializable> getRequiredInputs() {
		return requiredInputs;
	}
	public List<Serializable> getExecutedInputs() {
		return executedInputs;
	}
	public List<Serializable> getSuccessfullyExecutedInputs() {
		return successfullyExecutedInputs;
	}

}
