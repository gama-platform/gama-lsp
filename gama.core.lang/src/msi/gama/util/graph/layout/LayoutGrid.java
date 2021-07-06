package msi.gama.util.graph.layout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Graphs;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Queries;
import msi.gaml.types.Types; 

public class LayoutGrid {

	private final IGraph<IShape, IShape> graph;

	private final double coeffSq;

	private final IShape envelopeGeometry;

	public LayoutGrid(final IGraph<IShape, IShape> graph, final IShape envelopeGeometry, final double coeffSq) {
		this.graph = graph;
		this.envelopeGeometry = envelopeGeometry;
		this.coeffSq = coeffSq;
	}

	@SuppressWarnings("null")
	public void applyLayout(final IScope scope) {
		return;
	}

}
