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

    public void add(E val) {
        nodes.add(val);
    }

    public boolean contains(E val) {
        return nodes.contains(val);
    }

    /**
     * method for determining whether the click contains the element
     * @param clique for which we want to know if it contains the element
     * @return true if the clique contains the element
     */
    public boolean contains(Clique<E> clique) {
        for (E elem : clique.nodes)
            if (!nodes.contains(elem))
                return false;
        return true;
    }

    public int size() {
        return nodes.size();
    }

    public boolean equal(Clique<E> clique) {
        return clique.nodes.size() == nodes.size() && contains(clique);
    }

    /**
     * method to
     * @param graph
     * @return
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
