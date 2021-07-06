/*******************************************************************************************************
 *
 * msi.gama.util.graph.GraphAndPopulationsSynchronizer.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.types.GamaGeometryType;

/**
 * Syncs a graph with two populations of agents (one for edges, one for nodes).
 * <ul>
 * <li>When a node agent dies, the corresponding node is removed from the
 * network.</li>
 * <li>When an edge agent dies, the corresponding edge is removed from the
 * network.</li>
 * <li>When an edge is removed, the corresponding edge agent dies.</li>
 * <li>When a node is removed, the corresponding node agent dies.</li>
 * <li>When a novel node agent is created, a novel node is created into the
 * graph</li>
 * <li>When a novel edge agent is created, an exception is thrown (creating an
 * edge without its targets is meaningless)</li>
 * </ul>
 *
 * @author Samuel Thiriot
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GraphAndPopulationsSynchronizer implements IPopulation.Listener, IGraphEventListener {

	private final IPopulation popVertices;
	private final IPopulation popEdges;
	private final IGraph graph;

	/**
	 * The last vertex and edge for which we sent an event. Avoids first-order loops
	 * between events from graphs and populations
	 */
	private Object currentEventVertex = null;
	private Object currentEventEdge = null;

	// private boolean ignoreNextEvent = false;

	private final List<Map> initialValues = Collections.EMPTY_LIST;

	public GraphAndPopulationsSynchronizer(final IPopulation popVertices, final IPopulation popEdges,
			final IGraph graph) {
		this.popVertices = popVertices;
		this.popEdges = popEdges;
		this.graph = graph;

	}

	@Override
	public void notifyAgentRemoved(final IScope scope, final IPopulation pop, final IAgent agent) {
		return;
	}

	@Override
	public void notifyAgentAdded(final IScope scope, final IPopulation pop, final IAgent agent) {
		return;
	}

	@Override
	public void notifyAgentsAdded(final IScope scope, final IPopulation pop, final Collection agents) {
		return;
	}

	@Override
	public void notifyAgentsRemoved(final IScope scope, final IPopulation pop, final Collection agents) {
		return;
	}

	@Override
	public void notifyPopulationCleared(final IScope scope, final IPopulation pop) {
		return;
	}

	@Override
	public void receiveEvent(final IScope scope, final GraphEvent event) {
		return;
	}

	/**
	 * Creates a synchronizer which listens for a population of vertices and updates
	 * the graph accordingly
	 * 
	 * @param popVertices
	 * @param graph
	 * @return
	 */
	public static GraphAndPopulationsSynchronizer synchronize(final IPopulation popVertices, final IPopulation popEdges,
			final IGraph graph) {

		final GraphAndPopulationsSynchronizer res = new GraphAndPopulationsSynchronizer(popVertices, popEdges, graph);
		popVertices.addListener(res);
		popEdges.addListener(res);
		graph.addListener(res);
		return res;

	}

}
