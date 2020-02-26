package utils.graph;

import java.util.Map;
import java.util.TreeMap;

public class WeightedGraph<E extends Comparable<E>> {

    Map<E, Node<E>> nodes;
    int size;

    public WeightedGraph() {
        nodes = new TreeMap<>();
        size = 0;
    }

    public void add(E value) {
        if (nodes.containsKey(value))
            return;
        nodes.put(value, new Node<E>(value));
    }

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

    public float getConnectionWeight(E v1, E v2) {
        if (nodes.containsKey(v1) && nodes.containsKey(v2)) {
            return nodes.get(v1).getWeight(nodes.get(v2));
        }
        return 0;
    }

    public void replace(E value) {
        nodes.get(value).setValue(value);
    }

}
