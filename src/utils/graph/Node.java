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

    /**
     * Set the value of the node
     * @param value the new node's value
     */
    public void setValue(E value) {
        this.value = value;
    }

    /**
     * Return the value of the node
     * @return the node's value
     */
    public E getValue() {
        return value;
    }

    /**
     * Get the neighbors of the node
     * @return a map of neighbors and link weight
     */
    public Map<Node<E>, Float> getNeighbors() {
        return neighbors;
    }

    /**
     * method to get the weight of a link between this node and another
     * @param node for which to get the weight
     * @return the weight of the link
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
     * @param weight of the link
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
