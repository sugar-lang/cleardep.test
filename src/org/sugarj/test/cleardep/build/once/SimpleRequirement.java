package org.sugarj.test.cleardep.build.once;

import org.sugarj.cleardep.build.BuildRequest;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.output.None;
import org.sugarj.test.cleardep.build.once.SimpleBuilder.TestBuilderInput;

public class SimpleRequirement extends BuildRequest<TestBuilderInput, None, SimpleBuilder, BuilderFactory<TestBuilderInput, None, SimpleBuilder>> {

	public SimpleRequirement(
			BuilderFactory<TestBuilderInput, None, SimpleBuilder> factory,
			TestBuilderInput input) {
		super(factory, input);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2323402524730617911L;
	

}
