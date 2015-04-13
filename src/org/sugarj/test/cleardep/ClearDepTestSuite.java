package org.sugarj.test.cleardep;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.sugarj.test.cleardep.build.BuildManagerCycleDetectionTest;
import org.sugarj.test.cleardep.build.RebuildInconsistentTest;
import org.sugarj.test.cleardep.build.cycle.fixpoint.test.FixpointCycleTestSuite;
import org.sugarj.test.cleardep.build.once.CycleAtOnceBuilderTest;

@RunWith(Suite.class)
@SuiteClasses({CompilationUnitVisitTest.class, BuildManagerCycleDetectionTest.class, CycleAtOnceBuilderTest.class, RebuildInconsistentTest.class, FixpointCycleTestSuite.class})
public class ClearDepTestSuite {

}
