package utils.graph;

import java.util.*;
import java.util.List;

/**
 * Class which represents
 * @param <E>
 */
public class WeightedGraph<E extends Comparable<E> & Duplicable<E>> {

    private Map<E, Node<E>> nodes;
    private List<Clique<E>> cliques;
    private int size;

    public WeightedGraph() {
        nodes = new TreeMap<>();
        size = 0;
    }

    /**
     * Get the node with a specified value
     * @param key the value to get
     * @return the node with value key, null if not found
     */
    public Node<E> getNode(E key) {
        return nodes.get(key);
    }

    /**
     * Return the list of cliques of the graph
     * @return a list of the maximal cliques
     */
    public List<Clique<E>> getCliques() {
        if (cliques == null)
            computeCliquesDecomp();
        return cliques;
    }

    /**
     * Add a new node to the graph
     * @param value the value to add to the graph
     */
    public void add(E value) {
        if (nodes.containsKey(value)) {
            return;
        }
        nodes.put(value, new Node<E>(value));
    }

    /**
     * Connect two value
     * @param v1 first vertex of the connection
     * @param v2 second vertex of the connection
     * @param weight of the connection between vertices
     */
    public void connect(E v1, E v2, float weight) {
        Node<E> n1, n2;
        if (nodes.containsKey(v1))
            n1 = nodes.get(v1);
        else {
            n1 = new Node<E>(v1);
            nodes.put(v1, n1);
        }
        if (nodes.containsKey(v2))
            n2 = nodes.get(v2);
        else {
            n2 = new Node<E>(v2);
            nodes.put(v2, n2);
        }
        n1.addNeighbor(n2, weight);
        n2.addNeighbor(n1, weight);
    }

    /**
     * method to get the weight of a connection
     * @param v1 first vertex of the connection
     * @param v2 second vertex of the connection
     * @return the weight of the connection
     */
    public float getConnectionWeight(E v1, E v2) {
        if (nodes.containsKey(v1) && nodes.containsKey(v2)) {
            return nodes.get(v1).getWeight(nodes.get(v2));
        }
        return 0;
    }

    /**
     * Return the size of the graph
     * @return the size of the graph
     */
    public int size() {
        return nodes.size();
    }

    /**
     * Return a set of the graph's nodes
     * @return a set of the graph's nodes
     */
    public Set<E> getSet() {
        return nodes.keySet();
    }

    /**
     * Return the n strongest connection of the graph
     * @param n number of connection to get
     * @return the strongest connection of the graph
     */
    public List<Pair<E>> getStrongestConnection(int n) {
        List<Pair<E>> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            float max = Float.MIN_VALUE;
            Pair<E> best = null;
            for (E e : nodes.keySet()) {
                Map<Node<E>, Float> neighbors = nodes.get(e).getNeighbors();
                for (Map.Entry<Node<E>, Float> entry : neighbors.entrySet()) {
                    if (entry.getValue() > max) {
                        Pair<E> tmp = new Pair<E>(e, entry.getKey().getValue());
                        if (!list.contains(tmp)) {
                            best = tmp;
                            max = entry.getValue();
                        }
                    }
                }
            }
            System.out.println(max + " : " + best);
            list.add(best);
        }
        return list;
    }

    /**
     * Compute a list a cliques representing
     * The list only contains maximal cliques
     */
    private void computeCliquesDecomp() {
        List<Clique<E>> cliques = new ArrayList<>();
        List<Clique<E>> maximalCliques = new ArrayList<>();
        for (Map.Entry<E, Node<E>> entry : nodes.entrySet()) {
            for (Clique<E> clique : cliques) {
                boolean add = true;
                for (E val : clique.nodes) {
                    if (entry.getValue().getWeight(nodes.get(val)) == 0) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    clique.add(entry.getKey().duplicate());
                }

            }

            for (Node<E> neighbor : entry.getValue().getNeighbors().keySet()) {
                Clique<E> clique = new Clique<>();
                clique.add(entry.getKey().duplicate());
                clique.add(neighbor.getValue().duplicate());
                cliques.add(clique);
            }
        }

        for (Clique<E> clique : cliques) {
            boolean add = true;
            for (Clique<E> other : maximalCliques) {
                if (clique != other) {
                    if (other.contains(clique)) {
                        add = false;
                        break;
                    }
                }
            }
            if (add)
                maximalCliques.add(clique);
        }
        this.cliques = maximalCliques;
    }


    /**
     * Simplify the graph by removing the links with a weight smaller than a threshold
     * @param threshold the threshold value
     */
    public void simplify(float threshold) {
        Map<E, Node<E>> newNodes = new HashMap<>();
        for (Map.Entry<E, Node<E>> node : nodes.entrySet()) {
            Map<Node<E>, Float> neighbors = node.getValue().getNeighbors();
            for (Node<E> n : neighbors.keySet()) {
                float weight = getConnectionWeight(node.getKey(), n.getValue());
                if (weight >= threshold)
                    newNodes.put(node.getKey(), node.getValue());
            }
        }
        nodes.clear();
        nodes = newNodes;
        for (Map.Entry<E, Node<E>> node : nodes.entrySet()) {
            Map<Node<E>, Float> neighbors = node.getValue().getNeighbors();
            List<Node<E>> toRemove = new ArrayList<>();
            for (Node<E> n : neighbors.keySet()) {
                if (!nodes.containsKey(n.getValue()) || node.getValue().getWeight(n) < threshold)
                    toRemove.add(n);
            }
            for (Node<E> n : toRemove) {
                node.getValue().disconnect(n);
            }
        }
    }
}
