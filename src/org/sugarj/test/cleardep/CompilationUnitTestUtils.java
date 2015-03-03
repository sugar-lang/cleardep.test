package org.sugarj.test.cleardep;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.build.BuildManager;
import org.sugarj.cleardep.build.Builder;
import org.sugarj.cleardep.build.BuilderFactory;
import org.sugarj.cleardep.output.BuildOutput;

public class CompilationUnitTestUtils {

	private static class NodeInput implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2085005105090411812L;
		private String name;

		public NodeInput(String name) {
			this.name = name;
		}
	}
	
	public static class NodeOutput implements BuildOutput {

		private final String name;
		
		
		
		public NodeOutput(String name) {
			super();
			this.name = name;
		}



		@Override
		public boolean isConsistent() {
			return true;
		}
		
	}

	private static BuilderFactory<NodeInput, EmptyBuildOutput, Builder<NodeInput, EmptyBuildOutput>> factory = new BuilderFactory<NodeInput, EmptyBuildOutput, Builder<NodeInput, EmptyBuildOutput>>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -695869678306450263L;

		@Override
		public Builder<NodeInput, EmptyBuildOutput> makeBuilder(NodeInput input) {
			throw new UnsupportedOperationException();
		}

	};

	public static BuildUnit<NodeOutput> makeNode(String name) {
		BuildUnit<NodeOutput> unit = new BuildUnit<NodeOutput>();
		unit.setBuildResult(new NodeOutput(name));
		return unit;
	}

	public static interface EdgeMaker {
		public EdgeMaker and(BuildUnit<NodeOutput> dst);

		public AndEdgeMaker to(BuildUnit<NodeOutput> dst);
	}

	public static interface AndEdgeMaker {
		public AndEdgeMaker and(BuildUnit<NodeOutput> dst);
	}

	private static class DefaultEdgeMaker implements EdgeMaker {
		private Set<BuildUnit<NodeOutput>> srcs = new HashSet<>();

		public DefaultEdgeMaker(BuildUnit<NodeOutput> src) {
			srcs = Collections.singleton(src);
		}

		public DefaultEdgeMaker(Set<BuildUnit<NodeOutput>> otherSrcs, BuildUnit<NodeOutput> src) {
			srcs = new HashSet<>(otherSrcs);
			srcs.add(src);
		}

		@Override
		public EdgeMaker and(BuildUnit<NodeOutput> src) {
			return new DefaultEdgeMaker(srcs, src);
		}

		@Override
		public AndEdgeMaker to(BuildUnit<NodeOutput> dst) {
			AndEdgeMaker maker = new AndEdgeMaker() {

				@Override
				public AndEdgeMaker and(BuildUnit<NodeOutput> dst) {
					for (BuildUnit<NodeOutput> src : srcs) {
						src.dependsOn(dst);
					}
					return this;
				}
			};
			return maker.and(dst);
		}

	}

	public static EdgeMaker makeEdgeFrom(final BuildUnit<NodeOutput> src) {
		return new DefaultEdgeMaker(src);
	}
	
	public static String nodeName(BuildUnit<NodeOutput> node) {
		return node.getBuildResult().name;
	}

	public static <T> Set<T> set(T... elems) {
		return set(Arrays.asList(elems));
	}

	public static <T> Set<T> set(Collection<T> elems) {
		return new HashSet<>(elems);
	}

}
