/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto;

/**
 * Implementación de una lista enlazada simple desde cero. Se utiliza como base
 * para la lista de adyacencia del grafo.
 * @param <T> El tipo de dato que almacenará la lista.
 */
public class MiListaEnlazada <T>{
    
    private Nodo<T> cabeza;
    private int tamano;

    public MiListaEnlazada() {
        this.cabeza = null;
        this.tamano = 0;
    }
    
    /**
     * Revisa si la lista no contiene elementos.
     * @return true si la lista está vacía, false en caso contrario.
     */

    public boolean estaVacia() { return cabeza == null; }
    public int getTamano() { return tamano; }
    public Nodo<T> getCabeza() { return cabeza; }
    
    /**
     * Agrega un nuevo elemento al final de la lista.
     * @param dato El dato a agregar.
     */

    public void agregarAlFinal(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (estaVacia()) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamano++;
    }
    
}
