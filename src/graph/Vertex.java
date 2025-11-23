package graph;
import ds.ArrayList;

public class Vertex<V> {
    public V value;
    public ArrayList<Edge<V>> edges = new ArrayList<>();
    public int x, y; // координаты для визуализации


    public Vertex(V value) {
        this.value = value;
    }
}