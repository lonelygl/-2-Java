package util;
import ds.ArrayList;
/* базовые операции стека для алгоритмов на графе*/
public class Stack<E> {
    private ArrayList<E> list = new ArrayList<>();

    public void push(E v) { list.add(v); }
    public E pop() {
        int s = list.size();
        if (s == 0) return null;
        E v = list.get(s - 1);
        list.removeAt(s - 1);
        return v;
    }
    public E peek() {
        int s = list.size();
        if (s == 0) return null;
        return list.get(s - 1);
    }
    public int size() { return list.size(); }
}