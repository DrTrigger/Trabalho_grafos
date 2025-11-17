package com.breno.graph.io;

import com.breno.graph.Edge;
import com.breno.graph.Graph;
import com.breno.graph.Vertex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Saves a graph using the same format accepted by GraphLoader. */
public final class GraphSaver {

    private GraphSaver() {}

    public static void saveToFile(Graph<String> g, Path path) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            bw.write("DIRECTED " + g.isDirected());
            bw.newLine();

            // Emit vertices
            List<String> values = new ArrayList<>();
            for (Vertex<String> v : g.vertices()) values.add(v.getValue());
            Collections.sort(values);
            for (String v : values) {
                bw.write("V " + v);
                bw.newLine();
            }

            // Emit edges; avoid duplicates in undirected graphs
            Set<String> emitted = new HashSet<>();
            for (Vertex<String> v : g.vertices()) {
                for (Edge<String> e : g.outgoing(v)) {
                    String a = e.getFrom().getValue();
                    String b = e.getTo().getValue();
                    float w = e.getWeight();

                    String key;
                    if (g.isDirected()) {
                        key = a + "->" + b + "@" + w;
                    } else {
                        // undirected: store canonical pair
                        String x = a.compareTo(b) <= 0 ? a : b;
                        String y = a.compareTo(b) <= 0 ? b : a;
                        key = x + "--" + y + "@" + w;
                    }
                    if (emitted.add(key)) {
                        bw.write(String.format(Locale.ROOT, "E %s %s %.6f", a, b, w));
                        bw.newLine();
                    }
                }
            }
        }
    }
}
