package utils.graph;

import java.util.HashMap;
import java.util.Map;

public class Node<E> {

    private E value;
    Map<Node<E>, Float> neighbors;

    public Node(E value) {
        this.value = value;
        neighbors = new HashMap<>();
    }

    public void addNeighbor(Node<E> n, float weight) {
        if (neighbors.containsKey(n))
            neighbors.replace(n, neighbors.get(n) + weight);
        else
            neighbors.put(n, weight);
    }

    public void disconnect(Node<E> n) {
        neighbors.remove(n);
        n.neighbors.remove(this);
    }

    public void setValue(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }

    public Map<Node<E>, Float> getNeighbors() {
        return neighbors;
    }

    public float getWeight(Node<E> node) {
        Float val = neighbors.get(node);
        if (val == null)
            return 0;
        return val;
    }
}
