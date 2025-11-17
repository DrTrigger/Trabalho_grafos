package com.breno.graph;

import java.util.Objects;

/**
 * Generic vertex wrapper that stores a value of type T.
 * Equality and hash code are based solely on the wrapped value.
 */
public final class Vertex<T> {
    private final T value;

    public Vertex(T value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return Objects.equals(value, vertex.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
