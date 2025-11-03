/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyecto;

// --- IMPORTACIONES NECESARIAS ---
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

// --- IMPORTACIONES DE GRAPHSTREAM ---
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

/**
 * Clase principal que gestiona la interfaz gráfica (GUI) de la aplicación.
 * Construye la ventana, los paneles y los botones, y maneja las interacciones
 * del usuario, delegando la lógica de negocio a la clase LogicaGrafo.
 */

public class VentanaPrincipal extends javax.swing.JFrame {
    
    /**
     * Constructor que inicializa todos los componentes de la interfaz gráfica,
     * establece sus propiedades y asigna los listeners a los botones.
     */
 
    private final LogicaGrafo logica;
   
    private Graph graphStream;
    
    
    public VentanaPrincipal() {
        
        // 1. Inicializar la lógica del negocio
        this.logica = new LogicaGrafo();

        // 2. Configurar la ventana principal (el JFrame)
        setTitle("Analizador de Redes Sociales");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Usar BorderLayout

        // 3. Crear el panel de controles para los botones (IZQUIERDA)
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen

        JButton btnCargarArchivo = new JButton("Cargar Archivo");
        JButton btnIdentificarCFC = new JButton("Identificar Componentes");
        JButton btnAgregarUsuario = new JButton("Agregar Usuario");
        JButton btnEliminarUsuario = new JButton("Eliminar Usuario");
        JButton btnGuardarCambios = new JButton("Guardar Cambios");

        // Añadir botones al panel de controles
        panelControles.add(btnCargarArchivo);
        panelControles.add(Box.createRigidArea(new Dimension(0, 10)));
        panelControles.add(btnIdentificarCFC);
        panelControles.add(Box.createRigidArea(new Dimension(0, 10)));
        panelControles.add(btnAgregarUsuario);
        panelControles.add(Box.createRigidArea(new Dimension(0, 10)));
        panelControles.add(btnEliminarUsuario);
        panelControles.add(Box.createRigidArea(new Dimension(0, 10)));
        panelControles.add(btnGuardarCambios);

        // 4. Crear el panel donde irá el grafo (CENTRO)
        panelGrafo = new JPanel(new BorderLayout());
        panelGrafo.setBackground(java.awt.Color.DARK_GRAY); // Color oscuro para que sea visible

        // 5. Añadir los paneles a la ventana
        add(panelControles, BorderLayout.WEST);
        add(panelGrafo, BorderLayout.CENTER);

        // 6. Añadir las acciones a los botones (Listeners)
        btnCargarArchivo.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccione el archivo de texto del grafo");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt"));

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                try {
                    logica.cargarDesdeArchivo(archivo);
                    mostrarGrafo();
                    JOptionPane.showMessageDialog(this, "Grafo cargado exitosamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnIdentificarCFC.addActionListener((ActionEvent e) -> {
            if (graphStream == null) {
                JOptionPane.showMessageDialog(this, "Primero debe cargar un grafo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MiListaEnlazada<MiListaEnlazada<Integer>> componentes = logica.encontrarCFC();
            colorearGrafo(componentes);
        });
        
        btnAgregarUsuario.addActionListener((ActionEvent e) -> {
            if (logica.getGrafo() == null) {
                JOptionPane.showMessageDialog(this, "Primero debe cargar un grafo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nuevoUsuario = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo usuario (ej. @nuevo):");
            if (nuevoUsuario == null || nuevoUsuario.trim().isEmpty() || !nuevoUsuario.startsWith("@")) {
                JOptionPane.showMessageDialog(this, "Nombre de usuario inválido. Debe empezar con '@' y no estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (logica.getIndicePorUsuario(nuevoUsuario.trim()) != -1) {
                JOptionPane.showMessageDialog(this, "El usuario '" + nuevoUsuario + "' ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object[] opciones = {"Seguir a un usuario existente", "No seguir a nadie"};
            int seleccion = JOptionPane.showOptionDialog(this, "¿El nuevo usuario seguirá a alguien?", "Nueva Relación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            String usuarioASeguir = "";
            if (seleccion == JOptionPane.YES_OPTION) {
                String[] usuariosExistentes = logica.getUsuarios();
                usuarioASeguir = (String) JOptionPane.showInputDialog(
                    this, "Seleccione a qué usuario seguirá:", "Seleccionar Usuario",
                    JOptionPane.QUESTION_MESSAGE, null, usuariosExistentes, usuariosExistentes[0]);
                if (usuarioASeguir == null) {
                    return;
                }
            }

            logica.agregarUsuario(nuevoUsuario.trim(), usuarioASeguir);
            mostrarGrafo();
            JOptionPane.showMessageDialog(this, "Usuario '" + nuevoUsuario + "' agregado exitosamente.");
        });
        
        // Lógica para el nuevo botón "Eliminar Usuario"
        btnEliminarUsuario.addActionListener((ActionEvent e) -> {
            if (logica.getGrafo() == null || logica.getGrafo().getNumVertices() == 0) {
                JOptionPane.showMessageDialog(this, "No hay usuarios para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Mostrar una lista de usuarios existentes para que elija
            String[] usuariosExistentes = logica.getUsuarios();
            String usuarioSeleccionado = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el usuario a eliminar:",
                    "Eliminar Usuario",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    usuariosExistentes,
                    usuariosExistentes[0]);

            // Si el usuario cancela, no hacemos nada
            if (usuarioSeleccionado == null) {
                return;
            }

            // 2. Confirmación antes de borrar
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea eliminar a '" + usuarioSeleccionado + "'? Esta acción es permanente.",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                // 3. Llamar a la lógica y actualizar la vista
                logica.eliminarUsuario(usuarioSeleccionado);
                mostrarGrafo(); // Redibujar el grafo sin el usuario
                JOptionPane.showMessageDialog(this, "Usuario '" + usuarioSeleccionado + "' eliminado exitosamente.");
            }
        });
        
        // Lógica para el nuevo botón "Guardar Cambios"
        btnGuardarCambios.addActionListener((ActionEvent e) -> {
            if (logica.getGrafo() == null) {
                JOptionPane.showMessageDialog(this, "No hay ningún grafo cargado para guardar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar grafo como archivo de texto");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt"));

            // Abrir el diálogo en modo "Guardar"
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivoParaGuardar = fileChooser.getSelectedFile();

                // Asegurarse de que el archivo tenga la extensión .txt
                if (!archivoParaGuardar.getName().toLowerCase().endsWith(".txt")) {
                    archivoParaGuardar = new File(archivoParaGuardar.getAbsolutePath() + ".txt");
                }

                try {
                    // 3. Llamar a la lógica para escribir en el archivo
                    logica.guardarEnArchivo(archivoParaGuardar);
                    JOptionPane.showMessageDialog(this, "Grafo guardado exitosamente en:\n" + archivoParaGuardar.getName(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 7. Ajustar el tamaño final y hacer visible
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrar en pantalla
    
    }
    
    private void mostrarGrafo() {
        panelGrafo.removeAll();

        System.setProperty("org.graphstream.ui", "swing");
        graphStream = new SingleGraph("RedSocial");

        String[] usuarios = logica.getUsuarios();
        GrafoDirigido grafoDirigido = logica.getGrafo();

        graphStream.setAttribute("ui.stylesheet", "node { text-size: 14; fill-color: white; } edge { arrow-size: 10px, 6px; }");

        for (int i = 0; i < usuarios.length; i++) {
            Node node = graphStream.addNode(String.valueOf(i));
            node.setAttribute("ui.label", usuarios[i]);
        }

        for (int i = 0; i < grafoDirigido.getNumVertices(); i++) {
            Nodo<Integer> adyacente = grafoDirigido.getAdyacentes(i).getCabeza();
            while (adyacente != null) {
                graphStream.addEdge(i + "->" + adyacente.getDato(), String.valueOf(i), String.valueOf(adyacente.getDato()), true);
                adyacente = adyacente.getSiguiente();
            }
        }

        Viewer viewer = new SwingViewer(graphStream, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);

        panelGrafo.add(viewPanel, BorderLayout.CENTER);
        
        panelGrafo.revalidate();
        panelGrafo.repaint();
    }
    
    private void colorearGrafo(MiListaEnlazada<MiListaEnlazada<Integer>> componentes) {
        // ... (Este método no necesita cambios)
        String[] colores = {"red", "cyan", "green", "orange", "purple", "yellow", "pink", "blue", "magenta"};
        int colorIndex = 0;
        
        Nodo<MiListaEnlazada<Integer>> componenteActual = componentes.getCabeza();
        while (componenteActual != null) {
            String color = colores[colorIndex % colores.length];
            Nodo<Integer> nodoDelComponente = componenteActual.getDato().getCabeza();
            while(nodoDelComponente != null) {
                Node node = graphStream.getNode(String.valueOf(nodoDelComponente.getDato()));
                if(node != null) {
                     node.setAttribute("ui.style", "fill-color: " + color + ";");
                }
                nodoDelComponente = nodoDelComponente.getSiguiente();
            }
            componenteActual = componenteActual.getSiguiente();
            colorIndex++;
        }
    }
    
    /**
     * Método principal que inicia la aplicación, creando y mostrando la ventana.
     * @param args Argumentos de la línea de comandos (no se utilizan).
     */
    
    public static void main(String[] args) {
        // Asegura que la GUI se cree y se muestre en el hilo correcto
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
    // </editor-fold>

     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelControles = new javax.swing.JPanel();
        panelGrafo = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout panelControlesLayout = new javax.swing.GroupLayout(panelControles);
        panelControles.setLayout(panelControlesLayout);
        panelControlesLayout.setHorizontalGroup(
            panelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );
        panelControlesLayout.setVerticalGroup(
            panelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
        );

        getContentPane().add(panelControles, java.awt.BorderLayout.WEST);

        panelGrafo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panelGrafo.setPreferredSize(new java.awt.Dimension(600, 500));

        javax.swing.GroupLayout panelGrafoLayout = new javax.swing.GroupLayout(panelGrafo);
        panelGrafo.setLayout(panelGrafoLayout);
        panelGrafoLayout.setHorizontalGroup(
            panelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelGrafoLayout.setVerticalGroup(
            panelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
        );

        getContentPane().add(panelGrafo, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
    
    /**
     * @param args the command line arguments
     */
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelControles;
    private javax.swing.JPanel panelGrafo;
    // End of variables declaration//GEN-END:variables
}
