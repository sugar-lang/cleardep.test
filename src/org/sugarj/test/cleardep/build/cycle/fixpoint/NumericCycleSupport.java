package org.sugarj.test.cleardep.build.cycle.fixpoint;

import org.sugarj.cleardep.build.FixpointCycleSupport;

public class NumericCycleSupport extends FixpointCycleSupport {

	public NumericCycleSupport() {
		super(GCDBuilder.factory, ModuloBuilder.factory,
				DivideByBuilder.factory);
	}

}
