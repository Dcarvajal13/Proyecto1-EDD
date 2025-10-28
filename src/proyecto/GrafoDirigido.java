/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto;

/**
 * Representa un grafo dirigido implementado con una lista de adyacencia.
 */

public class GrafoDirigido {
    
    private final int numVertices;
    private final MiListaEnlazada<Integer>[] adyacencia;

    public GrafoDirigido(int numVertices) {
        this.numVertices = numVertices;
        adyacencia = new MiListaEnlazada[numVertices];
        for (int i = 0; i < numVertices; i++) {
            adyacencia[i] = new MiListaEnlazada<>();
        }
    }

    public void agregarArista(int origen, int destino) {
        adyacencia[origen].agregarAlFinal(destino);
    }

    public int getNumVertices() { return numVertices; }
    public MiListaEnlazada<Integer> getAdyacentes(int vertice) { return adyacencia[vertice]; }

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
