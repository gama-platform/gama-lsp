/*******************************************************************************************************
 *
 * msi.gaml.skills.MovingSkill.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.skills;

import static msi.gama.common.geometry.GeometryUtils.getFirstPointOf;
import static msi.gama.common.geometry.GeometryUtils.getLastPointOf;
import static msi.gama.common.geometry.GeometryUtils.getPointsOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.metamodel.topology.graph.GraphTopology;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.IGraph;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.GamaSpatialPath;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * MovingSkill : This class is intended to define the minimal set of behaviours
 * required from an agent that is able to move. Each member that has a meaning
 * in GAML is annotated with the respective tags (vars, getter, setter, init,
 * action & args)
 *
 * @author drogoul 4 juil. 07
 */

@doc("The moving skill is intended to define the minimal set of behaviours required for agents that are able to move on different topologies")
@vars({ @variable(name = IKeyword.LOCATION, type = IType.POINT, depends_on = IKeyword.SHAPE, doc = @doc("Represents the current position of the agent")),
		@variable(name = IKeyword.SPEED, type = IType.FLOAT, init = "1.0", doc = @doc("Represents the speed of the agent (in meter/second)")),
		@variable(name = IKeyword.HEADING, type = IType.FLOAT, init = "rnd(360.0)", doc = @doc("Represents the absolute heading of the agent in degrees.")),
		@variable(name = "current_path", type = IType.PATH, init = "nil", doc = @doc("Represents the path on which the agent is moving on (goto action on a graph)")),
		@variable(name = "current_edge", type = IType.GEOMETRY, init = "nil", doc = @doc("Represents the agent/geometry on which the agent is located (only used with a graph)")),
		@variable(name = IKeyword.REAL_SPEED, type = IType.FLOAT, init = "0.0", doc = @doc("Represents the actual speed of the agent (in meter/second)")),

		@variable(name = IKeyword.DESTINATION, type = IType.POINT, depends_on = { IKeyword.SPEED, IKeyword.HEADING,
				IKeyword.LOCATION }, doc = @doc(deprecated = "This attribute is going to be removed in a future version of GAMA", value = "Represents the next location of the agent if it keeps its current speed and heading (read-only). ** Only correct in continuous topologies and may return nil values if the destination is outside the environment **")) })
@skill(name = IKeyword.MOVING_SKILL, concept = { IConcept.SKILL, IConcept.AGENT_MOVEMENT })
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MovingSkill extends Skill {
	@getter(IKeyword.HEADING)
	public Double getHeading(final IAgent agent) {
		Double h = (Double) agent.getAttribute(IKeyword.HEADING);
		if (h == null) {
			h = agent.getScope().getRandom().next() * 360;
			setHeading(agent, h);
		}
		return Maths.checkHeading(h);
	}

	@setter(IKeyword.HEADING)
	public void setHeading(final IAgent agent, final double heading) {
		if (agent == null) {
			return;
		}
		final double headingValue = heading % 360;
		agent.setAttribute(IKeyword.HEADING, headingValue);
	}

	@getter(IKeyword.DESTINATION)
	public ILocation getDestination(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		final ILocation actualLocation = agent.getLocation();
		final double dist = computeDistance(agent.getScope(), agent);
		final ITopology topology = getTopology(agent);
		return topology.getDestination(actualLocation, getHeading(agent), dist, false);
	}

	@setter(IKeyword.DESTINATION)
	public void setDestination(final IAgent agent, final ILocation p) {
		// READ_ONLY
	}

	@getter(IKeyword.SPEED)
	public static double getSpeed(final IAgent agent) {
		if (agent == null) {
			return 0.0;
		}
		return (Double) agent.getAttribute(IKeyword.SPEED);
	}

	@getter(IKeyword.REAL_SPEED)
	public static double getRealSpeed(final IAgent agent) {
		if (agent == null) {
			return 0.0;
		}
		return (Double) agent.getAttribute(IKeyword.REAL_SPEED);
	}

	@setter(IKeyword.SPEED)
	public static void setSpeed(final IAgent agent, final double s) {
		if (agent == null) {
			return;
		}
		agent.setAttribute(IKeyword.SPEED, s);
	}

	@setter(IKeyword.REAL_SPEED)
	public static void setRealSpeed(final IAgent agent, final double s) {
		if (agent == null) {
			return;
		}
		agent.setAttribute(IKeyword.REAL_SPEED, s);
	}

	@getter(value = IKeyword.LOCATION, initializer = true)
	public ILocation getLocation(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		return agent.getLocation();
	}

	@setter(IKeyword.LOCATION)
	// Correctly manages the heading
	public void setLocation(final IAgent agent, final ILocation p) {
		return;
	}

	@setter("current_path")
	public static void setCurrentPath(final IAgent agent, final IPath p) {
		// READ_ONLY
	}

	@getter(value = "current_path")
	public static IPath getCurrentPath(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		return (IPath) agent.getAttribute("current_path");
	}

	@setter("current_edge")
	public void setCurrentEdge(final IAgent agent, final IShape g) {
		// READ_ONLY
	}

	@getter(value = "current_edge")
	public IShape getCurrentEdge(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		return (IShape) agent.getAttribute("current_edge");
	}

	public void setCurrentEdge(final IAgent agent, final IPath path) {
		if (path != null) {
			final Integer index = (Integer) agent.getAttribute("index_on_path");
			if (index < path.getEdgeList().size()) {
				agent.setAttribute("current_edge", path.getEdgeList().get(index));
			}
		}
	}

	public void setCurrentEdge(final IAgent agent, final IGraph graph) {
		if (graph != null) {
			final Integer index = (Integer) agent.getAttribute("index_on_path");
			if (index < graph.getEdges().size()) {
				agent.setAttribute("current_edge", graph.getEdges().get(index));
			}
		}
	}

	/**
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException Prim: move randomly. Has to be redefined for
	 *                              every class that implements this interface.
	 *
	 * @param args the args speed (meter/sec) : the speed with which the agent wants
	 *             to move distance (meter) : the distance the agent want to cover
	 *             in one step amplitude (in degrees) : 360 or 0 means completely
	 *             random move, while other values, combined with the heading of the
	 *             agent, define the angle in which the agent will choose a new
	 *             place. A bounds (geometry, agent, list of agents, list of
	 *             geometries, species) can be specified
	 * @return the path followed
	 */

	protected double computeHeadingFromAmplitude(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final double ampl = scope.hasArg("amplitude") ? scope.getFloatArg("amplitude") : 359;
		setHeading(agent, getHeading(agent) + scope.getRandom().between(-ampl / 2.0, ampl / 2.0));
		return getHeading(agent);
	}

	protected double computeHeading(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Double heading = scope.hasArg(IKeyword.HEADING) ? scope.getFloatArg(IKeyword.HEADING) : null;
		if (heading != null) {
			setHeading(agent, heading);
		}
		return getHeading(agent);
	}

	protected double computeDistance(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		// We do not change the speed of the agent anymore. Only the current
		// primitive is affected
		final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		// 20/1/2012 Change : The speed of the agent is multiplied by the
		// timestep in order to
		// obtain the maximal distance it can cover in one step.
		return s * scope.getClock().getStepInSeconds();
	}

	protected IShape computeTarget(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Object target = scope.getArg("target", IType.NONE);
		IShape result = null;
		if (target instanceof IShape) {
			result = (IShape) target;// ((ILocated) target).getLocation();
		}
		// if ( result == null ) {
		// scope.setStatus(ExecutionStatus.failure);
		// }
		return result;
	}

	protected ITopology computeTopology(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Object on = scope.getArg("on", IType.NONE);
		final ITopology topo = Cast.asTopology(scope, on);
		if (topo == null) {
			return scope.getTopology();
		}
		return topo;
	}

	protected Map computeMoveWeights(final IScope scope) throws GamaRuntimeException {
		return scope.hasArg("move_weights") ? (Map) scope.getArg("move_weights", IType.MAP) : null;
	}

	@action(name = "wander", args = {
			@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "amplitude", type = IType.FLOAT, optional = true, doc = @doc("a restriction placed on the random heading choice. The new heading is chosen in the range (heading - amplitude/2, heading+amplitude/2)")),
			@arg(name = IKeyword.BOUNDS, type = IType.GEOMETRY, optional = true, doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry)")),
			@arg(name = IKeyword.ON, type = IType.GRAPH, optional = true, doc = @doc("the graph that restrains this move (the agent moves on the graph")),
			@arg(name = "proba_edges", type = IType.MAP, optional = true, doc = @doc("When the agent moves on a graph, the probability to choose another edge. If not defined, each edge has the same probability to be chosen")) }, doc = @doc(examples = {
					@example("do wander speed: speed - 10 amplitude: 120 bounds: agentA;") }, value = "Moves the agent towards a random location at the maximum distance (with respect to its speed). The heading of the agent is chosen randomly if no amplitude is specified. This action changes the value of heading."))
	public void primMoveRandomly(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ILocation location = agent.getLocation();
		final double heading = computeHeadingFromAmplitude(scope, agent);
		final double dist = computeDistance(scope, agent);

		ILocation loc = scope.getTopology().getDestination(location, heading, dist, true);
		if (loc == null) {
			setHeading(agent, heading - 180);
			// pathFollowed = null;
		} else {
			final Object on = scope.getArg(IKeyword.ON, IType.GRAPH);
			Double newHeading = null;
			if (on instanceof GamaSpatialGraph) {
				final GamaSpatialGraph graph = (GamaSpatialGraph) on;
				IMap<IShape, Double> probaDeplacement = null;
				if (scope.hasArg("proba_edges")) {
					probaDeplacement = (IMap<IShape, Double>) scope.getVarValue("proba_edges");
				}
				moveToNextLocAlongPathSimplified(scope, agent, graph, dist, probaDeplacement);
				return;
			}
			final Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			if (bounds != null) {
				IShape geom = GamaGeometryType.staticCast(scope, bounds, null, false);

				if (geom.getGeometries().size() > 1) {
					for (final IShape g : geom.getGeometries()) {
						if (g.euclidianDistanceTo(location) < 0.01) {
							geom = g;
							break;
						}
					}
				}
				if (geom.getInnerGeometry() != null) {
					final ILocation loc2 = computeLocationForward(scope, dist, loc, geom);
					if (!loc2.equals(loc)) {
						newHeading = heading - 180;
						loc = loc2;
					}
				}
			}

			// Enable to use wander in 3D space. An agent will wander in the
			// plan define by its z value.
			((GamaPoint) loc).z = agent.getLocation().getZ();
			agent.setAttribute(IKeyword.REAL_SPEED,
					loc.euclidianDistanceTo(location) / scope.getClock().getStepInSeconds());

			setLocation(agent, loc);
			if (newHeading != null) {
				setHeading(agent, newHeading);

			}
		}
	}

	@action(name = "move", args = {
			@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = IKeyword.HEADING, type = IType.FLOAT, optional = true, doc = @doc("the angle (in degree) of the target direction.")),
			@arg(name = IKeyword.BOUNDS, type = IType.GEOMETRY, optional = true, doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry"))

	}, doc = @doc(examples = {
			@example("do move speed: speed - 10 heading: heading + rnd (30) bounds: agentA;") }, value = "moves the agent forward, the distance being computed with respect to its speed and heading. The value of the corresponding variables are used unless arguments are passed."))

	public IPath primMoveForward(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ILocation location = agent.getLocation();
		final double dist = computeDistance(scope, agent);
		final double heading = computeHeading(scope, agent);

		ILocation loc = scope.getTopology().getDestination(location, heading, dist, true);
		if (loc == null) {
			setHeading(agent, heading - 180);
		} else {
			final Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			if (bounds != null) {
				final IShape geom = GamaGeometryType.staticCast(scope, bounds, null, false);
				if (geom != null && geom.getInnerGeometry() != null) {
					loc = computeLocationForward(scope, dist, loc, geom);
				}
			}
			setLocation(agent, loc);
		}
		if (loc != null) {
			agent.setAttribute(IKeyword.REAL_SPEED,
					loc.euclidianDistanceTo(location) / scope.getClock().getStepInSeconds());
		} else {
			agent.setAttribute(IKeyword.REAL_SPEED, 0.0);
		}
		return null;
	}

	@action(name = "follow", args = {
			@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "path", type = IType.PATH, optional = false, doc = @doc("a path to be followed.")),
			@arg(name = "move_weights", type = IType.MAP, optional = true, doc = @doc("Weights used for the moving.")),
			@arg(name = "return_path", type = IType.BOOL, optional = true, doc = @doc("if true, return the path followed (by default: false)")) }, doc = @doc(value = "moves the agent along a given path passed in the arguments.", returns = "optional: the path followed by the agent.", examples = {
					@example("do follow speed: speed * 2 path: road_path;") }))
	public IPath primFollow(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final double dist = computeDistance(scope, agent);
		final Boolean returnPath = (Boolean) scope.getArg("return_path", IType.BOOL);
		final IMap weigths = (IMap) computeMoveWeights(scope);
		final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.PATH) : null;
		if (path != null && !path.getEdgeList().isEmpty()) {
			if (returnPath != null && returnPath) {
				final IPath pathFollowed = moveToNextLocAlongPath(scope, agent, path, dist, weigths);
				if (pathFollowed == null) {
					notMoving(agent);

					return null;
				}
				return pathFollowed;
			}
			moveToNextLocAlongPathSimplified(scope, agent, path, dist, weigths);
			return null;
		}
		notMoving(agent);
		return null;
	}

	@action(name = "goto", args = {
			@arg(name = "target", type = IType.GEOMETRY, optional = false, doc = @doc("the location or entity towards which to move.")),
			@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "on", type = IType.NONE, optional = true, doc = @doc("graph, topology, list of geometries or map of geometries that restrain this move")),
			@arg(name = "recompute_path", type = IType.BOOL, optional = true, doc = @doc("if false, the path is not recompute even if the graph is modified (by default: true)")),
			@arg(name = "return_path", type = IType.BOOL, optional = true, doc = @doc("if true, return the path followed (by default: false)")),
			@arg(name = "move_weights", type = IType.MAP, optional = true, doc = @doc("Weights used for the moving.")) }, doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "optional: the path followed by the agent.", examples = {
					@example("do goto target: (one_of road).location speed: speed * 2 on: road_network;") }))
	public IPath primGoto(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ILocation source = agent.getLocation().copy(scope);
		final double maxDist = computeDistance(scope, agent);
		IShape goal = computeTarget(scope, agent);
		final Boolean returnPath = scope.hasArg("return_path") ? (Boolean) scope.getArg("return_path", IType.NONE)
				: false;
		IContainer on = null;

		Object onV = scope.getArg("on", IType.NONE);
		Object rt;
		if (onV instanceof IShape && ((IShape) onV).isLine()) {
			rt = onV;
		} else {
			if (onV instanceof ISpecies) {
				on = ((ISpecies) onV).listValue(scope, Types.AGENT, false);
			} else if (onV instanceof IList) {

				on = GamaListFactory.create(Types.AGENT);
				final IList ags = (IList) onV;
				if (!ags.isEmpty() && ags.get(0) instanceof IAgent) {
					((IList) on).addAll(ags);
					onV = ((IAgent) ags.get(0)).getSpecies();
				}
			} else if (onV instanceof IMap) {
				on = GamaMapFactory.wrap(Types.AGENT, Types.NO_TYPE, (IMap) onV);
				onV = ((IAgent) ((IMap) onV).getKeys().get(scope, 0)).getSpecies();
			}
			rt = Cast.asTopology(scope, onV instanceof IMap ? ((IMap) onV).keySet() : onV);
		}

		if (on != null && on.isEmpty(scope)) {
			on = null;
		}
		final IShape edge = rt instanceof IShape ? (IShape) rt : null;
		final ITopology topo = rt instanceof ITopology ? (ITopology) rt : scope.getTopology();
		if (goal == null) {
			notMoving(agent);
			if (returnPath) {
				return PathFactory.newInstance(scope, topo, source, source, GamaListFactory.EMPTY_LIST, false);
			}
			return null;
		}
		if (topo == null) {
			notMoving(agent);
			if (returnPath) {
				return PathFactory.newInstance(scope, topo, source, source, GamaListFactory.EMPTY_LIST, false);
			}
			return null;
		}
		if (topo instanceof GridTopology) {
			// source =
			// ((GamaSpatialMatrix)topo.getPlaces()).getAgentAt(source).getLocation();
			goal = ((GamaSpatialMatrix) topo.getPlaces()).getAgentAt(goal.getLocation()).getLocation();
		}
		if (source.equals(goal.getLocation())) {
			notMoving(agent);
			if (returnPath) {
				return PathFactory.newInstance(scope, topo, source, source, GamaListFactory.EMPTY_LIST, false);
			}

			return null;
		}

		Boolean recomputePath = (Boolean) scope.getArg("recompute_path", IType.NONE);
		if (recomputePath == null) {
			recomputePath = true;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if (recomputePath && topo instanceof GridTopology) {
			agent.setAttribute("current_path", null);
			path = null;
		}
		if (path == null || path.getTopology(scope) != null && !path.getTopology(scope).equals(topo)
				|| !((IShape) path.getEndVertex()).getLocation().equals(goal.getLocation())
				|| !((IShape) path.getStartVertex()).getLocation().equals(source.getLocation())) {

			if (edge != null) {
				final IList<IShape> edges = GamaListFactory.create(Types.GEOMETRY);
				edges.add(edge);
				path = new GamaSpatialPath(source.getGeometry(), goal, edges, true);
			} else {
				if (topo instanceof GridTopology) {
					if (on instanceof IList) {
						path = ((GridTopology) topo).pathBetween(scope, source, goal, (IList) on);
					} else if (on instanceof IMap) {
						path = ((GridTopology) topo).pathBetween(scope, source, goal, (IMap) on);
					}

				} else {
					path = topo.pathBetween(scope, agent, goal);
				}
			}
		} else {

			if (topo instanceof GraphTopology) {
				if (((GraphTopology) topo).getPlaces() != path.getGraph()
						|| recomputePath && ((GraphTopology) topo).getPlaces().getVersion() != path.getGraphVersion()) {
					path = topo.pathBetween(scope, agent, goal);
				}
			}
		}
		if (path == null) {
			notMoving(agent);
			if (returnPath) {
				return PathFactory.newInstance(scope, topo, source, source,
						GamaListFactory.<IShape>create(Types.GEOMETRY), false);
			}
			return null;
		}

		final IMap weigths = (IMap) computeMoveWeights(scope);
		if (returnPath) {
			final IPath pathFollowed = moveToNextLocAlongPath(scope, agent, path, maxDist, weigths);
			if (pathFollowed == null) {
				return PathFactory.newInstance(scope, topo, source, source,
						GamaListFactory.<IShape>create(Types.GEOMETRY), false);
			}
			return pathFollowed;
		}
		moveToNextLocAlongPathSimplified(scope, agent, path, maxDist, weigths);
		return null;
	}

	private void notMoving(final IAgent agent) {
		setRealSpeed(agent, 0.0);
		agent.setAttribute("current_edge", null);
		agent.setAttribute("current_path", null);
	}

	/**
	 * @throws GamaRuntimeException Return the next location toward a target on a
	 *                              line
	 *
	 * @param coords   coordinates of the line
	 * @param source   current location
	 * @param target   location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */

	protected IList initMoveAlongPath3D(final IAgent agent, final IPath path, final GamaPoint cl) {
		GamaPoint currentLocation = cl.copy(GAMA.getRuntimeScope());
		final IList initVals = GamaListFactory.create();

		Integer index = 0;
		Integer indexSegment = 1;
		Integer endIndexSegment = 1;
		GamaPoint falseTarget = null;
		final IList<IShape> edges = path.getEdgeGeometry();
		if (path.isVisitor(agent)) {
			index = path.indexOf(agent);
			indexSegment = path.indexSegmentOf(agent);

		} else {
			if (edges.isEmpty()) {
				return null;
			}
			path.acceptVisitor(agent);

			double dist = Double.MAX_VALUE;
			int i = 0;
			for (final IShape e : edges) {
				final GamaPoint[] points = getPointsOf(e);
				int j = 0;
				for (final GamaPoint pt : points) {
					final double d = pt.euclidianDistanceTo(cl);
					if (d < dist) {
						currentLocation = pt;
						dist = d;
						index = i;
						indexSegment = j + 1;
						if (dist == 0.0) {
							break;
						}
					}
					j++;
				}

				if (dist == 0.0) {
					break;
				}
				i++;
			}
		}
		final GamaPoint[] points = getPointsOf(edges.lastValue(GAMA.getRuntimeScope()));
		int j = 0;
		double dist = Double.MAX_VALUE;
		final ILocation end = ((IShape) path.getEndVertex()).getLocation();
		for (final GamaPoint pt : points) {
			final double d = pt.euclidianDistanceTo(end);
			if (d < dist) {
				dist = d;
				endIndexSegment = j;
				falseTarget = pt;
				if (dist == 0.0) {
					break;
				}

			}
			j++;
		}
		initVals.add(index);
		initVals.add(indexSegment);
		initVals.add(endIndexSegment);
		initVals.add(currentLocation);
		initVals.add(falseTarget);
		return initVals;
	}

	protected IList initMoveAlongPath(final IAgent agent, final IPath path, final GamaPoint cl) {
		return null;
	}

	@SuppressWarnings("null")
	protected IList initMoveAlongPath(final IScope scope, final IAgent agent, final GamaSpatialGraph graph,
			final GamaPoint currentLoc) {
		return null;
	}

	public void moveToNextLocAlongPathSimplified(final IScope scope, final IAgent agent, final GamaSpatialGraph graph,
			final double d, final IMap probaEdge) {
		return;
	}

	private void moveToNextLocAlongPathSimplified(final IScope scope, final IAgent agent, final IPath path,
			final double d, final IMap weigths) {
		return;
	}

	protected double computeWeigth(final IGraph graph, final IPath path, final IShape line) {
		return 0.0;
	}

	private IPath moveToNextLocAlongPath(final IScope scope, final IAgent agent, final IPath path, final double d,
			final IMap weigths) {
		return null;
	}

	protected ILocation computeLocationForward(final IScope scope, final double dist, final ILocation loc,
			final IShape geom) {
		final IList pts = GamaListFactory.create(Types.POINT);
		pts.add(scope.getAgent().getLocation());
		pts.add(loc);
		final IShape line = Spatial.Creation.line(scope, pts);
		// line = Spatial.Operators.inter(scope, line, geom);

		if (line == null) {
			return getCurrentAgent(scope).getLocation();
		}
		if (geom.covers(line)) {
			return loc;
		}

		// final ILocation computedPt = line.getPoints().lastValue(scope);

		final ILocation computedPt = Spatial.Punctal.closest_points_with(line, geom.getExteriorRing(scope)).get(0);
		if (computedPt != null && computedPt.intersects(geom)) {
			return computedPt;
		}
		return getCurrentAgent(scope).getLocation();
	}
}
