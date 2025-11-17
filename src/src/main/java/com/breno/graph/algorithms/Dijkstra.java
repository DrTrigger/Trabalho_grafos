package com.breno.graph.algorithms;

import com.breno.graph.Edge;
import com.breno.graph.Graph;
import com.breno.graph.Vertex;

import java.util.*;

/**
 * Single-source shortest paths (non-negative edges) using a priority queue.
 * Complexity: O((V + E) log V) with adjacency lists.
 */
public final class Dijkstra {
    private Dijkstra() {}

    public static final class Result<T> {
        public final Map<T, Float> dist;
        public final Map<T, T> parent;
        public Result(Map<T, Float> dist, Map<T, T> parent) {
            this.dist = dist;
            this.parent = parent;
        }
        public List<T> pathTo(T target) {
            if (!dist.containsKey(target) || Float.isInfinite(dist.get(target))) return List.of();
            LinkedList<T> path = new LinkedList<>();
            for (T at = target; at != null; at = parent.get(at)) {
                path.addFirst(at);
            }
            return path;
        }
    }

    public static <T> Result<T> shortestPaths(Graph<T> g, T source) {
        Map<T, Float> dist = new HashMap<>();
        Map<T, T> parent = new HashMap<>();
        for (Vertex<T> v : g.vertices()) {
            dist.put(v.getValue(), Float.POSITIVE_INFINITY);
        }
        if (!dist.containsKey(source)) {
            return new Result<>(dist, parent);
        }
        dist.put(source, 0f);

        Comparator<T> cmp = Comparator.comparing(dist::get);
        PriorityQueue<T> pq = new PriorityQueue<>(cmp);
        pq.add(source);

        Set<T> processed = new HashSet<>();

        while (!pq.isEmpty()) {
            T uVal = pq.poll();
            if (processed.contains(uVal)) continue;
            processed.add(uVal);

            Optional<Vertex<T>> uOpt = g.findVertex(uVal);
            if (uOpt.isEmpty()) continue;
            Vertex<T> u = uOpt.get();

            for (Edge<T> e : g.outgoing(u)) {
                T vVal = e.getTo().getValue();
                float w = e.getWeight();
                if (w < 0) throw new IllegalArgumentException("Dijkstra requires non-negative weights");
                float nd = dist.get(uVal) + w;
                if (nd < dist.get(vVal)) {
                    dist.put(vVal, nd);
                    parent.put(vVal, uVal);
                    pq.add(vVal);
                }
            }
        }

        return new Result<>(dist, parent);
    }
}
