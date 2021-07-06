/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.graph.GraphTopology.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Ordering;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.AbstractTopology;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.GamaSpatialPath;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.types.Types;

/**
 * The class GraphTopology.
 *
 * @author drogoul
 * @since 27 nov. 2011
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GraphTopology extends AbstractTopology {

	/**
	 * @param scope
	 * @param env
	 * @param torus
	 */
	public GraphTopology(final IScope scope, final IShape env, final GamaSpatialGraph graph) {
		super(scope, env, null);
		places = graph;
	}

	// The default topologies for graphs.
	public GraphTopology(final IScope scope, final GamaSpatialGraph graph) {
		this(scope, scope.getSimulation().getGeometry(), graph);
	}

	@Override
	protected boolean canCreateAgents() {
		return true;
	}

	@Override
	public boolean isContinuous() {
		return false;
	}

	private IShape optimizedClosestTo(final IShape source, final List<IShape> candidates) {
		IShape result = null;
		final ILocation loc = source.getLocation();
		double distMin = Double.MAX_VALUE;
		for (final IShape c : candidates) {
			final double dist = loc.euclidianDistanceTo(c.getLocation());
			if (dist < distMin) {
				distMin = dist;
				result = c;
			}
		}
		return result;
	}

	/**
	 * TODO : doc
	 *
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target) {
		// final ILocation source = sourceShape.getLocation();
		final GamaSpatialGraph graph = (GamaSpatialGraph) getPlaces();
		IShape sourceN = source;
		IShape targetN = target;
		boolean targetNode = graph.getVertexMap().containsKey(target);
		final boolean isAgentVertex = graph.isEmpty(scope) ? false : graph.getVertices().get(0) instanceof IAgent;
		final boolean targetNSame = isAgentVertex == target instanceof IAgent;
		final boolean sourceNSame = isAgentVertex == source instanceof IAgent;
		boolean sourceNode = graph.getVertexMap().containsKey(source);
		final boolean optimizedClosestTo = GamaPreferences.External.PATH_COMPUTATION_OPTIMIZATION.getValue();

		if (sourceNode && GamaPreferences.External.TOLERANCE_POINTS.getValue() > 0.0) {
			for (final IShape v : graph.getVertexMap().keySet()) {
				if (v.equals(source)) {
					sourceN = v;
					break;
				}
			}
		}
		if (targetNode && GamaPreferences.External.TOLERANCE_POINTS.getValue() > 0.0) {
			for (final IShape v : graph.getVertexMap().keySet()) {
				if (v.equals(target)) {
					targetN = v;
					break;
				}
			}
		}
		if (!sourceNSame && !sourceNode || !targetNSame && !targetNode) {
			for (final Object ed : graph.getVertices()) {
				if (((IShape) ed).getLocation().equals(source.getLocation())) {
					sourceN = (IShape) ed;
					sourceNode = true;
				}
				if (((IShape) ed).getLocation().equals(target.getLocation())) {
					targetN = (IShape) ed;
					targetNode = true;
				}
				if (sourceNode && targetNode) {
					break;
				}
			}

		}
		if (sourceNode && targetNode) {
			return (GamaSpatialPath) graph.computeShortestPathBetween(scope, sourceN, targetN);
		}

		IShape edgeS = null, edgeT = null;
		final boolean optimization = graph.edgeSet().size() > 1000;

		final double dist = optimization ? Math.sqrt(scope.getSimulation().getArea()) / graph.edgeSet().size() * 100
				: -1;

		if (graph.isAgentEdge()) {
			final IAgentFilter filter = In.edgesOf(getPlaces());

			if (!sourceNode) {
				edgeS = getPathEdge(scope, source);
				if (edgeS == null) {
					if (optimizedClosestTo) {
						edgeS = optimizedClosestTo(source, getPlaces().getEdges());
					} else if (optimization) {
						final Collection<IAgent> ags = scope.getSimulation().getAgent().getTopology()
								.getNeighborsOf(scope, source, dist, filter);
						if (!ags.isEmpty()) {
							double distMin = Double.MAX_VALUE;
							for (final IAgent e : ags) {
								final double d = source.euclidianDistanceTo(e);
								if (d < distMin) {
									edgeS = e;
									distMin = d;
								}
							}
						}
					}
					if (edgeS == null) {
						edgeS = scope.getSimulation().getAgent().getTopology().getAgentClosestTo(scope, source, filter);
					}
				}
				// We avoid computing the target if we cannot find any source.
				if (edgeS == null) {
					return null;
				}
			}
			if (!targetNode) {
				// edgeT = getPathEdge(scope, target);

				if (optimizedClosestTo) {
					edgeT = optimizedClosestTo(target, getPlaces().getEdges());
				} else if (optimization) {
					final Collection<IAgent> ags = scope.getSimulation().getAgent().getTopology().getNeighborsOf(scope,
							target, dist, filter);
					if (!ags.isEmpty()) {
						double distMin = Double.MAX_VALUE;
						for (final IAgent e : ags) {
							final double d = target.euclidianDistanceTo(e);
							if (d < distMin) {
								edgeT = e;
								distMin = d;
							}
						}
					}
				}
				if (edgeT == null) {
					edgeT = scope.getSimulation().getAgent().getTopology().getAgentClosestTo(scope, target, filter);
				}

				if (edgeT == null) {
					return null;
				}
			}
		} else {
			double distSMin = Double.MAX_VALUE;
			double distTMin = Double.MAX_VALUE;
			edgeS = getPathEdge(scope, source);
			if (edgeS != null) {
				distSMin = 0;
			}
			/*
			 * edgeT = getPathEdge(scope, target); if (edgeT != null) distTMin = 0;
			 */
			if (distSMin > 0 && !sourceNode || distTMin > 0 && !targetNode) {

				for (final Object e : graph.getEdges()) {
					final IShape edge = (IShape) e;
					if (!sourceNode && distSMin > 0) {
						final double distS = optimizedClosestTo
								? edge.getLocation().euclidianDistanceTo(source.getLocation())
								: edge.euclidianDistanceTo(source);
						if (distS < distSMin) {
							distSMin = distS;
							edgeS = edge;
						}
					}
					if (!targetNode && distTMin > 0) {
						final double distT = optimizedClosestTo
								? edge.getLocation().euclidianDistanceTo(target.getLocation())
								: edge.euclidianDistanceTo(target);
						if (distT < distTMin) {
							distTMin = distT;
							edgeT = edge;
						}
					}
				}
			}
			if (!sourceNode && edgeS == null || !targetNode && edgeT == null) {
				return null;
			}
		}

		if (getPlaces().isDirected()) {
			final List<IShape> edgesS = new ArrayList<>();
			final List<IShape> edgesT = new ArrayList<>();
			edgesS.add(edgeS);
			if (!sourceNode) {
				final IShape edgeRev = (IShape) graph.getEdge(graph.getEdgeTarget(edgeS), graph.getEdgeSource(edgeS));
				if (edgeRev != null && edgeRev.euclidianDistanceTo(source) <= edgeS.euclidianDistanceTo(source)) {
					edgesS.add(edgeRev);
				}
			}
			edgesT.add(edgeT);
			if (!targetNode) {
				final IShape edgeRev = (IShape) graph.getEdge(graph.getEdgeTarget(edgeT), graph.getEdgeSource(edgeT));
				if (edgeRev != null && target != null && edgeS != null
						&& edgeRev.euclidianDistanceTo(target) <= edgeS.euclidianDistanceTo(target)) {
					edgesT.add(edgeRev);
				}
			}
			return pathBetweenCommonDirected(scope, edgesS, edgesT, sourceN, targetN, sourceNode, targetNode);
		}

		return pathBetweenCommon(scope, graph, edgeS, edgeT, sourceN, targetN, sourceNode, targetNode);
	}

	public IShape getPathEdge(final IScope scope, final IShape ref) {
		return null;
	}

	public GamaSpatialPath pathBetweenCommon(final IScope scope, final GamaSpatialGraph graph, final IShape edgeS,
			final IShape edgeT, final IShape source, final IShape target, final boolean sourceNode,
			final boolean targetNode) {
		return null;
	}

	GamaSpatialPath pathFromEdgesUndirected(final IScope scope, final IList<IShape> listOfEdges, final IShape edgeS,
			final IShape edgeT, final IShape source, final IShape target, final boolean sourceNode,
			final boolean targetNode, final IShape nodeS, final IShape nodeSbis, final IShape nodeT,
			final boolean computeOther) {
		return null;
	}

	public double pathlengthEdges(final IList<IShape> edges) {
		double length = 0;
		for (final IShape sp : edges) {
			length += getPlaces().getWeightOf(sp);
		}
		return length;
	}

	public double lengthEdge(final IShape edge, final IShape location, final IShape source, final IShape target) {
		final double dist = source.getLocation().euclidianDistanceTo(target.getLocation());
		return dist == 0 ? 0
				: getPlaces().getWeightOf(edge) * location.euclidianDistanceTo(target.getLocation()) / dist;
	}

	public GamaSpatialPath pathBetweenCommonDirected(final IScope scope, final List<IShape> edgeS,
			final List<IShape> edgeT, final IShape source, final IShape target, final boolean sourceNode,
			final boolean targetNode) {
		if (edgeS.size() == 1 && edgeT.size() == 1) {
			return pathBetweenCommonDirected(scope, edgeS.get(0), edgeT.get(0), source, target, sourceNode, targetNode);
		}
		double wMin = Double.MAX_VALUE;
		GamaSpatialPath shortestPath = null;
		for (final IShape eS : edgeS) {
			for (final IShape eT : edgeT) {
				final GamaSpatialPath path = pathBetweenCommonDirected(scope, eS, eT, source, target, sourceNode,
						targetNode);
				if (path == null) {
					continue;
				}
				final double weight = path.getWeight();
				if (weight < wMin) {
					wMin = weight;
					shortestPath = path;
				}
			}
		}
		return shortestPath;

	}

	public GamaSpatialPath pathBetweenCommonDirected(final IScope scope, final IShape edgeS, final IShape edgeT,
			final IShape source, final IShape target, final boolean sourceNode, final boolean targetNode) {
		return null;
	}

	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target,
			final IShape existingEdge) {
		IList<IShape> edges;
		if (source.equals(target)) {
			edges = GamaListFactory.create(Types.GEOMETRY);
			if (existingEdge != null) {
				edges.add(existingEdge);
			}
			return PathFactory.newInstance(scope, this, source, target, edges);
		}
		edges = getPlaces().computeBestRouteBetween(scope, source, target);
		if (existingEdge != null) {
			edges = edges.listValue(scope, Types.NO_TYPE, true);
			edges.addValueAtIndex(scope, 0, existingEdge);
		}
		if (edges.isEmpty() || edges.get(0) == null) {
			return null;
		}

		return PathFactory.newInstance(scope, this, source, target, edges);
	}

	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target) {
		return pathBetween(scope, (IShape) source, (IShape) target);
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "GraphTopology";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml(final boolean includingBuiltIn) {
		return "GraphTopology";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(final IScope scope) {
		return new GraphTopology(scope, environment, (GamaSpatialGraph) places);
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#getRandomPlace()
	 */

	@Override
	public ISpatialGraph getPlaces() {
		return (GamaSpatialGraph) super.getPlaces();
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final IScope scope, final ILocation p) {
		return isValidGeometry(scope, p.getGeometry());
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IScope scope, final IShape g) {
		// Geometry g2 = g.getInnerGeometry();
		for (final IShape g1 : places.iterable(scope)) {
			if (g1.intersects(g)) {
				return true;
			}
			// TODO covers or intersects ?
		}
		return false;
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry, java.lang.Double)
	 */
	@Override
	public Double distanceBetween(final IScope scope, final IShape source, final IShape target) {
		final GamaSpatialPath path = this.pathBetween(scope, source, target);
		if (path == null) {
			return Double.MAX_VALUE;
		}
		if (path.getEdgeList().isEmpty()) {
			return 0.0;
		}

		// Patrick: no idea of the goal of the following code (in commentary)
		/*
		 * final Coordinate[] coordsSource =
		 * path.getEdgeList().get(0).getInnerGeometry().getCoordinates(); final
		 * Coordinate[] coordsTarget = path.getEdgeList().get(path.getEdgeList().size()
		 * - 1).getInnerGeometry().getCoordinates(); if (coordsSource.length == 0 ||
		 * coordsTarget.length == 0) { return Double.MAX_VALUE; } final GamaPoint
		 * sourceEdges = new GamaPoint(coordsSource[0]); final GamaPoint targetEdges =
		 * new GamaPoint(coordsTarget[coordsTarget.length - 1]);
		 * path.setSource(sourceEdges); path.setTarget(targetEdges);
		 */
		return path.getDistance(scope);
	}

	@Override
	public Double distanceBetween(final IScope scope, final ILocation source, final ILocation target) {
		final GamaSpatialPath path = this.pathBetween(scope, source, target);
		if (path == null) {
			return Double.MAX_VALUE;
		}
		if (path.getEdgeList().isEmpty()) {
			return 0.0;
		}
		// Patrick: no idea of the goal of the following code (in commentary)
		/*
		 * final Coordinate[] coordsSource =
		 * path.getEdgeList().get(0).getInnerGeometry().getCoordinates(); final
		 * Coordinate[] coordsTarget = path.getEdgeList().get(path.getEdgeList().size()
		 * - 1).getInnerGeometry().getCoordinates(); if (coordsSource.length == 0 ||
		 * coordsTarget.length == 0) { return Double.MAX_VALUE; } final GamaPoint
		 * sourceEdges = new GamaPoint(coordsSource[0]); final GamaPoint targetEdges =
		 * new GamaPoint(coordsTarget[coordsTarget.length - 1]);
		 * path.setSource(sourceEdges); path.setTarget(targetEdges);
		 */
		return path.getDistance(scope);
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Double directionInDegreesTo(final IScope scope, final IShape source, final IShape target) {
		// WARNING As it is computed every time the location of an agent is set,
		// and as the source and target in that
		// case do not correspond to existing nodes, it may be safer (and
		// faster) to call the root topology
		return root.directionInDegreesTo(scope, source, target);
		// final GamaSpatialPath path = this.pathBetween(scope, source, target);
		// if ( path == null ) { return null; }
		// // LineString ls = (LineString)
		// path.getEdgeList().first().getInnerGeometry();
		// // TODO Check this
		// final double dx = target.getLocation().getX() -
		// source.getLocation().getX();
		// final double dy = target.getLocation().getY() -
		// source.getLocation().getY();
		// final double result = Maths.atan2Opt(dy, dx);
		// return Maths.checkHeading((int) result);
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsIn(msi.gama.interfaces.IGeometry,
	 *      msi.gama.environment.IAgentFilter, boolean)
	 */
	@Override
	public Collection<IAgent> getAgentsIn(final IScope scope, final IShape source, final IAgentFilter f,
			final boolean covered) {
		final Collection<IAgent> result = super.getAgentsIn(scope, source, f, covered);
		result.removeIf(each -> each.dead() || !isValidGeometry(scope, each));
		return result;
	}

	@Override
	public boolean isTorus() {
		// TODO Why is it the case ?
		return false;
	}

	@Override
	public IList KpathsBetween(final IScope scope, final IShape source, final IShape target, final int k) {
		return null;
	}

	public IShape shapeClosest(final List<IShape> shapes, final IShape geom) {
		IShape cp = null;
		double distMin = Double.MAX_VALUE;
		for (final IShape shp : shapes) {
			final double dist = shp.euclidianDistanceTo(geom);
			if (dist < distMin) {
				distMin = dist;
				cp = shp;
			}
		}
		return cp;
	}

	@Override
	public IList<GamaSpatialPath> KpathsBetween(final IScope scope, final ILocation source, final ILocation target,
			final int k) {
		return KpathsBetween(scope, source.getGeometry(), target.getGeometry(), k);
	}

	public IList KpathsBetweenCommon(final IScope scope, final IShape edgeS, final IShape edgeT, final IShape source,
			final IShape target, final boolean sourceNode, final boolean targetNode, final int k) {
		return null;
	}

	public IList KpathsBetweenCommonDirected(final IScope scope, final IShape edgeS, final IShape edgeT,
			final IShape source, final IShape target, final boolean sourceNode, final boolean targetNode, final int k) {
		return null;
	}

	@Override
	public Collection<IAgent> getNeighborsOf(final IScope scope, final IShape source, final Double distance,
			final IAgentFilter filter) throws GamaRuntimeException {
		return null;
	}

	public Set<IShape> getNeighborsOfRec(final IScope scope, final IShape currentSource, final boolean edge,
			final double currentDist, final ISpatialGraph graph, final Set<IShape> alr) throws GamaRuntimeException {
		return null;
	}

	@Override
	public IAgent getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter) {
		// A better solution is required !!! this solution is just here to
		// ensure the consistency of the closest operator on graph !
		final List<IAgent> listAgents = new ArrayList();
		listAgents.addAll(filter.getAgents(scope).listValue(scope, Types.AGENT, false));
		listAgents.remove(source);
		IAgent closest = null;
		double minDist = Double.POSITIVE_INFINITY;
		for (final IAgent ag : listAgents) {
			final Double dist = this.distanceBetween(scope, source, ag);
			if (dist != null && dist < minDist) {
				closest = ag;
				minDist = dist;
				if (dist == 0) {
					break;
				}
			}

		}
		return closest;
	}

	@Override
	public Collection<IAgent> getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter,
			final int number) {
		// A better solution is required !!! this solution is just here to
		// ensure the consistency of the closest operator on graph !

		final List<IAgent> listAgents = new ArrayList();
		listAgents.addAll(filter.getAgents(scope).listValue(scope, Types.AGENT, false));
		listAgents.remove(source);
		scope.getRandom().shuffleInPlace(listAgents);
		final Ordering<IAgent> ordering = Ordering.natural().onResultOf(input -> distanceBetween(scope, source, input));
		if (listAgents.size() <= number) {
			Collections.sort(listAgents, ordering);
			return GamaListFactory.wrap(Types.AGENT, listAgents);
		}

		return GamaListFactory.wrap(Types.AGENT, ordering.leastOf(listAgents, number));

	}
}
