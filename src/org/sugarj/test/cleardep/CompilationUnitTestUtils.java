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

	private static BuilderFactory<NodeInput, NodeUnit, Builder<NodeInput, NodeUnit>> factory = new BuilderFactory<NodeInput, NodeUnit, Builder<NodeInput, NodeUnit>>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -695869678306450263L;

		@Override
		public Builder<NodeInput, NodeUnit> makeBuilder(NodeInput input,
				BuildManager manager) {
			throw new UnsupportedOperationException();
		}

	};

	public static class NodeUnit extends BuildUnit {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5474025936620029380L;
		private String name;

		private NodeUnit(String name) {
			this.name = name;
			this.init();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Node(" + name + ")";
		}

	}

	public static NodeUnit makeNode(String name) {
		return new NodeUnit(name);
	}

	public static interface EdgeMaker {
		public EdgeMaker and(NodeUnit dst);

		public AndEdgeMaker to(NodeUnit dst);
	}

	public static interface AndEdgeMaker {
		public AndEdgeMaker and(NodeUnit dst);
	}

	private static class DefaultEdgeMaker implements EdgeMaker {
		private Set<NodeUnit> srcs = new HashSet<>();

		public DefaultEdgeMaker(NodeUnit src) {
			srcs = Collections.singleton(src);
		}

		public DefaultEdgeMaker(Set<NodeUnit> otherSrcs, NodeUnit src) {
			srcs = new HashSet<>(otherSrcs);
			srcs.add(src);
		}

		@Override
		public EdgeMaker and(NodeUnit src) {
			return new DefaultEdgeMaker(srcs, src);
		}

		@Override
		public AndEdgeMaker to(NodeUnit dst) {
			AndEdgeMaker maker = new AndEdgeMaker() {

				@Override
				public AndEdgeMaker and(NodeUnit dst) {
					for (NodeUnit src : srcs) {
						src.requires(dst);
					}
					return this;
				}
			};
			return maker.and(dst);
		}

	}

	public static EdgeMaker makeEdgeFrom(final NodeUnit src) {
		return new DefaultEdgeMaker(src);
	}

	public static <T> Set<T> set(T... elems) {
		return set(Arrays.asList(elems));
	}

	public static <T> Set<T> set(Collection<T> elems) {
		return new HashSet<>(elems);
	}

}
