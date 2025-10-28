/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
public class LogicaGrafo {
    
    private GrafoDirigido grafo;
    private String[] usuarios; // Arreglo para mapear indice <-> @usuario

    /**
     * Busca el índice numérico de un usuario a partir de su nombre.
     * Reemplaza la funcionalidad del HashMap.
     * @param nombreUsuario El nombre de usuario a buscar (ej. "@pepe").
     * @return El índice del usuario, o -1 si no se encuentra.
     */
    private int getIndicePorUsuario(String nombreUsuario) {
        if (usuarios == null) return -1;
        for (int i = 0; i < usuarios.length; i++) {
            if (usuarios[i] != null && usuarios[i].equals(nombreUsuario)) {
                return i;
            }
        }
        return -1; // Indica que el usuario no fue encontrado
    }

    /**
     * [cite_start]Carga los datos de usuarios y relaciones desde un archivo de texto [cite: 19] 
     * [cite_start]para construir el grafo[cite: 9].
     * @param archivo El archivo de texto plano a procesar.
     */
    public void cargarDesdeArchivo(File archivo) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String linea;
        boolean leyendoUsuarios = false;
        boolean leyendoRelaciones = false;

        MiListaEnlazada<String> listaUsuariosTemp = new MiListaEnlazada<>();
        MiListaEnlazada<String[]> listaRelacionesTemp = new MiListaEnlazada<>();

        // Fase 1: Leer todo el archivo y almacenar temporalmente
        while ((linea = reader.readLine()) != null) {
            linea = linea.trim();
            if (linea.equalsIgnoreCase("usuarios")) {
                leyendoUsuarios = true;
                leyendoRelaciones = false;
                continue;
            } else if (linea.equalsIgnoreCase("relaciones")) {
                leyendoUsuarios = false;
                leyendoRelaciones = true;
                continue;
            }

            if (leyendoUsuarios && !linea.isEmpty()) {
                listaUsuariosTemp.agregarAlFinal(linea);
            } else if (leyendoRelaciones && !linea.isEmpty()) {
                listaRelacionesTemp.agregarAlFinal(linea.split(",\\s*"));
            }
        }
        reader.close();

        // Fase 2: Inicializar las estructuras de datos del grafo
        int numUsuarios = listaUsuariosTemp.getTamano();
        this.usuarios = new String[numUsuarios];
        this.grafo = new GrafoDirigido(numUsuarios);

        // Fase 3: Poblar el arreglo de usuarios
        Nodo<String> nodoUsuarioActual = listaUsuariosTemp.getCabeza();
        for (int i = 0; i < numUsuarios; i++) {
            usuarios[i] = nodoUsuarioActual.getDato();
            nodoUsuarioActual = nodoUsuarioActual.getSiguiente();
        }
        
        // Fase 4: Poblar el grafo con las relaciones (aristas)
        Nodo<String[]> nodoRelacionActual = listaRelacionesTemp.getCabeza();
        while(nodoRelacionActual != null) {
            String[] relacion = nodoRelacionActual.getDato();
            if (relacion.length == 2) {
                int origen = getIndicePorUsuario(relacion[0]);
                int destino = getIndicePorUsuario(relacion[1]);
                if (origen != -1 && destino != -1) {
                     grafo.agregarArista(origen, destino);
                }
            }
            nodoRelacionActual = nodoRelacionActual.getSiguiente();
        }
    }

    
    // 1er DFS para llenar la pila con el orden de finalización
    private void llenarOrden(int v, boolean[] visitado, MiPila<Integer> pila) {
        visitado[v] = true;
        Nodo<Integer> adyacente = grafo.getAdyacentes(v).getCabeza();
        while (adyacente != null) {
            if (!visitado[adyacente.getDato()]) {
                llenarOrden(adyacente.getDato(), visitado, pila);
            }
            adyacente = adyacente.getSiguiente();
        }
        pila.apilar(v);
    }
    
    // 2do DFS en el grafo transpuesto para encontrar un CFC
    private void DFSUtil(int v, boolean[] visitado, GrafoDirigido gTranspuesto, MiListaEnlazada<Integer> componente) {
        visitado[v] = true;
        componente.agregarAlFinal(v);
        Nodo<Integer> adyacente = gTranspuesto.getAdyacentes(v).getCabeza();
        while (adyacente != null) {
            if (!visitado[adyacente.getDato()]) {
                DFSUtil(adyacente.getDato(), visitado, gTranspuesto, componente);
            }
            adyacente = adyacente.getSiguiente();
        }
    }

    /**
     * Encuentra y devuelve todos los componentes fuertemente conectados del grafo.
     * @return Una lista de listas, donde cada lista interna es un componente.
     */
    public MiListaEnlazada<MiListaEnlazada<Integer>> encontrarCFC() {
        MiPila<Integer> pila = new MiPila<>();
        int numVertices = grafo.getNumVertices();
        boolean[] visitado = new boolean[numVertices];

        for (int i = 0; i < numVertices; i++) {
            if (!visitado[i]) {
                llenarOrden(i, visitado, pila);
            }
        }

        GrafoDirigido gTranspuesto = grafo.getTranspuesto();
        
        for (int i = 0; i < numVertices; i++) {
            visitado[i] = false;
        }

        MiListaEnlazada<MiListaEnlazada<Integer>> componentes = new MiListaEnlazada<>();
        while (!pila.estaVacia()) {
            int v = pila.desapilar();
            if (!visitado[v]) {
                MiListaEnlazada<Integer> nuevoComponente = new MiListaEnlazada<>();
                DFSUtil(v, visitado, gTranspuesto, nuevoComponente);
                componentes.agregarAlFinal(nuevoComponente);
            }
        }
        return componentes;
    }
    
    // --- Getters para la Interfaz Gráfica ---
    public GrafoDirigido getGrafo() { return grafo; }
    public String[] getUsuarios() { return usuarios; }
    
}
