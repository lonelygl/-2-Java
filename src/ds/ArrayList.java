package ds;
/*реализация динамического массива*/
public class ArrayList<E> implements List<E> {
    private Object[] data;
    private int size;


    public ArrayList() {
        this.data = new Object[8];
        this.size = 0;
    }


    private void ensureCapacity() {
        if (size >= data.length) {
            Object[] nd = new Object[data.length * 2];
            for (int i = 0; i < data.length; i++) nd[i] = data[i];
            data = nd;
        }
    }


    @Override
    public void add(E value) {
        ensureCapacity();
        data[size++] = value;
    }


    @Override
    public E get(int index) {
        if (index < 0 || index >= size) return null;
        return (E) data[index];
    }


    @Override
    public void removeAt(int index) {
        if (index < 0 || index >= size) return;
        for (int i = index; i < size - 1; i++) data[i] = data[i + 1];
        data[--size] = null;
    }


    @Override
    public int size() {
        return size;
    }
}