/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto;

public class Nodo <T>{
    private T dato;
    private Nodo<T> siguiente;
    
    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    // --- Getters y Setters ---
    public T getDato() { return dato; }
    public void setDato(T dato) { this.dato = dato; }
    public Nodo<T> getSiguiente() { return siguiente; }
    public void setSiguiente(Nodo<T> siguiente) { this.siguiente = siguiente; }
}
    
   

