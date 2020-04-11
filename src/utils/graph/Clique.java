package utils.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents a clique
 * @param <E>
 */
public class Clique<E extends Duplicable<E> & Comparable<E>> {

    List<E> nodes;

    public Clique() {
        nodes = new ArrayList<>();
    }

    /**
     * Add a new Node to the graph
     * @param val the node to add
     */
    public void add(E val) {
        nodes.add(val);
    }

    /**
     * Return whether or not the graph contains a value
     * @param val the value to test
     * @return does the graph contains val
     */
    public boolean contains(E val) {
        return nodes.contains(val);
    }

    /**
     * method for determining whether the graph contains another graph or not
     * @param clique the graph to test if contained
     * @return true if the passed graph is a subgraph of the current one
     */
    public boolean contains(Clique<E> clique) {
        for (E elem : clique.nodes)
            if (!nodes.contains(elem))
                return false;
        return true;
    }

    /**
     * Return the number of node of the clique
     * @return the size of the clique
     */
    public int size() {
        return nodes.size();
    }

    public boolean equal(Clique<E> clique) {
        return clique.nodes.size() == nodes.size() && contains(clique);
    }

    /**
     * method to convert the clique to a WeightedGraph
     * @param graph the graph from which to get the links' weights
     * @return a graph representing the clique
     */
    public WeightedGraph<E> toGraph(WeightedGraph<E> graph) {
        WeightedGraph<E> cGraph = new WeightedGraph<>();
        for (E e : nodes) {
            cGraph.add(e);
        }
        for (E e1 : nodes) {
            for (E e2 : nodes) {
                if (e1 == e2)
                    continue;
                float weight = graph.getConnectionWeight(e1, e2);
                cGraph.connect(e1, e2, weight);
            }
        }
        return cGraph;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[");
        for (E e : nodes)
            s.append(e).append(", ");
        return s.toString();
    }
}
