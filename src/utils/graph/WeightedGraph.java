package utils.graph;

import java.util.*;
import java.util.List;

public class WeightedGraph<E extends Comparable<E> & Duplicable<E>> {

    Map<E, Node<E>> nodes;
    List<Clique<E>> cliques;
    int size;

    public WeightedGraph() {
        nodes = new TreeMap<>();
        size = 0;
    }

    public void add(E value) {
        if (nodes.containsKey(value)) {
            return;
        }
        nodes.put(value, new Node<E>(value));
    }

    public Node<E> getNode(E key) {
        return nodes.get(key);
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

    public int size() {
        return nodes.size();
    }

    public Set<E> getSet() {
        return nodes.keySet();
    }

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

    public List<Clique<E>> getCliques() {
        if (cliques == null)
            computeCliquesDecomp();
        return cliques;
    }

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
