package graph;

import ds.ArrayList;
import ds.List;
import util.Queue;

import javax.swing.JTextArea;
import java.util.Objects;
import java.util.Random;

/*
 Универсальный класс графа.
 Поддерживает ориентированный/неориентированный вариант (флаг directed).
 Хранит вершины в ArrayList и рёбра внутри Vertex.
 */
public class Graph<V> {

    // Список вершин
    private ArrayList<Vertex<V>> vertices = new ArrayList<>();

    // Флаг: ориентированный или нет
    private boolean directed;

    /*
      Конструктор.

      @param directed true — ориентированный, false — неориентированный
     */
    public Graph(boolean directed) {
        this.directed = directed;
    }


    public boolean isDirected() {
        return directed;
    }


    public boolean isOriented() {
        return directed;
    }


      //Полностью очистить граф (удалить все вершины и рёбра).

    public void clear() {
        vertices = new ArrayList<>();
    }


    public void addVertex(V v) {
        if (v == null) return;
        if (findVertex(v) != null) return;

        Vertex<V> vert = new Vertex<>(v);

        // задаём случайные координаты для первоначальной отрисовки (если требуется)
        Random r = new Random();
        vert.x = 50 + r.nextInt(400);
        vert.y = 50 + r.nextInt(300);

        vertices.add(vert);
    }

    /*
      Добавление ребра: если ребро уже есть — метод ничего не делает
     Для невзвешенного графа вес можно передать как 1
     */
    public void addEdge(V from, V to, int weight) {
        if (from == null || to == null) return;

        Vertex<V> f = findVertex(from);
        Vertex<V> t = findVertex(to);
        if (f == null || t == null) return;

        // не добавляем дубликат
        if (!canAddEdge(from, to)) return;

        f.edges.add(new Edge<>(to, weight));
        if (!directed) {
            // для неориентированного — добавляем зеркальное ребро
            t.edges.add(new Edge<>(from, weight));
        }
    }


    public void removeVertex(V v) {
        if (v == null) return;

        // удалить саму вершину из списка
        for (int i = 0; i < vertices.size(); i++) {
            if (Objects.equals(vertices.get(i).value, v)) {
                vertices.removeAt(i);
                break;
            }
        }

        // удалить входящие рёбра (и зеркальные в неориентированном)
        for (int i = 0; i < vertices.size(); i++) {
            Vertex<V> vv = vertices.get(i);
            for (int j = 0; j < vv.edges.size(); j++) {
                if (Objects.equals(vv.edges.get(j).to, v)) {
                    vv.edges.removeAt(j);
                    j--;
                }
            }
        }
    }


    public void removeEdge(V from, V to) {
        if (from == null || to == null) return;
        Vertex<V> f = findVertex(from);
        if (f != null) {
            for (int i = 0; i < f.edges.size(); i++) {
                if (Objects.equals(f.edges.get(i).to, to)) {
                    f.edges.removeAt(i);
                    break;
                }
            }
        }

        if (!directed) {
            Vertex<V> t = findVertex(to);
            if (t != null) {
                for (int i = 0; i < t.edges.size(); i++) {
                    if (Objects.equals(t.edges.get(i).to, from)) {
                        t.edges.removeAt(i);
                        break;
                    }
                }
            }
        }
    }


    public List<V> getAdjacent(V v) {
        ArrayList<V> res = new ArrayList<>();
        if (v == null) return res;
        Vertex<V> vert = findVertex(v);
        if (vert == null) return res;
        for (int i = 0; i < vert.edges.size(); i++) res.add(vert.edges.get(i).to);
        return res;
    }


    private Vertex<V> findVertex(V value) {
        if (value == null) return null;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex<V> vv = vertices.get(i);
            if (Objects.equals(vv.value, value)) return vv;
        }
        return null;
    }


    public boolean canAddEdge(V from, V to) {
        if (from == null || to == null) return false;
        Vertex<V> f = findVertex(from);
        Vertex<V> t = findVertex(to);
        if (f == null || t == null) return false;

        // проверим в списках соседей
        for (int i = 0; i < f.edges.size(); i++) {
            if (Objects.equals(f.edges.get(i).to, to)) return false; // уже есть from->to
        }
        if (!directed) {
            for (int i = 0; i < t.edges.size(); i++) {
                if (Objects.equals(t.edges.get(i).to, from)) return false;
            }
        }
        return true;
    }


    public boolean containsEdge(V from, V to) {
        if (from == null || to == null) return false;
        Vertex<V> f = findVertex(from);
        if (f == null) return false;
        for (int i = 0; i < f.edges.size(); i++) if (Objects.equals(f.edges.get(i).to, to)) return true;
        return false;
    }


    public void printGraph(JTextArea out, boolean weighted) {
        if (out == null) return;
        String sep = directed ? " -> " : " - ";
        for (int i = 0; i < vertices.size(); i++) {
            Vertex<V> v = vertices.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(v.value).append(": ");
            for (int j = 0; j < v.edges.size(); j++) {
                sb.append(v.edges.get(j).to);
                if (weighted) {
                    sb.append(" (").append(v.edges.get(j).weight).append(")");
                }
                if (j + 1 < v.edges.size()) sb.append(", ");
            }
            out.append(sb.toString() + "\n");
        }
    }


    public void printGraph(JTextArea out) {
        printGraph(out, true);
    }


    public void dfs(V start, JTextArea out) {
        ArrayList<V> visited = new ArrayList<>();
        dfsRec(start, visited, out);
    }


    private void dfsRec(V v, ArrayList<V> visited, JTextArea out) {
        if (v == null) return;
        if (contains(visited, v)) return;
        if (out == null) System.out.println(v); else out.append(v + " ");
        visited.add(v);

        Vertex<V> vert = findVertex(v);
        if (vert == null) return;
        for (int i = 0; i < vert.edges.size(); i++) {
            dfsRec(vert.edges.get(i).to, visited, out);
        }
    }


    public void bfs(V start, JTextArea out) {
        ArrayList<V> visited = new ArrayList<>();
        Queue<V> q = new Queue<>();
        q.enqueue(start);
        while (!q.isEmpty()) {
            V cur = q.dequeue();
            if (cur == null) continue;
            if (contains(visited, cur)) continue;
            if (out == null) System.out.println(cur); else out.append(cur + " ");
            visited.add(cur);
            Vertex<V> vert = findVertex(cur);
            if (vert == null) continue;
            for (int i = 0; i < vert.edges.size(); i++) q.enqueue(vert.edges.get(i).to);
        }
    }


    private boolean contains(ArrayList<V> list, V value) {
        for (int i = 0; i < list.size(); i++) if (Objects.equals(list.get(i), value)) return true;
        return false;
    }


    public ArrayList<Vertex<V>> getVertices() {
        return vertices;
    }

    /**
     * Расстановка вершин по кругу (для визуализации).
     */
    public void layoutCircle(int centerX, int centerY, int radius) {
        int n = vertices.size();
        if (n == 0) return;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            Vertex<V> v = vertices.get(i);
            v.x = centerX + (int) (radius * Math.cos(angle));
            v.y = centerY + (int) (radius * Math.sin(angle));
        }
    }
}
