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
    public int getIndicePorUsuario(String nombreUsuario) {
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
    
   /**
 * Agrega un nuevo usuario al grafo y establece una relación de seguimiento.
 * @param nuevoUsuario El nombre del nuevo usuario (ej. "@derek").
 * @param usuarioASeguir El nombre del usuario existente al que seguirá.
 */
public void agregarUsuario(String nuevoUsuario, String usuarioASeguir) {
    if (getIndicePorUsuario(nuevoUsuario) != -1) {
        System.out.println("Error: El usuario " + nuevoUsuario + " ya existe.");
        return;
    }

    int numVerticesActual = (this.grafo == null) ? 0 : this.grafo.getNumVertices();
    int nuevoNumVertices = numVerticesActual + 1;

    String[] nuevosUsuarios = new String[nuevoNumVertices];
    if (this.usuarios != null) {
        for (int i = 0; i < numVerticesActual; i++) {
            nuevosUsuarios[i] = this.usuarios[i];
        }
    }
    nuevosUsuarios[numVerticesActual] = nuevoUsuario;
    this.usuarios = nuevosUsuarios;

    GrafoDirigido nuevoGrafo = new GrafoDirigido(nuevoNumVertices);
    if (this.grafo != null) {
        for (int i = 0; i < numVerticesActual; i++) {
            Nodo<Integer> adyacente = this.grafo.getAdyacentes(i).getCabeza();
            while (adyacente != null) {
                nuevoGrafo.agregarArista(i, adyacente.getDato());
                adyacente = adyacente.getSiguiente();
            }
        }
    }

    if (usuarioASeguir != null && !usuarioASeguir.isEmpty()) {
        int indiceNuevoUsuario = nuevoNumVertices - 1;
        int indiceUsuarioASeguir = getIndicePorUsuario(usuarioASeguir);

        if (indiceUsuarioASeguir != -1) {
            nuevoGrafo.agregarArista(indiceNuevoUsuario, indiceUsuarioASeguir);
        }
    }

    this.grafo = nuevoGrafo;
} 

/**
 * Elimina un usuario del grafo, junto con todas sus conexiones entrantes y salientes.
 * @param usuarioAEliminar El nombre del usuario a eliminar (ej. "@pepe").
 */
public void eliminarUsuario(String usuarioAEliminar) {
    int indiceAEliminar = getIndicePorUsuario(usuarioAEliminar);
    if (indiceAEliminar == -1) {
        System.out.println("Error: El usuario " + usuarioAEliminar + " no existe.");
        return;
    }

    int numVerticesActual = this.grafo.getNumVertices();
    int nuevoNumVertices = numVerticesActual - 1;

    // 1. Crear un nuevo arreglo de usuarios más pequeño
    String[] nuevosUsuarios = new String[nuevoNumVertices];
    int nuevoIndice = 0;
    for (int i = 0; i < numVerticesActual; i++) {
        if (i != indiceAEliminar) {
            nuevosUsuarios[nuevoIndice] = this.usuarios[i];
            nuevoIndice++;
        }
    }
    this.usuarios = nuevosUsuarios;

    // 2. Crear un nuevo grafo más pequeño y copiar las aristas relevantes
    GrafoDirigido nuevoGrafo = new GrafoDirigido(nuevoNumVertices);
    for (int i = 0; i < numVerticesActual; i++) {
        // Ignorar las aristas que salen del usuario eliminado
        if (i == indiceAEliminar) {
            continue;
        }

        // Determinar el nuevo índice del vértice de origen
        int nuevoIndiceOrigen = (i < indiceAEliminar) ? i : i - 1;

        Nodo<Integer> adyacente = this.grafo.getAdyacentes(i).getCabeza();
        while (adyacente != null) {
            int indiceDestino = adyacente.getDato();
            // Ignorar las aristas que apuntan al usuario eliminado
            if (indiceDestino != indiceAEliminar) {
                // Determinar el nuevo índice del vértice de destino
                int nuevoIndiceDestino = (indiceDestino < indiceAEliminar) ? indiceDestino : indiceDestino - 1;
                nuevoGrafo.agregarArista(nuevoIndiceOrigen, nuevoIndiceDestino);
            }
            adyacente = adyacente.getSiguiente();
        }
    }
    this.grafo = nuevoGrafo;
}
    
}
