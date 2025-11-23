package algorithms;

import graph.Graph;
import graph.Vertex;
import ds.ArrayList;
import util.Stack;
import javax.swing.JTextArea;

/**
 * Алгоритмы для графа:
 * - Dijkstra (с индексной функцией для ArrayList)
 * - Kosaraju (SCC)
 * - Topological Sort
 */
public class GraphAlgorithms {

    public static void dijkstra(Graph<String> g, String source, String target, JTextArea out) {
        ArrayList<Vertex<String>> verts = g.getVertices();
        int n = verts.size();
        if (n == 0) {
            if (out != null) out.append("Граф пуст\n");
            return;
        }

        ArrayList<String> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) nodes.add(verts.get(i).value);

        final int INF = Integer.MAX_VALUE / 4;
        int[] dist = new int[n];
        String[] prev = new String[n];
        boolean[] used = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = INF;
            prev[i] = null;
            used[i] = false;
        }

        int srcIdx = indexOf(nodes, source);
        if (srcIdx == -1) {
            if (out != null) out.append("Источник не найден\n");
            return;
        }
        dist[srcIdx] = 0;

        for (int iter = 0; iter < n; iter++) {
            int v = -1;
            for (int i = 0; i < n; i++) if (!used[i] && (v == -1 || dist[i] < dist[v])) v = i;
            if (v == -1 || dist[v] == INF) break;
            used[v] = true;

            Vertex<String> vert = verts.get(v);
            for (int i = 0; i < vert.edges.size(); i++) {
                String to = (String) vert.edges.get(i).to;
                int w = vert.edges.get(i).weight;
                int toIdx = indexOf(nodes, to);
                if (toIdx == -1) continue;
                if (dist[v] + w < dist[toIdx]) {
                    dist[toIdx] = dist[v] + w;
                    prev[toIdx] = vert.value;
                }
            }
        }

        int tgtIdx = indexOf(nodes, target);
        if (tgtIdx == -1) { if (out != null) out.append("Цель не найдена\n"); return; }
        if (dist[tgtIdx] >= INF) { if (out != null) out.append("Пути нет\n"); return; }

        // восстановление пути
        ArrayList<String> path = new ArrayList<>();
        for (String at = target; at != null;) {
            path.add(at);
            int idx = indexOf(nodes, at);
            at = (idx == -1) ? null : prev[idx];
        }

        for (int i = path.size() - 1; i >= 0; i--) {
            if (out != null) out.append(path.get(i) + (i > 0 ? " -> " : "\n"));
            else System.out.print(path.get(i) + (i > 0 ? " -> " : "\n"));
        }

        if (out != null) out.append("Distance: " + dist[tgtIdx] + "\n");
    }

    // вспомогательный метод для поиска индекса в ArrayList
    private static int indexOf(ArrayList<String> list, String val) {
        for (int i = 0; i < list.size(); i++) if (list.get(i).equals(val)) return i;
        return -1;
    }


    public static void findSCC(Graph<String> g, JTextArea out) {
        ArrayList<Vertex<String>> verts = g.getVertices();
        ArrayList<String> visited = new ArrayList<>();
        Stack<String> order = new Stack<>();

        // формируем порядок вершин
        for (int i = 0; i < verts.size(); i++) {
            String v = verts.get(i).value;
            if (!contains(visited, v)) iterativeDFS(v, g, visited, order);
        }

        // поиск компонент на транспонированном графе
        ArrayList<String> compVisited = new ArrayList<>();
        while (order.size() > 0) {
            String v = order.pop();
            if (contains(compVisited, v)) continue;
            ArrayList<String> component = new ArrayList<>();
            iterativeDFSTranspose(v, g, compVisited, component);

            StringBuilder sb = new StringBuilder("{ ");
            for (int i = 0; i < component.size(); i++)
                sb.append(component.get(i)).append(i + 1 < component.size() ? ", " : " ");
            sb.append("}\n");

            if (out != null) out.append(sb.toString()); else System.out.print(sb.toString());
        }
    }

    private static void iterativeDFS(String start, Graph<String> g, ArrayList<String> visited, Stack<String> order) {
        Stack<String> stack = new Stack<>();
        stack.push(start);

        while (stack.size() > 0) {
            String v = stack.peek();
            if (!contains(visited, v)) visited.add(v);

            boolean pushed = false;
            ArrayList<String> adj = (ArrayList<String>) g.getAdjacent(v);
            for (int i = 0; i < adj.size(); i++) {
                String u = adj.get(i);
                if (!contains(visited, u)) {
                    stack.push(u);
                    pushed = true;
                    break;
                }
            }

            if (!pushed) {
                stack.pop();
                order.push(v);
            }
        }
    }

    private static void iterativeDFSTranspose(String start, Graph<String> g, ArrayList<String> visited, ArrayList<String> component) {
        Stack<String> stack = new Stack<>();
        stack.push(start);

        while (stack.size() > 0) {
            String v = stack.pop();
            if (contains(visited, v)) continue;
            visited.add(v);
            component.add(v);

            ArrayList<Vertex<String>> verts = g.getVertices();
            for (int i = 0; i < verts.size(); i++) {
                Vertex<String> vv = verts.get(i);
                for (int j = 0; j < vv.edges.size(); j++) {
                    if (vv.edges.get(j).to.equals(v)) stack.push(vv.value);
                }
            }
        }
    }


    public static void topologicalSort(Graph<String> g, JTextArea out) {
        ArrayList<Vertex<String>> verts = g.getVertices();
        ArrayList<String> visited = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (int i = 0; i < verts.size(); i++) {
            String v = verts.get(i).value;
            if (!contains(visited, v)) iterativeTopSortDFS(v, g, visited, stack);
        }

        while (stack.size() > 0) {
            String v = stack.pop();
            if (out != null) out.append(v + (stack.size() > 0 ? " -> " : "\n"));
            else System.out.print(v + (stack.size() > 0 ? " -> " : "\n"));
        }
    }

    private static void iterativeTopSortDFS(String start, Graph<String> g, ArrayList<String> visited, Stack<String> stack) {
        Stack<String> s = new Stack<>();
        Stack<String> tempStack = new Stack<>();
        s.push(start);

        while (s.size() > 0) {
            String v = s.pop();
            if (!contains(visited, v)) {
                visited.add(v);
                tempStack.push(v);
                ArrayList<String> adj = (ArrayList<String>) g.getAdjacent(v);
                for (int i = 0; i < adj.size(); i++)
                    if (!contains(visited, adj.get(i))) s.push(adj.get(i));
            }
        }

        while (tempStack.size() > 0) stack.push(tempStack.pop());
    }

    private static boolean contains(ArrayList<String> list, String val) {
        for (int i = 0; i < list.size(); i++) if (list.get(i).equals(val)) return true;
        return false;
    }
}
