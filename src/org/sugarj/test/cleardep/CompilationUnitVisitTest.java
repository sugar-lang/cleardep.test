package org.sugarj.test.cleardep;

import static org.sugarj.test.cleardep.CompilationUnitTestUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.CompilationUnit.ModuleVisitor;
import org.sugarj.cleardep.Mode;
import org.sugarj.test.cleardep.CompilationUnitTestUtils.NodeUnit;

public class CompilationUnitVisitTest {
	
	ModuleVisitor<List<NodeUnit>> collectVisitedNodesVisitor = new ModuleVisitor<List<NodeUnit>> () {

		@Override
		public List<NodeUnit> visit(CompilationUnit mod, Mode<?> mode) {
			if (!(mod instanceof NodeUnit)) {
				fail("Got unit of illegal type");
			}
			List<NodeUnit> singleton = new ArrayList<>();
			singleton.add((NodeUnit) mod);
			return singleton;
		}

		@Override
		public List<NodeUnit> combine(List<NodeUnit> t1, List<NodeUnit> t2) {
			t1.addAll(t2);
			return t1;
		}

		@Override
		public List<NodeUnit> init() {
			return new ArrayList<>();
		}

		@Override
		public boolean cancel(List<NodeUnit> t) {
			return false;
		}
		
	};
	
	@Test
	public void testVisitTreeGraph() {
		NodeUnit root = makeNode("root");
		
		NodeUnit c1 = makeNode("c1");
		makeEdgeFrom(root).to(c1);
		NodeUnit c11 = makeNode("c11");
		NodeUnit c12 = makeNode("c12");
		makeEdgeFrom(c1).to(c11).and(c12);
		
		NodeUnit c2 = makeNode("c2");
		makeEdgeFrom(root).to(c2);
		
		
		List<NodeUnit> visitedUnits = root.visit(collectVisitedNodesVisitor);
		
		//Check that we did visit the correct number if modules
		assertEquals("Visited wrong number of visited nodes", 5, visitedUnits.size());
		
		// Check that all units has been visited
		assertEquals("Visited not all units", set(root, c1, c11, c12, c2), set(visitedUnits));
	}
	
	@Test
	public void testVisitDAGGraph() {
		NodeUnit root1 = makeNode("root1");
		NodeUnit root2 = makeNode("root2");
		
		NodeUnit cx1 = makeNode("cx1");
		makeEdgeFrom(root1).and(root2).to(cx1);
		NodeUnit cx11 = makeNode("cx11");
		NodeUnit cx12 = makeNode("cx12");
		makeEdgeFrom(cx1).to(cx11).and(cx12);
		NodeUnit cx1x1 = makeNode("cx1x1");
		NodeUnit cx121 = makeNode("cx121");
		makeEdgeFrom(cx11).and(cx12).to(cx1x1);
		makeEdgeFrom(cx12).to(cx121);
		
		NodeUnit c21 = makeNode("c21");
		makeEdgeFrom(root2).to(c21);
		
		List<NodeUnit> visitedUnitsRoot1 = root1.visit(collectVisitedNodesVisitor);
		assertEquals("Wrong number of visited nodes from root1", 6, visitedUnitsRoot1.size());
		assertEquals("Wrong visited nodes from root1", set(root1, cx1, cx11, cx12, cx1x1, cx121), set(visitedUnitsRoot1));
		
		List<NodeUnit> visitedUnitsRoot2 = root2.visit(collectVisitedNodesVisitor);
		assertEquals("Wrong number of visited nodes from root2", 7, visitedUnitsRoot2.size());
		assertEquals("Wrong visited nodes from root2", set(root2,  cx1, cx11,cx12,cx1x1,cx121,c21), set(visitedUnitsRoot2));
		
	}
	
	@Test
	public void testVisitCycleGraph() {
		NodeUnit n1 = makeNode("n1");
		NodeUnit n2 = makeNode("n2");
		makeEdgeFrom(n1).to(n2);
		makeEdgeFrom(n2).to(n1);
		
		List<NodeUnit> visitedUnitsN1 = n1.visit(collectVisitedNodesVisitor);
		List<NodeUnit> visitedUnitsN2 = n2.visit(collectVisitedNodesVisitor);
		assertEquals("Wrong number of visited nodes from n1", 2, visitedUnitsN1.size());
		assertEquals("Wrong visited nodes from n1", set(n1, n2), set(visitedUnitsN1));
		assertEquals("Wrong number of visited nodes from n2", 2, visitedUnitsN2.size());
		assertEquals("Wrong visited nodes from n2", set(n1, n2), set(visitedUnitsN2));
		
		NodeUnit root = makeNode("root");
		makeEdgeFrom(root).to(n1);
		List<NodeUnit> visitedUnitsRoot = root.visit(collectVisitedNodesVisitor);
		assertEquals("Wrong number of visited nodes from root", 3, visitedUnitsRoot.size());
		assertEquals("Wrong visited nodes from root", set(n1, n2, root), set(visitedUnitsRoot));
	}

}
