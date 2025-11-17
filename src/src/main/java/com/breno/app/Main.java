package com.breno.app;

import com.breno.graph.Graph;
import com.breno.graph.algorithms.Dijkstra;
import com.breno.graph.algorithms.TopologicalSort;
import com.breno.graph.io.GraphLoader;
import com.breno.graph.io.GraphSaver;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static Graph<String> g = new Graph<>(false); // default: undirected

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        sc.useLocale(Locale.ROOT);

        if (args.length == 0) {
            System.out.println("== Modo interativo (sem arquivo inicial) ==");
            boolean directed = askYesNo("Deseja grafo dirigido? (s/n): ");
            g = new Graph<>(directed);
        } else {
            Path input = Path.of(args[0]);
            try {
                g = GraphLoader.loadFromFile(input);
                System.out.println("Grafo carregado de " + input.toString());
            } catch (IOException e) {
                System.err.println("Falha ao ler arquivo: " + e.getMessage());
                System.out.println("Prosseguindo com grafo vazio (não dirigido por padrão).");
                g = new Graph<>(false);
            }
        }

        loop();
    }

    private static void loop() {
        while (true) {
            System.out.println();
            System.out.println("== Menu ==");
            System.out.println("1) Mostrar grafo (lista de adjacência)");
            System.out.println("2) Adicionar vértice");
            System.out.println("3) Adicionar aresta");
            System.out.println("4) BFS (caminhamento em largura)");
            System.out.println("5) Dijkstra (caminho mínimo)");
            System.out.println("6) Ordenação topológica (DAG dirigido)");
            System.out.println("7) Salvar grafo em arquivo");
            System.out.println("8) Carregar grafo de arquivo");
            System.out.println("9) Novo grafo (limpar; escolher dirigido/não)");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();
            try {
                switch (op) {
                    case "1" -> showGraph();
                    case "2" -> addVertex();
                    case "3" -> addEdge();
                    case "4" -> runBfs();
                    case "5" -> runDijkstra();
                    case "6" -> runTopo();
                    case "7" -> saveGraph();
                    case "8" -> loadGraph();
                    case "9" -> newGraph();
                    case "0" -> { System.out.println("Encerrando."); return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception ex) {
                System.out.println("Falha: " + ex.getMessage());
            }
        }
    }

    private static void showGraph() {
        System.out.println();
        System.out.println("Grafo (" + (g.isDirected() ? "dirigido" : "não dirigido") + "):");
        System.out.println(g.toString());
    }

    private static void addVertex() {
        System.out.print("Digite o valor do vértice (string): ");
        String v = sc.nextLine().trim();
        boolean added = g.addVertex(v);
        if (added) System.out.println("Vértice adicionado: " + v);
        else System.out.println("Já existia: " + v);
    }

    private static void addEdge() {
        System.out.print("Origem: ");
        String from = sc.nextLine().trim();
        System.out.print("Destino: ");
        String to = sc.nextLine().trim();
        float w = askFloat("Peso (float): ");
        g.addEdge(from, to, w);
        System.out.println("Aresta adicionada" + (g.isDirected() ? "" : " (dupla, pois não dirigido)") + ".");
    }

    private static void runBfs() {
        System.out.print("Vértice inicial: ");
        String s = sc.nextLine().trim();
        List<String> ordem = g.bfs(s);
        if (ordem.isEmpty()) {
            System.out.println("Vértice inexistente ou grafo vazio.");
        } else {
            System.out.println("Ordem BFS: " + String.join(" -> ", ordem));
        }
    }

    private static void runDijkstra() {
        System.out.print("Origem: ");
        String src = sc.nextLine().trim();
        Dijkstra.Result<String> res = Dijkstra.shortestPaths(g, src);
        System.out.print("Destino: ");
        String dst = sc.nextLine().trim();
        List<String> path = res.pathTo(dst);
        if (path.isEmpty() || !res.dist.containsKey(dst) || Float.isInfinite(res.dist.getOrDefault(dst, Float.POSITIVE_INFINITY))) {
            System.out.println("Sem caminho ou origem/destino inexistente.");
        } else {
            System.out.printf(Locale.ROOT, "Distância total: %.3f%n", res.dist.get(dst));
            System.out.println("Caminho: " + String.join(" -> ", path));
        }
    }

    private static void runTopo() {
        try {
            List<String> order = TopologicalSort.sort(g);
            System.out.println("Topológica: " + String.join(" -> ", order));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    private static void saveGraph() {
        System.out.print("Caminho do arquivo para salvar: ");
        String p = sc.nextLine().trim();
        try {
            GraphSaver.saveToFile(g, Path.of(p));
            System.out.println("Salvo em: " + p);
        } catch (IOException e) {
            System.out.println("Falha ao salvar: " + e.getMessage());
        }
    }

    private static void loadGraph() {
        System.out.print("Caminho do arquivo para carregar: ");
        String p = sc.nextLine().trim();
        try {
            g = GraphLoader.loadFromFile(Path.of(p));
            System.out.println("Grafo carregado.");
        } catch (IOException e) {
            System.out.println("Falha ao carregar: " + e.getMessage());
        }
    }

    private static void newGraph() {
        boolean directed = askYesNo("Novo grafo dirigido? (s/n): ");
        g = new Graph<>(directed);
        System.out.println("Novo grafo criado: " + (directed ? "dirigido" : "não dirigido"));
    }

    private static boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim().toLowerCase(Locale.ROOT);
            if (s.equals("s") || s.equals("sim") || s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("nao") || s.equals("não") || s.equals("no")) return false;
            System.out.println("Responda com s/n.");
        }
    }

    private static float askFloat(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String s = sc.nextLine().trim();
                return Float.parseFloat(s);
            } catch (NumberFormatException ex) {
                System.out.println("Valor inválido; tente de novo.");
            }
        }
    }
}
