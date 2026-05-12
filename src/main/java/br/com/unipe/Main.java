package br.com.unipe;

public class Main {
    public static void main(String[] args) {
        Grafo grafo = new Grafo(); //não dirigido
        grafo.adicionaVertices("1", "2", "3", "4", "5", "6");
        grafo.addAresta("a1", "1", "3");
        grafo.addAresta("a2", "1", "4");
        ;
        grafo.addAresta("a3", "2", "5");
        grafo.addAresta("a4", "3", "5");
        grafo.addAresta("a5", "4", "4");

        System.out.println(grafo);
        grafo.exibeMatrizAdjacencia();
        grafo.exibeMatrizIncidencia();

System.out.println("\nExiste caminho de 1 a 5? " + grafo.existeCaminho("1", "5"));
System.out.println("Existe caminho de 2 a 4? " + grafo.existeCaminho("2", "4"));
System.out.println("Comprimento de 1 a 5: " + grafo.comprimentoCaminho("1", "5"));
System.out.println("Comprimento de 2 a 4: " + grafo.comprimentoCaminho("2", "4"));
    }
}
