package org.sugarj.test.cleardep.build;

import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.build.BuildRequirement;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public class TestRequirement extends BuildRequirement<TestBuilderInput, CompilationUnit, SimpleBuilder, BuilderFactory<TestBuilderInput, CompilationUnit, SimpleBuilder>> {

	public TestRequirement(
			BuilderFactory<TestBuilderInput, CompilationUnit, SimpleBuilder> factory,
			TestBuilderInput input) {
		super(factory, input);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2323402524730617911L;
	

}
