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


public class VentanaPrincipal extends javax.swing.JFrame {

 
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

        // Añadir botones al panel de controles
        panelControles.add(btnCargarArchivo);
        panelControles.add(Box.createRigidArea(new Dimension(0, 10))); // Un espacio vertical
        panelControles.add(btnIdentificarCFC);

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
        btnCargarArchivo = new javax.swing.JButton();
        btnIdentificarCFC = new javax.swing.JButton();
        panelGrafo = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnCargarArchivo.setText("Cargar Archivo");
        btnCargarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarArchivoActionPerformed(evt);
            }
        });

        btnIdentificarCFC.setText("Identificar Componentes");
        btnIdentificarCFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdentificarCFCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelControlesLayout = new javax.swing.GroupLayout(panelControles);
        panelControles.setLayout(panelControlesLayout);
        panelControlesLayout.setHorizontalGroup(
            panelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlesLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(btnCargarArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(121, 121, 121)
                .addComponent(btnIdentificarCFC, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );
        panelControlesLayout.setVerticalGroup(
            panelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlesLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(panelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCargarArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnIdentificarCFC, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(279, Short.MAX_VALUE))
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

    
    private void btnCargarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarArchivoActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione el archivo de texto del grafo");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                logica.cargarDesdeArchivo(archivo);
                mostrarGrafo(); // Llamamos al método para dibujar
                JOptionPane.showMessageDialog(this, "Grafo cargado exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Útil para ver el error completo en la consola
            }
        }       
        
       
    
    }//GEN-LAST:event_btnCargarArchivoActionPerformed

    private void btnIdentificarCFCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdentificarCFCActionPerformed
        //pRIMERO REVISA SI YA SE CARGO UN GRAFO
        if (logica.getGrafo() == null) {
            JOptionPane.showMessageDialog(this, "Primero debe cargar un grafo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return; // NO HACE NADA MAS SI NO HAY GRAFO
        }
        // LLAMA A LA LOGICA QUE EJECUTA EL ALGORITMO DE KOSARAJU
        MiListaEnlazada<MiListaEnlazada<Integer>> componentes = logica.encontrarCFC();
        // LLAMA AL METODO QUE USA EL RESULTA PARA PINTAR LOS NODOS
        colorearGrafo(componentes);
        
        JOptionPane.showMessageDialog(this, "Se identificaron los componentes fuertemente conectados.");
    }//GEN-LAST:event_btnIdentificarCFCActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCargarArchivo;
    private javax.swing.JButton btnIdentificarCFC;
    private javax.swing.JPanel panelControles;
    private javax.swing.JPanel panelGrafo;
    // End of variables declaration//GEN-END:variables
}
