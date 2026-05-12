package br.com.unipe;

import java.util.*;

public class Grafo {
    private final List<Aresta> arestas;
    private final List<Vertice> vertices;
    private boolean eDirigido;
    private int ordem;
    private int tamanho;

    public Grafo() {
        this(false);
    }

    public Grafo(boolean eDirigido) {
        this.eDirigido = eDirigido;
        arestas = new ArrayList<>();
        vertices = new ArrayList<>();
    }

    public void adicionaVertices(String... nomes) {
        for (String nome : nomes) {
            vertices.add(new Vertice(nome));
            ordem++;
        }
    }

    public void addAresta(String nomeVertice1, String nomeVertice2) {
        arestas.add(criaAresta("", nomeVertice1, nomeVertice2));
    }

    public void addAresta(String nomeAresta, String nomeVertice1, String nomeVertice2) {
        arestas.add(criaAresta(nomeAresta, nomeVertice1, nomeVertice2));
    }

    private Aresta criaAresta(String nomeAresta, String nomeVertice1, String nomeVertice2) {
        Vertice v1 = encontraVertice(nomeVertice1).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice1 + " não encontrado."));
        Vertice v2 = encontraVertice(nomeVertice2).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice2 + " não encontrado."));
        if (!eDirigido) {
            infereSeGrafoEDirecionado(v1, v2);
        }
        aumentaGrauDosVertices(v1, v2);
        resolveAdjacencias(v1, v2);
        tamanho++;
        return nomeAresta.isEmpty() ? new Aresta(v1, v2) : new Aresta(nomeAresta, v1, v2);
    }

    private void resolveAdjacencias(Vertice v1, Vertice v2) {
        v1.adicionaAdjacencia(v2); //v1 envia p v2
        v2.adicionaAdjacente(v1); // v2 recebe de v1
        if (!eDirigido) {
            v1.adicionaAdjacente(v2);
            v2.adicionaAdjacencia(v1);
        }
    }

    private void aumentaGrauDosVertices(Vertice v1, Vertice v2) {
        if (eDirigido) {
            v1.aumentaOutDegree();
            v2.aumentaInDegree();
        } else {
            v1.aumentaGrau();
            v2.aumentaGrau();
        }
    }

    private void infereSeGrafoEDirecionado(Vertice v1, Vertice v2) {
        if (eSelfLoop(v1, v2)) {
            reprocessamentoParaDigrafo();
        } else {
            for (Aresta aresta : arestas) {
                if (eViaMaoDupla(v1, v2, aresta) || eArestaDuplicada(v2, v1, aresta)) {
                    reprocessamentoParaDigrafo();
                    break;
                }
            }
        }
    }

    private static boolean eArestaDuplicada(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v1) && aresta.getVerticeDestino().equals(v2);
    }

    private static boolean eViaMaoDupla(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v2) && aresta.getVerticeDestino().equals(v1);
    }

    private static boolean eSelfLoop(Vertice v1, Vertice v2) {
        return v1.getNome().equals(v2.getNome());
    }

    public Optional<Vertice> encontraVertice(String nome) {
        for (Vertice vertice : vertices) {
            if (vertice.getNome().equalsIgnoreCase(nome)) {
                return Optional.of(vertice);
            }
        }
        return Optional.empty();
    }

    private void reprocessamentoParaDigrafo() {
        eDirigido = true;
        System.out.println("Reprocessamento para digrafo necessário. O grafo agora é direcionado.");
        limpezaGrausEAdjacencias();
        recalculaGrausEAdjacencias();
    }

    private void recalculaGrausEAdjacencias() {
        arestas.forEach(aresta -> {
            Vertice origem = aresta.getVerticeOrigem();
            Vertice destino = aresta.getVerticeDestino();
            aumentaGrauDosVertices(origem, destino);
            resolveAdjacencias(origem, destino);
        });
    }

    private void limpezaGrausEAdjacencias() {
        vertices.forEach(vertice -> {
            vertice.resetaGraus();
            vertice.resetaAdjacenciasEAdjacentes();
        });
    }

    public String exibeGrausDosVertices() {
        StringBuilder graus = new StringBuilder();
        for (Vertice vertice : vertices) {
            graus.append(vertice.exibeGraus());
        }
        return graus.toString();
    }

    public String exibeAdjacencias() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias
                    .append("\n")
                    .append(vertice.getNome())
                    .append(": ")
                    .append(vertice.getAdjacencias());
        }
        return adjacencias.toString();
    }

    public String exibeAdjacentes() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias
                    .append("\n")
                    .append(vertice.getNome())
                    .append(": ")
                    .append(vertice.getAdjacentes());
        }
        return adjacencias.toString();
    }


    public void exibeMatrizAdjacencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();

        StringBuilder matriz = new StringBuilder("\nMatriz de Adjacência\n\t");
        verticesOrdenados.forEach(v -> matriz.append(v.getNome()).append("\t"));
        matriz.append("\n");

        for (Vertice vertice : verticesOrdenados) {
            matriz.append(vertice.getNome()).append("\t");
            List<Vertice> adjacencias = vertice.getAdjacencias();
            for (Vertice outroVertice : verticesOrdenados) {
                matriz.append(adjacencias.contains(outroVertice) ? "1" : "0").append("\t");
            }
            matriz.append("\n");
        }

        System.out.println(matriz);
    }

    public void exibeMatrizIncidencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();
        StringBuilder matriz = new StringBuilder("\nMatriz de Incidência\n\t");
        arestas.forEach(a -> matriz.append(a.getNome()).append("\t"));
        matriz.append("\n");
        for (Vertice vertice : verticesOrdenados) {
            matriz.append(vertice.getNome()).append("\t");
            for (Aresta aresta : arestas) {
                Vertice origem = aresta.getVerticeOrigem();
                Vertice destino = aresta.getVerticeDestino();
                if (origem.equals(vertice) && destino.equals(vertice)) {
                    matriz.append(" 2").append("\t");
                } else if (origem.equals(vertice)) {
                    matriz.append(eDirigido ? "-1" : "1").append("\t");
                } else if (destino.equals(vertice)) {
                    matriz.append(" 1").append("\t");
                } else {
                    matriz.append(" 0\t");
                }
            }
            matriz.append("\n");
        }
        System.out.println(matriz);
    }

    public boolean existeCaminho(String nomeOrigem, String nomeDestino) {
        Vertice origem = encontraVertice(nomeOrigem).orElseThrow(
                () -> new IllegalArgumentException("Vértice " + nomeOrigem + " não encontrado."));
        Vertice destino = encontraVertice(nomeDestino).orElseThrow(
                () -> new IllegalArgumentException("Vértice " + nomeDestino + " não encontrado."));

        if (origem.equals(destino)) return true;

        Set<Vertice> visitados = new HashSet<>();
        Queue<Vertice> fila = new LinkedList<>();
        fila.add(origem);
        visitados.add(origem);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();
            for (Vertice vizinho : atual.getAdjacencias()) {
                if (vizinho.equals(destino)) return true;
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    fila.add(vizinho);
                }
            }
        }
        return false;
    }

    public int comprimentoCaminho(String nomeOrigem, String nomeDestino) {
        Vertice origem = encontraVertice(nomeOrigem).orElseThrow(
                () -> new IllegalArgumentException("Vértice " + nomeOrigem + " não encontrado."));
        Vertice destino = encontraVertice(nomeDestino).orElseThrow(
                () -> new IllegalArgumentException("Vértice " + nomeDestino + " não encontrado."));

        if (origem.equals(destino)) return 0;

        Set<Vertice> visitados = new HashSet<>();
        Queue<Vertice> fila = new LinkedList<>();
        Map<Vertice, Integer> distancia = new HashMap<>();

        fila.add(origem);
        visitados.add(origem);
        distancia.put(origem, 0);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();
            for (Vertice vizinho : atual.getAdjacencias()) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    distancia.put(vizinho, distancia.get(atual) + 1);
                    if (vizinho.equals(destino)) return distancia.get(vizinho);
                    fila.add(vizinho);
                }
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return """
                direcionado = %s,
                ordem = %d,
                tamanho = %d,
                vertices = %s,
                arestas = %s,
                graus = %s,
                adjacencias = %s,
                adjacentes = %s
                }""".formatted(
                eDirigido ? "sim" : "não",
                ordem,
                tamanho,
                vertices,
                arestas,
                exibeGrausDosVertices(),
                exibeAdjacencias(),
                exibeAdjacentes());
    }
}

