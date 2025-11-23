package ds;
/*базовый интерфейс списка */
public interface List <E> {
    void add(E value);
    E get(int index);
    void removeAt(int index);
    int size();
}
