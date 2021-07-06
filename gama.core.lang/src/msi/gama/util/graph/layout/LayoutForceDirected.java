package msi.gama.util.graph.layout;

import java.util.IdentityHashMap;
import java.util.Map;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gaml.operators.Containers;
import msi.gaml.operators.Points;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.types.Types;

public class LayoutForceDirected {

	private final Object graph;
	private final boolean equi;
	private final double criterion;
	private final double coolingRate;
	private final int maxit;
	private final double coeffForce;
	IShape bounds;

	private int iteration = 0;

	private double area;
	private double k;
	private double t;

	private boolean equilibriumReached = false;
	private final Map<IShape, GamaPoint> disp;
	private final Map<IShape, GamaPoint> loc;

	/**
	 * Creates a new Simulation.
	 *
	 * @param graph
	 * @param p
	 * @throws ParseException
	 */
	public LayoutForceDirected(final Object graph, final IShape bounds, final double coeffForce,
			final double coolingRate, final int maxit, final boolean isEquilibriumCriterion, final double criterion) {
		this.graph = graph;
		this.bounds = bounds;
		this.equi = isEquilibriumCriterion;
		this.criterion = criterion;
		this.coolingRate = coolingRate;
		this.maxit = maxit;
		this.coeffForce = coeffForce;
		this.disp = new IdentityHashMap<>();
		this.loc = new IdentityHashMap<>();

	}

	/**
	 * Starts the simulation.
	 *
	 * @return number of iterations used until criterion is met
	 */
	public int startSimulation(final IScope scope) {
		return 0;
	}

	/**
	 * Simulates a single step.
	 */
	private void simulateStep(final IScope scope) {
		return;
	}

	/**
	 * Calculates the amount of the attractive force between vertices using the
	 * expression entered by the user.
	 *
	 * @param d the distance between the two vertices
	 * @param k
	 * @return amount of force
	 */
	private double forceAttractive(final double d, final double k) {
		return k == 0 ? 1 : d * d / k;
	}

	/**
	 * Calculates the amount of the repulsive force between vertices using the
	 * expression entered by the user.
	 *
	 * @param d the distance between the two vertices
	 * @param k
	 * @return amount of force
	 */
	private double forceRepulsive(final double d, final double k) {
		return d == 0 ? 1 : k * k / d;
	}

}
