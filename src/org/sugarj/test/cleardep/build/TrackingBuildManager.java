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
import org.sugarj.cleardep.output.BuildOutput;
import org.sugarj.common.path.Path;

public class TrackingBuildManager extends BuildManager {
	
	private List<Serializable> requiredInputs = new ArrayList<Serializable>();
	private List<Serializable> executedInputs = new ArrayList<Serializable>();
	
	public TrackingBuildManager() {
		super(null);
	}
	
	
	public <In extends Serializable, Out extends BuildOutput, B extends Builder<In, Out>, F extends BuilderFactory<In, Out, B>> BuildUnit<Out> require(
			F factory, In input) throws IOException {
		requiredInputs.add(input);
		return super.require(new BuildRequest<>(factory, input));
	}
	
	@Override
	public <In extends Serializable, Out extends BuildOutput, B extends Builder<In, Out>, F extends BuilderFactory<In, Out, B>> BuildUnit<Out> require(
			BuildRequest<In, Out, B, F> buildReq) throws IOException {
		requiredInputs.add(buildReq.input);
		return super.require(buildReq);
	}
	
	@Override
	protected <In extends Serializable, Out extends BuildOutput, B extends Builder<In, Out>, F extends BuilderFactory<In, Out, B>> BuildUnit<Out> executeBuilder(
			Builder<In, Out> builder, Path dep,
			BuildRequest<In, Out, B, F> buildReq) throws IOException {
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
