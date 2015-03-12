package org.sugarj.test.cleardep.build.cycle.fixpoint;

import java.io.IOException;

import org.sugarj.cleardep.output.BuildOutput;
import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;

public class IntegerOutput implements BuildOutput {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6695142649240097187L;
	private Path resultFile;
	private int value;

	public IntegerOutput(Path resultFile, int value) {
		super();
		this.resultFile = resultFile;
		this.value = value;
	}
	public int getResult() throws IOException {
		return FileUtils.readIntFromFile(resultFile);
	}
	
	public Path getResultFile() {
		return resultFile;
	}
	
	@Override
	public boolean isConsistent() {
		try {
			return FileCommands.fileExists(resultFile) && FileUtils.readIntFromFile(resultFile) == value;
		} catch (IOException e) {
			return false;
		}
	}
}