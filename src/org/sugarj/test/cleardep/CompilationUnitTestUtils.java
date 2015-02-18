package org.sugarj.test.cleardep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sugarj.cleardep.SimpleCompilationUnit;

public class CompilationUnitTestUtils {
	
	public static class NodeUnit extends SimpleCompilationUnit {

		private String name;
		
		private NodeUnit(String name) {
			this.name = name;
			this.init();
		}
		
		@Override
		protected boolean isConsistentExtend() {
			return true;
		}
		
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return "Node(" + name +")";
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
	
	private static class DefaultEdgeMaker implements EdgeMaker{
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
						src.addModuleDependency(dst);
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
