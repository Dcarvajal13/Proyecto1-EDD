/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto;

/**
 * Representa un grafo dirigido implementado con una lista de adyacencia.
 * Los vértices son representados por enteros (índices) para mayor eficiencia.
 */

public class GrafoDirigido {
    
    /**
     * Constructor que inicializa el grafo con un número específico de vértices.
     * @param numVertices El número total de vértices que tendrá el grafo.
     */
    
    private final int numVertices;
    private final MiListaEnlazada<Integer>[] adyacencia;

    public GrafoDirigido(int numVertices) {
        this.numVertices = numVertices;
        adyacencia = new MiListaEnlazada[numVertices];
        for (int i = 0; i < numVertices; i++) {
            adyacencia[i] = new MiListaEnlazada<>();
        }
    }
    
    /**
     * Agrega una arista dirigida desde un vértice de origen a uno de destino.
     * @param origen El índice del vértice de origen.
     * @param destino El índice del vértice de destino.
     */

    public void agregarArista(int origen, int destino) {
        adyacencia[origen].agregarAlFinal(destino);
    }

    public int getNumVertices() { return numVertices; }
    public MiListaEnlazada<Integer> getAdyacentes(int vertice) { return adyacencia[vertice]; }
    
    /**
     * Retorna el grafo transpuesto (con todas las aristas invertidas).
     * Este método es un paso fundamental para el algoritmo de Kosaraju.
     * @return un nuevo GrafoDirigido que es el transpuesto del actual.
     */

    public GrafoDirigido getTranspuesto() {
        GrafoDirigido gTranspuesto = new GrafoDirigido(numVertices);
        for (int v = 0; v < numVertices; v++) {
            Nodo<Integer> actual = adyacencia[v].getCabeza();
            while (actual != null) {
                gTranspuesto.agregarArista(actual.getDato(), v);
                actual = actual.getSiguiente();
            }
        }
        return gTranspuesto;
    }
    
}
