package com.breno.graph.algorithms;

import com.breno.graph.Edge;
import com.breno.graph.Graph;
import com.breno.graph.Vertex;

import java.util.*;

/**
 * Simple DFS-based cycle detection.
 * For directed graphs, uses colors (0=unvisited,1=visiting,2=done).
 * For undirected graphs, checks back-edges ignoring the parent.
 */
public final class CycleDetector {
    private CycleDetector() {}

    public static <T> boolean hasCycleDirected(Graph<T> g) {
        if (!g.isDirected()) throw new IllegalArgumentException("Expected directed graph");
        Map<Vertex<T>, Integer> color = new HashMap<>();
        for (Vertex<T> v : g.vertices()) color.put(v, 0);
        for (Vertex<T> v : g.vertices()) {
            if (color.get(v) == 0 && dfsDirected(g, v, color)) return true;
        }
        return false;
    }

    private static <T> boolean dfsDirected(Graph<T> g, Vertex<T> v, Map<Vertex<T>, Integer> color) {
        color.put(v, 1);
        for (Edge<T> e : g.outgoing(v)) {
            Vertex<T> u = e.getTo();
            int c = color.getOrDefault(u, 0);
            if (c == 1) return true;        // back-edge
            if (c == 0 && dfsDirected(g, u, color)) return true;
        }
        color.put(v, 2);
        return false;
    }

    public static <T> boolean hasCycleUndirected(Graph<T> g) {
        if (g.isDirected()) throw new IllegalArgumentException("Expected undirected graph");
        Set<Vertex<T>> vis = new HashSet<>();
        for (Vertex<T> v : g.vertices()) {
            if (!vis.contains(v) && dfsUndirected(g, v, null, vis)) return true;
        }
        return false;
    }

    private static <T> boolean dfsUndirected(Graph<T> g, Vertex<T> v, Vertex<T> parent, Set<Vertex<T>> vis) {
        vis.add(v);
        for (Edge<T> e : g.outgoing(v)) {
            Vertex<T> u = e.getTo();
            if (u.equals(parent)) continue;
            if (vis.contains(u)) return true;
            if (dfsUndirected(g, u, v, vis)) return true;
        }
        return false;
    }
}
