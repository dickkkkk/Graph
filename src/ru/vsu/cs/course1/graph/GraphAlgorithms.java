package ru.vsu.cs.course1.graph;

import java.util.*;
import java.util.function.Consumer;

public class GraphAlgorithms {

    /**
     * Поиск в глубину, реализованный рекурсивно
     * (начальная вершина также включена)
     *
     * @param graph граф
     * @param from  Вершина, с которой начинается поиск
     */
    public static void dfsRecursion(Graph graph, int from, int second) {
        boolean[] visited = new boolean[graph.vertexCount()];

        class Inner {
            void visit(Integer curr) {
                visited[curr] = true;
                boolean log = false;
                for (Integer v : graph.adjacencies(curr)) {
                    if (v != second) {
                        if (!visited[v] && log==false) {
                            System.out.println(v);
                            visit(v);
                        }
                    }else
                        log = true;
                }
            }
        }
        new Inner().visit(from);
    }

    /**
     * Поиск в глубину, реализованный с помощью стека
     * (не совсем "правильный"/классический, т.к. "в глубину" реализуется только "план" обхода, а не сам обход)
     *
     * @param graph   граф
     */
    public static void dfs(Graph graph, int v, boolean[] visited, boolean[] result, int needV) {
        visited[v] = true;
        if (v != needV){
            for (int i : graph.adjacencies(v)){
                if (!visited[i]){
                    dfs(graph, i, visited,result,needV);
                }
            }
        }else{
            for (int i = 0; i< visited.length; i++){
                if (!visited[i]){
                    result[i] = false;
                }
            }
        }
        visited[v] = false;
    }

    public static void solution(Graph graph, int v1, int v2){
        boolean[] visited = new boolean[graph.vertexCount()];
        boolean[] result = new boolean[graph.vertexCount()];
        Arrays.fill(result,true);
        dfs(graph,v1,visited,result,v2);
        for (int i = 0; i < result.length; i++){
            System.out.println(i+ "->" + result[i]);
        }
        System.out.println();
    }

    /**
     * Поиск в ширину, реализованный с помощью очереди
     * (начальная вершина также включена)
     *
     * @param graph   граф
     * @param from    Вершина, с которой начинается поиск
     * @param visitor Посетитель
     */
    public static void bfs(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(from);
        visited[from] = true;
        while (queue.size() > 0) {
            Integer curr = queue.remove();
            visitor.accept(curr);
            for (Integer v : graph.adjacencies(curr)) {
                if (!visited[v]) {
                    queue.add(v);
                    visited[v] = true;
                }
            }
        }
    }

    /**
     * Поиск в глубину в виде итератора
     * (начальная вершина также включена)
     *
     * @param graph граф
     * @param from  Вершина, с которой начинается поиск
     * @return Итератор
     */
    public static Iterable<Integer> dfs(Graph graph, int from) {
        return new Iterable<Integer>() {
            private Stack<Integer> stack = null;
            private boolean[] visited = null;

            @Override
            public Iterator<Integer> iterator() {
                stack = new Stack<>();
                stack.push(from);
                visited = new boolean[graph.vertexCount()];
                visited[from] = true;

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return !stack.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        Integer result = stack.pop();
                        for (Integer adj : graph.adjacencies(result)) {
                            if (!visited[adj]) {
                                visited[adj] = true;
                                stack.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    /**
     * Поиск в ширину в виде итератора
     * (начальная вершина также включена)
     *
     * @param from Вершина, с которой начинается поиск
     * @return Итератор
     */
    public static Iterable<Integer> bfs(Graph graph, int from) {
        return new Iterable<Integer>() {
            private Queue<Integer> queue = null;
            private boolean[] visited = null;

            @Override
            public Iterator<Integer> iterator() {
                queue = new LinkedList<>();
                queue.add(from);
                visited = new boolean[graph.vertexCount()];
                visited[from] = true;

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return !queue.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        Integer result = queue.remove();
                        for (Integer adj : graph.adjacencies(result)) {
                            if (!visited[adj]) {
                                visited[adj] = true;
                                queue.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }


    /**
     * Алгоритм Дейкстры
     * (простейшая реализация без приоритетной очереди за n^2)
     */
    public static class MinDistanceSearchResult {
        public double d[];
        public int from[];
    }

    public static MinDistanceSearchResult dijkstra(WeightedGraph graph, int source, int target) {
        int n = graph.vertexCount();

        double[] d = new double[n];
        int[] from = new int[n];
        boolean[] found = new boolean[n];

        Arrays.fill(d, Double.MAX_VALUE);
        d[source] = 0;
        int prev = -1;
        for (int i = 0; i < n; i++) {
            // ищем "ненайденную" вершину с минимальным d[i]
            // (в общем случае обращение к приоритетной очереди)
            int curr = -1;
            for (int j = 0; j < n; j++) {
                if (!found[j] && (curr < 0 || d[j] < d[curr])) {
                    curr = j;
                }
            }

            found[curr] = true;
            from[curr] = prev;
            if (curr == target) {
                break;
            }
            for (WeightedGraph.WeightedEdgeTo v : graph.adjacenciesWithWeights(curr)) {
                if (d[curr] + v.weight() < d[v.to()]) {
                    d[v.to()] = d[curr] + v.weight();
                    // в общем случае надо было изменить в приоритетной очереди приоритет для v.to()
                }
            }
        }

        // возвращение результата
        MinDistanceSearchResult result = new MinDistanceSearchResult();
        result.d = d;
        result.from = from;
        return result;
    }

    /**
     * Алгоритм Белмана-Форда
     * O(n*m)
     */
    public static MinDistanceSearchResult belmanFord(WeightedGraph graph, int source) {
        int n = graph.vertexCount();

        double[] d = new double[n];
        int[] from = new int[n];

        Arrays.fill(d, Double.MAX_VALUE);
        d[source] = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean changed = false;
            // обход всех связей (в данной реализации - цикл в цикле)
            for (int j = 0; j < n; j++) {
                for (WeightedGraph.WeightedEdgeTo v : graph.adjacenciesWithWeights(j)) {
                    if (d[v.to()] > d[j] + v.weight()) {
                        d[v.to()] = d[j] + v.weight();
                        from[v.to()] = j;
                        changed = true;
                    }
                }
            }
            if (!changed) {
                break;
            }
        }

        // возвращение результата
        MinDistanceSearchResult result = new MinDistanceSearchResult();
        result.d = d;
        result.from = from;
        return result;
    }
}
