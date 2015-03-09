package org.sugarj.test.cleardep.build.cycle.fixpoint;

import org.sugarj.cleardep.build.FixpointCycleSupport;

public class NumericCycleSupport extends FixpointCycleSupport {

	public NumericCycleSupport() {
		super(entry(GCDBuilder.factory, FileInput.class), entry(ModuloBuilder.factory, FileInput.class), entry(DivideByBuilder.factory, FileInput.class));
	}
	
}
