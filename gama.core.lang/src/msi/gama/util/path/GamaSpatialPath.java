/*******************************************************************************************************
 *
 * msi.gama.util.path.GamaSpatialPath.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.path;

import static java.lang.Math.min;
import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;
import static msi.gama.common.geometry.GeometryUtils.getLastPointOf;
import static msi.gama.common.geometry.GeometryUtils.getPointsOf;
import static msi.gama.common.geometry.GeometryUtils.split_at;
import static msi.gaml.operators.Spatial.Punctal._closest_point_to;



import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.Types;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaSpatialPath extends GamaPath<IShape, IShape, IGraph<IShape, IShape>> {

	IList<IShape> segments;
	IShape shape = null;
	boolean threeD = false;
	IMap<IShape, IShape> realObjects; // key = part of the geometry

	public GamaSpatialPath(final GamaSpatialGraph g, final IShape start, final IShape target,
			final IList<IShape> _edges) {
		super(g, start, target, _edges);
	}

	public GamaSpatialPath(final GamaSpatialGraph g, final IShape start, final IShape target,
			final IList<IShape> _edges, final boolean modify_edges) {
		super(g, start, target, _edges, modify_edges);
	}

	public GamaSpatialPath(final IShape start, final IShape target, final IList<? extends IShape> edges) {
		super(null, start, target, edges, false);
	}

	public GamaSpatialPath(final IShape start, final IShape target, final IList<? extends IShape> edges,
			final boolean modify_edges) {
		super(null, start, target, edges, modify_edges);
	}

	public GamaSpatialPath(final IList<IShape> nodes) {
		super(nodes);
	}

	@Override
	protected IShape createEdge(final IShape v, final IShape v2) {
		return GamaGeometryType.buildLine(v.getLocation(), v2.getLocation());
	}

	@Override
	public void init(final IGraph<IShape, IShape> g, final IShape start, final IShape target,
			final IList<? extends IShape> _edges, final boolean modify_edges) {
		return;
	}

	protected double zVal(final GamaPoint point, final IShape edge) {
		return 0.0;
	}

	public GamaSpatialPath(final GamaSpatialGraph g, final IList<? extends IShape> nodes) {
		// FIXME call super super(param...);
		// DEBUG.OUT("GamaSpatialPath nodes: " + nodes);
		if (nodes.isEmpty()) {
			source = new GamaPoint(0, 0);
			target = source;
		} else {
			source = nodes.get(0);
			target = nodes.get(nodes.size() - 1);
		}
		segments = GamaListFactory.<IShape> create(Types.GEOMETRY);
		realObjects = GamaMapFactory.createUnordered();
		graph = g;

		for (int i = 0, n = nodes.size(); i < n - 1; i++) {
			final IShape geom = GamaGeometryType.buildLine(nodes.get(i).getLocation(), nodes.get(i + 1).getLocation());
			segments.add(geom);

			final IAgent ag = nodes.get(i).getAgent();
			if (ag != null) {
				// MODIF: put?
				realObjects.put(nodes.get(i).getGeometry(), ag);
			}
		}
		final IAgent ag = nodes.isEmpty() ? null : nodes.get(nodes.size() - 1).getAgent();
		if (ag != null) {
			// MODIF: put?
			realObjects.put(nodes.get(nodes.size() - 1).getGeometry(), ag);
		}
	}

	// /////////////////////////////////////////////////
	// Implements methods from IValue

	@Override
	public GamaSpatialPath copy(final IScope scope) {
		return new GamaSpatialPath(getGraph(), source, target, edges);
	}

	@Override
	public GamaSpatialGraph getGraph() {
		return (GamaSpatialGraph) graph;
	}

	// /////////////////////////////////////////////////
	// Implements methods from IPath
	//
	// @Override
	// public IList<IShape> getAgentList() {
	// GamaList<IShape> ags = GamaListFactory.create(Types.GEOMETRY);
	// ags.addAll(new HashSet<IShape>(realObjects.values()));
	// return ags;
	// }

	@Override
	public IList getEdgeGeometry() {
		// GamaList<IShape> ags = GamaListFactory.create(Types.GEOMETRY);
		// ags.addAll(new HashSet<IShape>(realObjects.values()));
		// return ags;
		return segments;
	}

	@Override
	public void acceptVisitor(final IAgent agent) {
		agent.setAttribute("current_path", this); // ???
	}

	@Override
	public void forgetVisitor(final IAgent agent) {
		agent.setAttribute("current_path", null); // ???
	}

	@Override
	public int indexOf(final IAgent a) {
		return Cast.asInt(null, a.getAttribute("index_on_path")); // ???
	}

	@Override
	public int indexSegmentOf(final IAgent a) {
		return Cast.asInt(null, a.getAttribute("index_on_path_segment")); // ???
	}

	@Override
	public boolean isVisitor(final IAgent a) {
		return a.getAttribute("current_path") == this;
	}

	@Override
	public void setIndexOf(final IAgent a, final int index) {
		a.setAttribute("index_on_path", index);
	}

	@Override
	public void setIndexSegementOf(final IAgent a, final int indexSegement) {
		a.setAttribute("index_on_path_segment", indexSegement);
	}

	@Override
	public double getDistance(final IScope scope) {
		return 0.0;
	}

	private double getDistanceComplex(final IScope scope, final boolean keepSource, final boolean keepTarget) {
		return 0.0;
	}

	@Override
	public ITopology getTopology(final IScope scope) {
		if (graph == null) { return null; }
		return ((GamaSpatialGraph) graph).getTopology(scope);
	}

	@Override
	public void setRealObjects(final IMap<IShape, IShape> realObjects) {
		this.realObjects = realObjects;
	}

	@Override
	public IShape getRealObject(final Object obj) {
		return realObjects.get(obj);
	}

	@Override
	public IShape getGeometry() {

		if (shape == null && segments.size() > 0) {
			if (segments.size() == 1) {
				shape = new GamaShape(segments.get(0));
			} else {
				final IList<IShape> pts = GamaListFactory.create(Types.POINT);
				for (final IShape ent : segments) {
					for (final GamaPoint p : GeometryUtils.getPointsOf(ent)) {
						if (!pts.contains(p)) {
							pts.add(p);
						}
					}
				}
				if (pts.size() > 0) {
					shape = GamaGeometryType.buildPolyline(pts);
				}
			}

		}
		return shape;
	}

	@Override
	public void setGraph(final IGraph<IShape, IShape> graph) {
		this.graph = graph;
		graphVersion = graph.getVersion();

		for (final IShape edge : edges) {
			final IAgent ag = edge.getAgent();
			if (ag != null) {
				realObjects.put(edge.getGeometry(), ag);
			} else {
				realObjects.put(edge.getGeometry(), edge);
			}
		}

	}

	@Override
	public IList<IShape> getEdgeList() {
		if (edges == null) { return segments; }
		return edges;
	}

	@Override
	public IList<IShape> getVertexList() {
		if (graph == null) {
			try (final Collector.AsList<IShape> vertices = Collector.getList()) {
				IShape g = null;
				for (final Object ed : getEdgeList()) {
					g = (IShape) ed;
					vertices.add(GeometryUtils.getFirstPointOf(g));
				}
				if (g != null) {
					vertices.add(GeometryUtils.getLastPointOf(g));
				}
				return vertices.items();
			}
		}
		return getPathVertexList();
	}

	public IList<IShape> getPathVertexList() {
		return null;
	}

	public static IShape getOppositeVertex(final Object g, final IShape e, final IShape v) {
		return null;
	}

	public boolean isThreeD() {
		return threeD;
	}

}
