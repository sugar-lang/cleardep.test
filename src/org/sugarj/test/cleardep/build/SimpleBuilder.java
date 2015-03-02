package org.sugarj.test.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.stamp.ContentHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public class SimpleBuilder extends Builder<TestBuilderInput, BuildUnit> {

	public static BuilderFactory<TestBuilderInput, BuildUnit, SimpleBuilder> factory = new BuilderFactory<SimpleBuilder.TestBuilderInput, BuildUnit, SimpleBuilder>() {

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
	protected Class<BuildUnit> resultClass() {
		return BuildUnit.class;
	}

	@Override
	protected Stamper defaultStamper() {
		return ContentHashStamper.instance;
	}

	@Override
	protected void build(BuildUnit result) throws IOException {
		result.requires(input.inputPath);
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
		result.requires(generatedFile);
		result.setState(BuildUnit.State.finished(true));
	}

}
