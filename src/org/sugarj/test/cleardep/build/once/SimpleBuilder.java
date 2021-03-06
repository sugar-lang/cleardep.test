package org.sugarj.test.cleardep.build.once;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.output.None;
import org.sugarj.cleardep.stamp.FileHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.once.SimpleBuilder.TestBuilderInput;

public class SimpleBuilder extends Builder<TestBuilderInput, None> {

	public static BuilderFactory<TestBuilderInput, None, SimpleBuilder> factory = new BuilderFactory<TestBuilderInput, None, SimpleBuilder>() {

	
		private static final long serialVersionUID = -6787456873371906431L;

		@Override
		public SimpleBuilder makeBuilder(TestBuilderInput input) {
			return new SimpleBuilder(input);
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
		
		@Override
		public String toString() {
			return this.getInputPath().getRelativePath();
		}
	}

	
	private SimpleBuilder(TestBuilderInput input) {
		super(input);
	}

	@Override
	protected String description() {
		return "Test Builder for " + input.getInputPath().getRelativePath();
	}

	@Override
	protected Path persistentPath() {
		return FileCommands.addExtension(input.inputPath, "dep");
	}

	@Override
	protected Stamper defaultStamper() {
		return FileHashStamper.instance;
	}

	@Override
	protected None build() throws IOException {
		require(input.inputPath);
		List<String> allLines = FileCommands.readFileLines(input.inputPath);

		List<String> contentLines = new ArrayList<String>();

		for (String line : allLines) {
			if (line.startsWith("Dep:")) {
				String depFile = line.substring(4);
				TestBuilderInput depInput = new TestBuilderInput(
						input.basePath, new RelativePath(input.getBasePath(),
								depFile));
				SimpleRequirement req = new SimpleRequirement(factory, depInput);
				requireBuild(req);
			} else {
				contentLines.add(line);
			}
		}

		// Write the content to a generated file
		Path generatedFile = FileCommands.addExtension(input.inputPath, "gen");
		FileCommands.writeLinesFile(generatedFile, contentLines);
		provide(generatedFile);
		setState(BuildUnit.State.finished(true));
		return None.val;
	}

}
