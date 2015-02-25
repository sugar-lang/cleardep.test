package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.CompilationUnit.State;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.BuildRequirement;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.stamp.ContentHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public class SimpleBuilder extends Builder<TestBuilderInput, CompilationUnit> {

	public static BuilderFactory<TestBuilderInput, CompilationUnit, SimpleBuilder> factory = new BuilderFactory<SimpleBuilder.TestBuilderInput, CompilationUnit, SimpleBuilder>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6787456873371906431L;

		@Override
		public SimpleBuilder makeBuilder(TestBuilderInput input,
				BuildManager manager) {
			return new SimpleBuilder(input, manager);
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
			Objects.requireNonNull(basePath);
			Objects.requireNonNull(inputPath);
			Objects.requireNonNull(inputPath.getBasePath());
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

	private SimpleBuilder(TestBuilderInput input, BuildManager manager) {
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
	protected Class<CompilationUnit> resultClass() {
		return CompilationUnit.class;
	}

	@Override
	protected Stamper defaultStamper() {
		return ContentHashStamper.instance;
	}

	@Override
	protected void build(CompilationUnit result) throws IOException {
		result.addSourceArtifact(input.inputPath);
		List<String> allLines = FileCommands.readFileLines(input.inputPath);

		List<String> contentLines = new ArrayList<String>();

		for (String line : allLines) {
			if (line.startsWith("Dep:")) {
				String depFile = line.substring(4);
				TestBuilderInput depInput = new TestBuilderInput(
						input.basePath, new RelativePath(input.getBasePath(),
								depFile));
				TestRequirement req = new TestRequirement(factory, depInput);
				require(req);
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
