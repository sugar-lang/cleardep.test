package org.sugarj.test.cleardep.build.cycle.fixpoint;

import java.math.BigInteger;

import org.sugarj.cleardep.build.BuilderFactory;

public class GCDBuilder extends NumericBuilder {

	public static final BuilderFactory<FileInput, IntegerOutput, GCDBuilder> factory = new BuilderFactory<FileInput, IntegerOutput, GCDBuilder>() {

	
		/**
		 * 
		 */
		private static final long serialVersionUID = -8446980413610510702L;

		@Override
		public GCDBuilder makeBuilder(FileInput input) {
			return new GCDBuilder(input);
		}
	};
	
	public GCDBuilder(FileInput input) {
		super(input);
	}

	@Override
	protected Operator getOperator() {
		return new Operator() {
			
			@Override
			public int apply(int a, int b) {
				return new BigInteger(Integer.toString(a)).gcd(new BigInteger(Integer.toString(b))).intValue();
			}
		};
	}

	@Override
	protected String taskDescription() {
		return "GCD for " + this.input.getFile().getRelativePath();
	}

}
