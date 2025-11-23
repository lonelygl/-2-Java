package app;

import graph.Graph;
import algorithms.GraphAlgorithms;
import javax.swing.*;
import java.awt.*;

/*
  GUI для работы с графом:
 позволяет добавлять вершины и рёбра, запускать алгоритмы и видеть результаты
 */
public class Main {
    private JFrame frame;
    private JTextArea outputArea;

    private JTextField vertexField, sourceField, targetField, weightField;

    private JComboBox<String> fromCombo, toCombo; // выбираем вершины для ребра
    private JComboBox<String> orientationCombo;  // ориентированный / нет
    private JComboBox<String> weightedCombo;     // взвешенный / нет

    private Graph<String> graph;

    public Main() {
        graph = new Graph<>(true);
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Graph GUI");
        frame.setBounds(100, 100, 900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));
        frame.add(panel, BorderLayout.NORTH);

        // Тип графа
        JPanel typePanel = new JPanel();
        typePanel.add(new JLabel("Тип графа:"));

        orientationCombo = new JComboBox<>(new String[]{"Ориентированный", "Неориентированный"});
        typePanel.add(orientationCombo);

        weightedCombo = new JComboBox<>(new String[]{"Взвешенный", "Невзвешенный"});
        typePanel.add(weightedCombo);

        JButton newGraphBtn = new JButton("Создать новый граф");
        typePanel.add(newGraphBtn);

        panel.add(typePanel);

        // Вершины
        JPanel vertexPanel = new JPanel();
        vertexPanel.add(new JLabel("Вершина:"));
        vertexField = new JTextField(5);
        vertexPanel.add(vertexField);

        JButton addVertexBtn = new JButton("Добавить вершину");
        JButton removeVertexBtn = new JButton("Удалить вершину");
        vertexPanel.add(addVertexBtn);
        vertexPanel.add(removeVertexBtn);

        panel.add(vertexPanel);

        // Рёбра через выпадающие списки
        JPanel edgePanel = new JPanel();

        edgePanel.add(new JLabel("От:"));
        fromCombo = new JComboBox<>();
        fromCombo.setPreferredSize(new Dimension(80, 24));
        edgePanel.add(fromCombo);

        edgePanel.add(new JLabel("До:"));
        toCombo = new JComboBox<>();
        toCombo.setPreferredSize(new Dimension(80, 24));
        edgePanel.add(toCombo);

        edgePanel.add(new JLabel("Вес:"));
        weightField = new JTextField(3);
        edgePanel.add(weightField);

        JButton addEdgeBtn = new JButton("Добавить ребро");
        JButton removeEdgeBtn = new JButton("Удалить ребро");
        edgePanel.add(addEdgeBtn);
        edgePanel.add(removeEdgeBtn);

        panel.add(edgePanel);

        // Алгоритмы
        JPanel algoPanel = new JPanel();
        algoPanel.add(new JLabel("Источник:"));
        sourceField = new JTextField(4);
        algoPanel.add(sourceField);

        algoPanel.add(new JLabel("Цель:"));
        targetField = new JTextField(4);
        algoPanel.add(targetField);

        JButton dfsBtn = new JButton("DFS");
        JButton bfsBtn = new JButton("BFS");
        JButton dijkstraBtn = new JButton("Dijkstra");
        JButton kosarajuBtn = new JButton("Kosaraju");
        JButton topoBtn = new JButton("TopSort");
        JButton printBtn = new JButton("Печать графа");

        algoPanel.add(dfsBtn);
        algoPanel.add(bfsBtn);
        algoPanel.add(dijkstraBtn);
        algoPanel.add(kosarajuBtn);
        algoPanel.add(topoBtn);
        algoPanel.add(printBtn);

        panel.add(algoPanel);


        JPanel clearPanel = new JPanel();
        JButton clearScreenBtn = new JButton("Очистить экран");
        clearPanel.add(clearScreenBtn);
        panel.add(clearPanel);

        //  Поле вывода
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);


        newGraphBtn.addActionListener(e -> {
            boolean oriented = orientationCombo.getSelectedItem().equals("Ориентированный");
            graph = new Graph<>(oriented);
            fromCombo.removeAllItems();
            toCombo.removeAllItems();
            outputArea.append("Создан новый " + (oriented ? "ориентированный" : "неориентированный") + " граф.\n");
        });

        addVertexBtn.addActionListener(e -> {
            String v = vertexField.getText().trim();
            if (v.isEmpty()) return;
            graph.addVertex(v);
            fromCombo.addItem(v);
            toCombo.addItem(v);
            outputArea.append("Добавлена вершина: " + v + "\n");
        });

        removeVertexBtn.addActionListener(e -> {
            String v = vertexField.getText().trim();
            if (v.isEmpty()) return;

            graph.removeVertex(v);
            fromCombo.removeItem(v);
            toCombo.removeItem(v);

            outputArea.append("Удалена вершина: " + v + "\n");
        });

        addEdgeBtn.addActionListener(e -> {
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();

            if (from == null || to == null) {
                outputArea.append("Ошибка: нет доступных вершин.\n");
                return;
            }

            boolean isWeighted = weightedCombo.getSelectedItem().equals("Взвешенный");
            int weight = isWeighted ? parseWeight() : 1;

            if (!graph.canAddEdge(from, to)) {
                outputArea.append("Ошибка: такое ребро уже есть.\n");
                return;
            }

            graph.addEdge(from, to, weight);
            outputArea.append("Добавлено ребро: " +
                    (graph.isOriented() ? from + " -> " + to : from + " - " + to) +
                    (isWeighted ? " (" + weight + ")" : "") + "\n");
        });

        removeEdgeBtn.addActionListener(e -> {
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();
            graph.removeEdge(from, to);
            outputArea.append("Удалено ребро: " + from + " - " + to + "\n");
        });

        dfsBtn.addActionListener(e -> runAlgorithm("DFS", () -> graph.dfs(sourceField.getText(), outputArea)));
        bfsBtn.addActionListener(e -> runAlgorithm("BFS", () -> graph.bfs(sourceField.getText(), outputArea)));
        dijkstraBtn.addActionListener(e -> runAlgorithm("Dijkstra",
                () -> GraphAlgorithms.dijkstra(graph, sourceField.getText(), targetField.getText(), outputArea)
        ));
        kosarajuBtn.addActionListener(e -> runAlgorithm("SCC",
                () -> GraphAlgorithms.findSCC(graph, outputArea)
        ));
        topoBtn.addActionListener(e -> runAlgorithm("TopSort",
                () -> GraphAlgorithms.topologicalSort(graph, outputArea)
        ));

        printBtn.addActionListener(e -> {
            outputArea.append("Структура графа:\n");
            graph.printGraph(outputArea,
                    weightedCombo.getSelectedItem().equals("Взвешенный")
            );
            outputArea.append("\n");
        });

        clearScreenBtn.addActionListener(e -> outputArea.setText(""));

        frame.setVisible(true);
    }


    private int parseWeight() {
        try { return Integer.parseInt(weightField.getText().trim()); }
        catch (Exception e) { return 1; }
    }

    
    private void runAlgorithm(String name, Runnable action) {
        outputArea.append(name + ":\n");
        action.run();
        outputArea.append("\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
