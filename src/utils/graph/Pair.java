package utils.graph;

/**
 * Class which represents
 * @param <E>
 */
public class Pair<E> {

    private E v1;
    private E v2;

    public Pair(E v1, E v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public E getV1() {
        return v1;
    }

    public void setV1(E v1) {
        this.v1 = v1;
    }

    public E getV2() {
        return v2;
    }

    public void setV2(E v2) {
        this.v2 = v2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair<E> p2 = (Pair<E>) obj;
            return v1.equals(p2.v1) || v1.equals(p2.v2) || v2.equals(p2.v1) || v2.equals(p2.v2);
        }
        return false;
    }

    @Override
    public String toString() {
        return v1 + " / " + v2;
    }
}
