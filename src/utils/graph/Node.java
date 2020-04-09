package utils.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Class which represents a node
 * @param <E>
 */
public class Node<E> {

    private E value;
    Map<Node<E>, Float> neighbors;

    public Node(E value) {
        this.value = value;
        neighbors = new HashMap<>();
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

    /**
     * method to get the weight of a node
     * @param node for which to get the weight
     * @return the weight of the node
     */
    public float getWeight(Node<E> node) {
        Float val = neighbors.get(node);
        if (val == null)
            return 0;
        return val;
    }

    /**
     * method to add a neighbors to an element
     * @param n the node to add to
     * @param weight of the node
     */
    public void addNeighbor(Node<E> n, float weight) {
        if (neighbors.containsKey(n))
            neighbors.replace(n, neighbors.get(n) + weight);
        else
            neighbors.put(n, weight);
    }

    /**
     * method to disconnect a node of a neighbor
     * @param n node to disconnect with
     */
    public void disconnect(Node<E> n) {
        neighbors.remove(n);
        n.neighbors.remove(this);
    }

}
