/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto;

/**
 * Implementando una pila (Stack) usando una lista enlazada (LIFO) 
 * @author User
 * @param <T> EL tipo de dato que almacenara la pila
 */
public class MiPila<T> {
    
    private Nodo<T> cima;
    private int tamano;

    public MiPila() {
        this.cima = null;
        this.tamano = 0;
    }

    public boolean estaVacia() { return cima == null; }
    public int getTamano() { return tamano; }

    public void apilar(T dato) { // push
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (!estaVacia()) {
            nuevoNodo.setSiguiente(cima);
        }
        cima = nuevoNodo;
        tamano++;
    }

    public T desapilar() { // pop
        if (estaVacia()) { return null; }
        T dato = cima.getDato();
        cima = cima.getSiguiente();
        tamano--;
        return dato;
    }
    
}
