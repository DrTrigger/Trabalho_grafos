package com.breno.graph;

/**
 * Directed edge from 'from' to 'to' with float weight.
 * For undirected graphs, Graph.addEdge creates two directed edges internally.
 */
public final class Edge<T> {
    private final Vertex<T> from;
    private final Vertex<T> to;
    private final float weight;

    public Edge(Vertex<T> from, Vertex<T> to, float weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Vertex<T> getFrom() { return from; }
    public Vertex<T> getTo() { return to; }
    public float getWeight() { return weight; }

    @Override
    public String toString() {
        return from + " --" + weight + "--> " + to;
    }
}
