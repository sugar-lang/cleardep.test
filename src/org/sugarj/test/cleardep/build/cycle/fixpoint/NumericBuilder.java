package org.sugarj.test.cleardep.build.cycle.fixpoint;

import java.util.List;

import org.sugarj.cleardep.BuildUnit.State;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.CycleSupport;
import org.sugarj.cleardep.stamp.FileContentStamper;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;

public abstract class NumericBuilder extends Builder<FileInput, IntegerOutput> {

	protected static interface Operator {
		public int apply(int a, int b);
	}

	public NumericBuilder(FileInput input) {
		super(input);
	}

	protected abstract Operator getOperator();

	@Override
	protected final Path persistentPath() {
		return FileCommands.addExtension(this.input.getFile(), "dep");
	}

	@Override
	protected final Stamper defaultStamper() {
		return FileContentStamper.instance;
	}
	
	@Override
	protected final CycleSupport getCycleSupport() {
		return new NumericCycleSupport();
	}

	@Override
	protected final IntegerOutput build() throws Throwable {
		require(this.input.getFile());
		int myNumber = FileUtils.readIntFromFile(this.input.getFile());

		
		if (FileCommands.exists(this.input.getDepsFile())) {
			require(this.input.getDepsFile());
			List<RelativePath> depPaths = FileUtils.readPathsFromFile(
					this.input.getDepsFile(), this.input.getWorkingDir());

			for (RelativePath path : depPaths) {
				FileInput input = new FileInput(this.input.getWorkingDir(),
						path);
				IntegerOutput output = null;
				String extension = FileCommands.getExtension(path);
				if (extension.equals("modulo")) {
					output = requireBuild(ModuloBuilder.factory, input);
				} else if (extension.equals("divide")) {
					output = requireBuild(DivideByBuilder.factory, input);
				} else if (extension.equals("gcd")) {
					output = requireBuild(GCDBuilder.factory, input);
				} else {
					throw new IllegalArgumentException("The extension "
							+ extension + " is unknown.");
				}
				// Cycle support: if there is no output currently, ignore the dependency
				if (output != null && FileCommands.exists(output.getResultFile())) {
				myNumber = this.getOperator().apply(myNumber,
						output.getResult());
				require(output.getResultFile());

				} 
			}
		}
		
		Path outFile = FileCommands.replaceExtension(input.getFile(), "out");
		FileUtils.writeIntToFile(myNumber, outFile);
		generate(outFile);
		
		setState(State.finished(true));
		return new IntegerOutput(outFile, myNumber);

	}

}
