/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.AbstractTopology.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Ordering;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.continuous.RootTopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.path.GamaSpatialPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.operators.Maths;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public abstract class AbstractTopology implements ITopology {

	@Override
	public IType<?> getGamlType() {
		return Types.TOPOLOGY;
	}

	protected IShape environment;
	protected RootTopology root;
	protected IContainer<?, IShape> places;
	protected List<ISpecies> speciesInserted;

	// VARIABLES USED IN TORUS ENVIRONMENT
	private double[][] adjustedXYVector = null;

	public AbstractTopology(final IScope scope, final IShape env, final RootTopology root) {
		setRoot(scope, root);
		speciesInserted = new ArrayList<>();
		environment = env;
	}

	@Override
	public void setRoot(final IScope scope, final RootTopology root) {
		this.root = root == null ? scope.getSimulation().getTopology() : root;
	}

	@Override
	public List<Object> listToroidalGeometries(final Object geom) {
		return null;
	}

	public Object returnToroidalGeom(final GamaPoint loc) {
		return null;
	}

	public Object returnToroidalGeom(final IShape shape) {
		return null;
	}

	public Map<Object, IAgent> toroidalGeoms(final IScope scope, final IContainer<?, ? extends IShape> shps) {
		return null;
	}

	protected void createVirtualEnvironments() {}

	protected boolean canCreateAgents() {
		return false;
	}

	/**
	 * @see msi.gama.environment.ITopology#initialize(msi.gama.interfaces.IPopulation)
	 */
	@Override
	public void initialize(final IScope scope, final IPopulation<? extends IAgent> pop) throws GamaRuntimeException {
		// Create the population from the places of the topology
		if (!canCreateAgents()) { return; }
		pop.createAgents(scope, places);

	}

	@Override
	public void removeAgent(final IAgent agent) {
		getSpatialIndex().remove(agent.getEnvelope(), agent);
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target)
			throws GamaRuntimeException {
		return PathFactory.newInstance(scope, this,
				GamaListFactory.create(scope, Types.POINT, new IShape[] { source.getLocation(), target.getLocation() }),
				0.0);
	}

	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target)
			throws GamaRuntimeException {
		return PathFactory.newInstance(scope, this, GamaListFactory.wrap(Types.POINT, source, target), 0.0);
	}

	@Override
	public IList<GamaSpatialPath> KpathsBetween(final IScope scope, final IShape source, final IShape target,
			final int k) {
		final IList<GamaSpatialPath> paths = GamaListFactory.create(Types.PATH);
		paths.add(pathBetween(scope, source, target));
		return paths;
	}

	@Override
	public IList<GamaSpatialPath> KpathsBetween(final IScope scope, final ILocation source, final ILocation target,
			final int k) {
		final IList<GamaSpatialPath> paths = GamaListFactory.create(Types.PATH);
		paths.add(pathBetween(scope, source, target));
		return paths;
	}

	@Override
	public void updateAgent(final Envelope3D previous, final IAgent agent) {
		if (GamaPreferences.External.QUADTREE_OPTIMIZATION.getValue()) {
			if (speciesInserted.contains(agent.getSpecies())) {
				updateAgentBase(previous, agent);
			}
		} else {
			updateAgentBase(previous, agent);
		}
	}

	public void updateAgentBase(final Envelope3D previous, final IAgent agent) {}

	@Override
	public IShape getEnvironment() {
		return environment;
	}

	@Override
	public ILocation normalizeLocation(final ILocation point, final boolean nullIfOutside) {
		return null;
	}

	@Override
	public ILocation getDestination(final ILocation source, final double direction, final double distance,
			final boolean nullIfOutside) {
		final double cos = distance * Maths.cos(direction);
		final double sin = distance * Maths.sin(direction);
		final ILocation result = source.toGamaPoint().plus(cos, sin, 0);
		return normalizeLocation(result, nullIfOutside);
	}

	@Override
	public ILocation getDestination3D(final ILocation source, final double heading, final double pitch,
			final double distance, final boolean nullIfOutside) {
		final double x = distance * Maths.cos(pitch) * Maths.cos(heading);
		final double y = distance * Maths.cos(pitch) * Maths.sin(heading);
		final double z = distance * Maths.sin(pitch);
		return normalizeLocation3D(new GamaPoint(source.getX() + x, source.getY() + y, source.getZ() + z),
				nullIfOutside);
	}

	public ILocation normalizeLocation3D(final ILocation point, final boolean nullIfOutside) {
		final ILocation p = normalizeLocation(point, nullIfOutside);
		if (p == null) { return null; }
		final double z = p.getZ();
		if (z < 0) { return null; }
		if (((GamaShape) environment.getGeometry()).getDepth() != null) {
			if (z > ((GamaShape) environment.getGeometry()).getDepth()) { return null; }
			return point;
		}
		throw GamaRuntimeException.error("The environment must be a 3D environment (e.g shape <- cube(100).", null);

	}

	@Override
	public ITopology copy(final IScope scope) throws GamaRuntimeException {
		return _copy(scope);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return _toGaml(includingBuiltIn);
	}

	/**
	 * @return a gaml description of the construction of this topology.
	 */
	protected abstract String _toGaml(boolean includingBuiltIn);

	/**
	 * @throws GamaRuntimeException
	 * @return a copy of this topology
	 */
	protected abstract ITopology _copy(IScope scope) throws GamaRuntimeException;

	@Override
	public GamaPoint getRandomLocation(final IScope scope) {
		return GeometryUtils.pointInGeom(environment, scope.getRandom());
	}

	@Override
	public IContainer<?, IShape> getPlaces() {
		return places;
	}

	protected void insertSpecies(final IScope scope, final ISpecies species) {
		if (!this.speciesInserted.contains(species)) {
			this.speciesInserted.add(species);
			for (final IAgent ag : species.getPopulation(scope)) {
				getSpatialIndex().insert(ag);
			}
		}
	}

	protected void insertAgents(final IScope scope, final IAgentFilter filter) {
		if (GamaPreferences.External.QUADTREE_OPTIMIZATION.getValue()) {
			if (filter.getSpecies() != null) {
				insertSpecies(scope, filter.getSpecies());
			} else {
				final IPopulation<? extends IAgent> pop = filter.getPopulation(scope);
				if (pop != null) {
					insertSpecies(scope, pop.getSpecies());
				}
			}
		}
	}

	@Override
	public Collection<IAgent> getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter,
			final int number) {
		return null;
	}

	@Override
	public IAgent getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter) {
		return null;
	}

	@Override
	public IAgent getAgentFarthestTo(final IScope scope, final IShape source, final IAgentFilter filter) {
		return null;
	}

	public Map<Object, IAgent> getTororoidalAgents(final IShape source, final IScope scope,
			final IAgentFilter filter) {
		return toroidalGeoms(scope, getFilteredAgents(source, scope, filter));
	}

	@SuppressWarnings ("unchecked")
	public static IContainer<?, ? extends IShape> getFilteredAgents(final IShape source, final IScope scope,
			final IAgentFilter filter) {
		IContainer<?, ? extends IShape> shps;
		if (filter != null) {
			if (filter.hasAgentList()) {
				shps = filter.getAgents(scope);
			} else {
				shps = scope.getSimulation().getAgents(scope);
				filter.filter(scope, source, (Collection<? extends IShape>) shps);
			}
		} else {
			shps = scope.getSimulation().getAgents(scope);
		}
		return shps;
	}

	@Override
	public Collection<IAgent> getNeighborsOf(final IScope scope, final IShape source, final Double distance,
			final IAgentFilter filter) throws GamaRuntimeException {
		return null;
	}

	@Override
	public double getWidth() {
		return 0.0;
	}

	@Override
	public double getHeight() {
		return 0.0;
	}

	@Override
	public void dispose() {
		// host = null;
		// scope = null;
	}

	private final Object pgFact = new Object();

	@Override
	public Collection<IAgent> getAgentsIn(final IScope scope, final IShape source, final IAgentFilter f,
			final boolean covered) {
		return null;
	}

	@Override
	public ISpatialIndex getSpatialIndex() {
		return root.getSpatialIndex();
	}

	@Override
	public boolean isTorus() {
		return root.isTorus();
	}

	protected double[][] getAdjustedXYVector() {
		if (adjustedXYVector == null) {
			createVirtualEnvironments();
		}
		return adjustedXYVector;
	}

}
