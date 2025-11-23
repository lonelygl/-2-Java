package util;
import ds.ArrayList;

/*базовые операции с очередью для алшоритмов на графе */
public class Queue<E> {
    private ArrayList<E> list = new ArrayList<>();


    public void enqueue(E v) { list.add(v); }
    public E dequeue() {
        if (list.size() == 0) return null;
        E v = list.get(0);
        list.removeAt(0);
        return v;
    }
    public boolean isEmpty() { return list.size() == 0; }
    public int size() { return list.size(); }
}