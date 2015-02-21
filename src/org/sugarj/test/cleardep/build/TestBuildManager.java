package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.Mode;
import org.sugarj.cleardep.build.BuildContext;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.Builder;

public class TestBuildManager extends BuildManager {
	
	private List<Serializable> requiredInputs = new ArrayList<Serializable>();
	private List<Serializable> executedInputs = new ArrayList<Serializable>();
	
	@Override
	public <C extends BuildContext, T extends Serializable, E extends CompilationUnit> E require(
			Builder<C, T, E> builder, T input, Mode<E> mode) throws IOException {
		requiredInputs.add(input);
		return super.require(builder, input, mode);
	}
	
	@Override
	protected <C extends BuildContext, T extends Serializable, E extends CompilationUnit> E executeBuilder(
			Builder<C, T, E> builder, T input, Mode<E> mode) throws IOException {
		executedInputs.add(input);
		return super.executeBuilder(builder, input, mode);
	}
	
	public List<Serializable> getRequiredInputs() {
		return requiredInputs;
	}
	public List<Serializable> getExecutedInputs() {
		return executedInputs;
	}

}
