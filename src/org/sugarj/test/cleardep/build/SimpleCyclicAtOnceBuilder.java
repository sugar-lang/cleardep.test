package org.sugarj.test.cleardep.build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.build.CompileCycleAtOnceBuilder;
import org.sugarj.cleardep.stamp.ContentHashStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public class SimpleCyclicAtOnceBuilder extends
		CompileCycleAtOnceBuilder<TestBuilderInput, BuildUnit> {

	public static BuilderFactory<ArrayList<TestBuilderInput>, BuildUnit, SimpleCyclicAtOnceBuilder> factory = new BuilderFactory<ArrayList<TestBuilderInput>, BuildUnit, SimpleCyclicAtOnceBuilder>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public SimpleCyclicAtOnceBuilder makeBuilder(
				ArrayList<TestBuilderInput> input, BuildManager manager) {
			return new SimpleCyclicAtOnceBuilder(input, manager);
		}
	};

	public SimpleCyclicAtOnceBuilder(ArrayList<TestBuilderInput> input,
			BuildManager manager) {
		super(input, factory, manager);
	}

	@Override
	protected Path singletonPersistencePath(TestBuilderInput input) {
		return FileCommands.addExtension(input.getInputPath(), "dep");
	}

	@Override
	protected Path cyclePersistencePath(List<TestBuilderInput> input) {
		String name = "";
		for (TestBuilderInput singleInput : input) {
			name += FileCommands.dropExtension(singleInput.getInputPath().getRelativePath());
		}
		name += ".txt.dep";
		return new RelativePath(input.get(0).getBasePath(), name);
	}

	@Override
	protected void buildCycle(BuildUnit result) throws Throwable {

		
		Set<RelativePath> cyclicDependencies = new HashSet<>();
		for (TestBuilderInput input : this.input) {
			cyclicDependencies.add(input.getInputPath());
			result.requires(input.getInputPath());
		}

		List<String> contentLines = new ArrayList<>();

		for (TestBuilderInput input : this.input) {
			List<String> allLines = FileCommands.readFileLines(input
					.getInputPath());

			for (String line : allLines) {
				if (line.startsWith("Dep:")) {
					String depFile = line.substring(4);
					RelativePath depPath = new RelativePath(
							input.getBasePath(), depFile);
					if (!cyclicDependencies.contains(depPath)) {
						TestBuilderInput depInput = new TestBuilderInput(
								input.getBasePath(), depPath);
						requireCyclicable(factory, depInput);
					}
				} else {
					contentLines.add(line);
				}
			}
		}

		for (TestBuilderInput input : this.input) {

			// Write the content to a generated file
			Path generatedFile = FileCommands.addExtension(
					input.getInputPath(), "gen");
			FileCommands.writeLinesFile(generatedFile, contentLines);
			result.requires(generatedFile);
		}
		result.setState(BuildUnit.State.finished(true));
	}

	@Override
	protected String taskDescription() {
		String descr = "Cyclic SimpleBuilder for ";
		for (TestBuilderInput input : this.input) {
			descr += input.getInputPath().getRelativePath() + ", ";
		}
		return descr;
	}

	@Override
	protected Class<BuildUnit> resultClass() {
		return BuildUnit.class;
	}

	@Override
	protected Stamper defaultStamper() {
		return ContentHashStamper.instance;
	}

}
