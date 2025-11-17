package com.breno.graph.algorithms;

import com.breno.graph.Edge;
import com.breno.graph.Graph;
import com.breno.graph.Vertex;

import java.util.*;

/**
 * Kahn's algorithm for topological ordering of a DAG.
 * Complexity: O(V + E).
 */
public final class TopologicalSort {
    private TopologicalSort() {}

    public static <T> List<T> sort(Graph<T> g) {
        if (!g.isDirected()) {
            throw new IllegalArgumentException("Topological sort requires a directed graph.");
        }

        Map<Vertex<T>, Integer> indeg = new HashMap<>();
        for (Vertex<T> v : g.vertices()) indeg.put(v, 0);
        for (Vertex<T> v : g.vertices()) {
            for (Edge<T> e : g.outgoing(v)) {
                indeg.put(e.getTo(), indeg.get(e.getTo()) + 1);
            }
        }

        Deque<Vertex<T>> q = new ArrayDeque<>();
        for (Map.Entry<Vertex<T>, Integer> en : indeg.entrySet()) {
            if (en.getValue() == 0) q.add(en.getKey());
        }

        List<T> order = new ArrayList<>();
        int visited = 0;
        while (!q.isEmpty()) {
            Vertex<T> v = q.removeFirst();
            order.add(v.getValue());
            visited++;
            for (Edge<T> e : g.outgoing(v)) {
                Vertex<T> u = e.getTo();
                indeg.put(u, indeg.get(u) - 1);
                if (indeg.get(u) == 0) q.addLast(u);
            }
        }

        if (visited != indeg.size()) {
            throw new IllegalStateException("Graph has at least one cycle; topological order doesn't exist.");
        }
        return order;
    }
}
