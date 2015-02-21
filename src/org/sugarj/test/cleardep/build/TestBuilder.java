package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.CompilationUnit.State;
import org.sugarj.cleardep.SimpleCompilationUnit;
import org.sugarj.cleardep.SimpleMode;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.stamp.ContentHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.TestBuilder.TestBuilderInput;

public class TestBuilder extends Builder<TestBuildContext, TestBuilderInput, SimpleCompilationUnit>{

	public static BuilderFactory<TestBuildContext, TestBuilderInput, SimpleCompilationUnit, TestBuilder> factory = new BuilderFactory<TestBuildContext, TestBuilder.TestBuilderInput, SimpleCompilationUnit, TestBuilder>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6787456873371906431L;

		@Override
		public TestBuilder makeBuilder(TestBuildContext context) {
			return new TestBuilder(context);
		}
	};
	
	public static class TestBuilderInput implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6657909750424698658L;
		private RelativePath inputPath;

		public TestBuilderInput(RelativePath inputPath) {
			super();
			this.inputPath = inputPath;
		}
		
		public RelativePath getInputPath() {
			return inputPath;
		}
	}
	

	private TestBuilder(TestBuildContext context) {
		super(context, factory);
	}

	@Override
	protected String taskDescription(TestBuilderInput input) {
		return "Test Builder";
	}

	@Override
	protected Path persistentPath(TestBuilderInput input) {
		return FileCommands.addExtension(input.inputPath, "dep");
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
	protected void build(SimpleCompilationUnit result, TestBuilderInput input)
			throws IOException {
		result.addSourceArtifact(input.inputPath);
		List<String> allLines = Files.readAllLines(Paths.get(input.inputPath.getAbsolutePath()));
		
		List<String> contentLines = new ArrayList<String>();
		
		for (String line : allLines) {
			if (line.startsWith("Dep:")) {
				String depFile = line.substring(4);
				CompilationUnit dep = factory.makeBuilder(context).require(new TestBuilderInput(new RelativePath(context.getBasePath(), depFile)), new SimpleMode());
				result.addModuleDependency(dep);
			} else {
				contentLines.add(line);
			}
		}
		
		// Write the content to a generated file
		Path generatedFile = FileCommands.addExtension(input.inputPath, "gen");
		Files.write(Paths.get(generatedFile.getAbsolutePath()), contentLines);
		result.addGeneratedFile(generatedFile);
		result.setState(State.finished(true));
	}

}
