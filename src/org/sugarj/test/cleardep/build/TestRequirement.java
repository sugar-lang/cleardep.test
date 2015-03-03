package org.sugarj.test.cleardep.build;

import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildRequest;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public class TestRequirement extends BuildRequest<TestBuilderInput, TestOutput, SimpleBuilder, BuilderFactory<TestBuilderInput, TestOutput, SimpleBuilder>> {

	public TestRequirement(
			BuilderFactory<TestBuilderInput, TestOutput, SimpleBuilder> factory,
			TestBuilderInput input) {
		super(factory, input);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2323402524730617911L;
	

}
