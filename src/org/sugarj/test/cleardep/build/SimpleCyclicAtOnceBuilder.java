package org.sugarj.test.cleardep.build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.build.CompileCycleAtOnceBuilder;
import org.sugarj.cleardep.stamp.ContentStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.test.cleardep.build.SimpleBuilder.TestBuilderInput;

public class SimpleCyclicAtOnceBuilder extends
		CompileCycleAtOnceBuilder<TestBuilderInput, TestOutput> {

	public static BuilderFactory<ArrayList<TestBuilderInput>, TestOutput, SimpleCyclicAtOnceBuilder> factory = new BuilderFactory<ArrayList<TestBuilderInput>, TestOutput, SimpleCyclicAtOnceBuilder>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public SimpleCyclicAtOnceBuilder makeBuilder(
				ArrayList<TestBuilderInput> input) {
			return new SimpleCyclicAtOnceBuilder(input);
		}
	};

	public SimpleCyclicAtOnceBuilder(ArrayList<TestBuilderInput> input) {
		super(input, factory);
	}

	@Override
	protected Path singletonPersistencePath(TestBuilderInput input) {
		return FileCommands.addExtension(input.getInputPath(), "dep");
	}

	@Override
	protected List<TestOutput> buildAll() throws Throwable {

		List<TestOutput> outputs = new ArrayList<>(this.input.size());

		Set<RelativePath> cyclicDependencies = new HashSet<>();
		for (TestBuilderInput input : this.input) {
			//System.out.println(input.getInputPath().getRelativePath());
			cyclicDependencies.add(input.getInputPath());
			requires(input.getInputPath());
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
			generates(input, generatedFile);
			outputs.add(new TestOutput());
		}
		setState(BuildUnit.State.finished(true));
		return outputs;
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
	protected Stamper defaultStamper() {
		return ContentStamper.instance;
	}

}
