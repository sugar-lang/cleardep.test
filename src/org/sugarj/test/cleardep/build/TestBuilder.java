package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.CompilationUnit.State;
import org.sugarj.cleardep.SimpleCompilationUnit;
import org.sugarj.cleardep.SimpleMode;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.stamp.ContentHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.TestBuilder.TestBuilderInput;

public class TestBuilder extends Builder< TestBuilderInput, SimpleCompilationUnit>{

	public static BuilderFactory< TestBuilderInput, SimpleCompilationUnit, TestBuilder> factory = new BuilderFactory< TestBuilder.TestBuilderInput, SimpleCompilationUnit, TestBuilder>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6787456873371906431L;

		@Override
		public TestBuilder makeBuilder(TestBuilderInput input, BuildManager manager) {
			return new TestBuilder(input, manager);
		}
	};
	
	public static class TestBuilderInput implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6657909750424698658L;
		private RelativePath inputPath;
		private Path basePath;

		public TestBuilderInput(Path basePath, RelativePath inputPath) {
			super();
			this.inputPath = inputPath;
			this.basePath = basePath;
		}
		
		public RelativePath getInputPath() {
			return inputPath;
		}
		
		public Path getBasePath() {
			return basePath;
		}
	}
	

	private TestBuilder(TestBuilderInput input, BuildManager manager) {
		super(input, factory, manager);
	}

	@Override
	protected String taskDescription() {
		return "Test Builder for " + input.getInputPath().getRelativePath();
	}

	@Override
	protected Path persistentPath() {
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
	protected void build(SimpleCompilationUnit result)
			throws IOException {
		result.addSourceArtifact(input.inputPath);
		List<String> allLines = FileCommands.readFileLines(input.inputPath);
		
		List<String> contentLines = new ArrayList<String>();
		
		for (String line : allLines) {
			if (line.startsWith("Dep:")) {
				String depFile = line.substring(4);
				TestBuilderInput depInput = new TestBuilderInput(input.basePath, new RelativePath(input.getBasePath(), depFile));
				CompilationUnit dep = require(factory, depInput, new SimpleMode());
				result.addModuleDependency(dep);
			} else {
				contentLines.add(line);
			}
		}
		
		// Write the content to a generated file
		Path generatedFile = FileCommands.addExtension(input.inputPath, "gen");
		FileCommands.writeLinesFile(generatedFile, contentLines);
		result.addGeneratedFile(generatedFile);
		result.setState(State.finished(true));
	}

}
