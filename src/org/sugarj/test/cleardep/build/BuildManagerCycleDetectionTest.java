package org.sugarj.test.cleardep.build;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.SimpleCompilationUnit;
import org.sugarj.cleardep.SimpleMode;
import org.sugarj.cleardep.build.BuildContext;
import org.sugarj.cleardep.build.BuildCycleException;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.RequiredBuilderFailed;
import org.sugarj.cleardep.stamp.ContentHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;

public class BuildManagerCycleDetectionTest {
  
  private static class TestBuilder extends Builder<BuildContext, AbsolutePath, SimpleCompilationUnit> {

    public TestBuilder(BuildContext context) {
      super(context);
    }

    @Override
    protected String taskDescription(AbsolutePath input) {
      return "Test Builder";
    }

    @Override
    protected Path persistentPath(AbsolutePath input) {
      return FileCommands.replaceExtension(input ,"dep");
    }

    @Override
    protected Class<SimpleCompilationUnit> resultClass() {
      return SimpleCompilationUnit.class;
    }

    @Override
    protected Stamper defaultStamper() {
      return ContentHashStamper.instance;
    }

    @Override
    protected void build(SimpleCompilationUnit result, AbsolutePath input) throws IOException {
      this.require(input, new SimpleMode());
    }
    
  }
  
  
  @Test
  public void testCyclesDetected() throws IOException{
    BuildContext context = new BuildContext(new BuildManager());
    TestBuilder builder = new TestBuilder(context);
    try {
    builder.require(new AbsolutePath(new File("testdata/Test.txt").getAbsolutePath()), new SimpleMode());
    } catch (RequiredBuilderFailed e) {
    	assertTrue("Cause is not a cycle", e.getCause() instanceof BuildCycleException);
    }
    
  }

}
