package com.breno.graph;

import java.util.*;
import java.util.function.Function;

/**
 * Adjacency-list based generic graph (directed or undirected).
 * Stores unique vertices keyed by their value.
 */
public class Graph<T> {
    private final boolean directed;
    private final Map<T, Vertex<T>> vertexByValue = new HashMap<>();
    private final Map<Vertex<T>, List<Edge<T>>> adj = new HashMap<>();

    public Graph(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    /** Adds a vertex if absent. Returns true if it was added. */
    public boolean addVertex(T value) {
        if (vertexByValue.containsKey(value)) return false;
        Vertex<T> v = new Vertex<>(value);
        vertexByValue.put(value, v);
        adj.put(v, new ArrayList<>());
        return true;
    }

    /** Get (and optionally create) vertex for value. */
    private Vertex<T> requireVertex(T value) {
        Vertex<T> v = vertexByValue.get(value);
        if (v == null) {
            addVertex(value);
            v = vertexByValue.get(value);
        }
        return v;
    }

    /** Adds an edge; creates missing vertices automatically. */
    public void addEdge(T from, T to, float weight) {
        Vertex<T> vf = requireVertex(from);
        Vertex<T> vt = requireVertex(to);
        addArc(vf, vt, weight);
        if (!directed) addArc(vt, vf, weight);
    }

    private void addArc(Vertex<T> from, Vertex<T> to, float w) {
        adj.get(from).add(new Edge<>(from, to, w));
    }

    public Set<Vertex<T>> vertices() {
        return Collections.unmodifiableSet(adj.keySet());
    }

    public List<Edge<T>> outgoing(Vertex<T> v) {
        return Collections.unmodifiableList(adj.getOrDefault(v, List.of()));
    }

    public Optional<Vertex<T>> findVertex(T value) {
        return Optional.ofNullable(vertexByValue.get(value));
    }

    public List<T> bfs(T startValue) {
        Vertex<T> start = vertexByValue.get(startValue);
        if (start == null) return List.of();

        List<T> order = new ArrayList<>();
        Set<Vertex<T>> visited = new HashSet<>();
        Deque<Vertex<T>> q = new ArrayDeque<>();
        visited.add(start);
        q.add(start);

        while (!q.isEmpty()) {
            Vertex<T> v = q.removeFirst();
            order.add(v.getValue());
            for (Edge<T> e : adj.getOrDefault(v, List.of())) {
                Vertex<T> u = e.getTo();
                if (!visited.contains(u)) {
                    visited.add(u);
                    q.addLast(u);
                }
            }
        }
        return order;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex<T> v : vertices()) {
            sb.append(v).append(" : ");
            List<Edge<T>> es = adj.get(v);
            if (es == null || es.isEmpty()) {
                sb.append("∅");
            } else {
                for (int i = 0; i < es.size(); i++) {
                    Edge<T> e = es.get(i);
                    sb.append(e.getTo()).append("(").append(e.getWeight()).append(")");
                    if (i + 1 < es.size()) sb.append(", ");
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
