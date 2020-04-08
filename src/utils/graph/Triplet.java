package utils.graph;

public class Triplet<E> implements Comparable<Triplet<E>>, Duplicable<Triplet<E>> {

    private E v1;
    private E v2;
    private E v3;

    public Triplet(E v1, E v2, E v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
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

    public E getV3() {
        return v3;
    }

    public void setV3(E v3) {
        this.v3 = v3;
    }

    @Override
    public int compareTo(Triplet triplet) {
        return triplet.v1.equals(v1) && triplet.v2.equals(v2) && triplet.v3.equals(v3) ? 0 : 1;
    }

    @Override
    public Triplet<E> duplicate() {
        return new Triplet<E>(v1, v2, v3);
    }
}
