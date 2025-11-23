package graph;

public class Edge<V> {
    public V to;
    public int weight;


    public Edge(V to, int weight) {
        this.to = to;
        this.weight = weight;
    }
}