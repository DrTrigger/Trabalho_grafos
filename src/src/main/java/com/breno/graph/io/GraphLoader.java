package com.breno.graph.io;

import com.breno.graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple graph file parser.
 *
 * Format (UTF-8, one directive per line, '#' comments allowed):
 *   DIRECTED true|false
 *   V <value>
 *   E <from> <to> <weight>
 *
 * Example:
 *   DIRECTED false
 *   V A
 *   V B
 *   V C
 *   E A B 1.5
 *   E B C 2.0
 */
public final class GraphLoader {

    private GraphLoader() {}

    public static Graph<String> loadFromFile(Path path) throws IOException {
        boolean directed = false;
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String firstNonComment = null;
            br.mark(8192);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                firstNonComment = line;
                break;
            }
            if (firstNonComment != null && firstNonComment.toUpperCase().startsWith("DIRECTED")) {
                // ok, we will reread file from start to parse all lines
            } else {
                // no DIRECTED header found; default false
            }
        }

        Graph<String> g = new Graph<>(directed);
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\s+");
                if (parts.length == 0) continue;
                String cmd = parts[0].toUpperCase();
                switch (cmd) {
                    case "DIRECTED" -> {
                        if (parts.length < 2) throw new IOException("DIRECTED expects true|false");
                        boolean dir = Boolean.parseBoolean(parts[1]);
                        // If graph already has edges/vertices, this would be too late.
                        // For simplicity of this assignment, we expect DIRECTED before any V/E.
                        g = new Graph<>(dir);
                    }
                    case "V" -> {
                        if (parts.length < 2) throw new IOException("V expects a value");
                        g.addVertex(parts[1]);
                    }
                    case "E" -> {
                        if (parts.length < 4) throw new IOException("E expects: from to weight");
                        String from = parts[1];
                        String to = parts[2];
                        float w = Float.parseFloat(parts[3]);
                        g.addEdge(from, to, w);
                    }
                    default -> throw new IOException("Unknown directive: " + cmd);
                }
            }
        }
        return g;
    }
}
