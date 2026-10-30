package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import conexion.Conexion;
import controlador.Controlador;
import modelo.Historial;
import modelo.Workouts;

public class PanelWorkouts extends JFrame {

    private static final long serialVersionUID = 1L;

    // Colores y fuentes comunes
    private static final Color COLOR_PRIMARIO = new Color(30, 144, 255);
    private static final Color COLOR_OSCURO = new Color(25, 25, 25);
    private static final Color COLOR_GRIS_BARRA = new Color(235, 238, 242);
    private static final Color COLOR_BOTON_SIDEBAR = new Color(220, 225, 230);
    private static final Color COLOR_FILA_ALT = new Color(240, 245, 255);
    private static final Font FUENTE_TABLA = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_CABECERA = new Font("Segoe UI", Font.BOLD, 13);

    private static final String[] NOMBRES_NIVEL = { "Principiante", "Intermedio", "Avanzado" };

    private JPanel contentPane;
    private JTable tablaWorkouts;
    private JTable tablaEjers;
    private JComboBox<Object> nivel;
    private DocumentSnapshot usu;
    private List<Workouts> todosLosWorkouts = new ArrayList<>();
    private int nivelActual; // nivel del usuario
    private List<Workouts> workoutsVisibles = new ArrayList<>();
    private DefaultTableModel modeloWorkouts;
    private DefaultTableModel modeloEjers;
    private JScrollPane scrollWorkouts;
    private JScrollPane scrollEjers;

    static Controlador controlador = new Controlador();

    public PanelWorkouts(DocumentSnapshot docUsu, List<Workouts> workoutsDisp) {
        this.usu = docUsu;
        this.nivelActual = controlador.obtenerNivelUsuario(docUsu);

        if (workoutsDisp != null) {
            this.todosLosWorkouts = workoutsDisp;
        }
        try {
            Firestore db = Conexion.conectar();
            List<Workouts> cargados = controlador.cargarWorkoutsDesdeFirestore(db);
            if (cargados != null) {
                this.todosLosWorkouts = cargados;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();
    }

    private void initComponents() {
        setTitle("Elogym - Workouts");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1130, 771);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setBackground(new Color(255, 255, 255));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // ==========================================
        // 🔹 BARRA DE NAVEGACIÓN LATERAL (SIDEBAR)
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setBounds(0, 0, 230, 734);
        sidebar.setBackground(COLOR_GRIS_BARRA);
        sidebar.setLayout(null);
        contentPane.add(sidebar);

        // Logo dentro de la barra
        JLabel lblLogo = new JLabel("");
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBounds(45, 30, 140, 140);

        URL urlLogo = PanelWorkouts.class.getResource("/img/EloGymLogo.png");
        ImageIcon icon = (urlLogo != null) ? new ImageIcon(urlLogo) : new ImageIcon("img/EloGymLogo.png");

        if (icon.getImage() != null) {
            Image imgEscalada = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(imgEscalada));
        }
        sidebar.add(lblLogo);

        // Botón "Mi Perfil" en la barra
        JButton btnPerfilSide = crearBotonSidebar("Mi Perfil", COLOR_BOTON_SIDEBAR, Color.BLACK);
        btnPerfilSide.setBounds(20, 200, 190, 42);
        btnPerfilSide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usu == null) {
                    JOptionPane.showMessageDialog(PanelWorkouts.this,
                            "No se pudo cargar la información del usuario.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                PanelUsuario principal = new PanelUsuario(usu, todosLosWorkouts);
                principal.setVisible(true);
                dispose();
            }
        });
        sidebar.add(btnPerfilSide);

        // Botón "Historial" en la barra
        JButton btnHistorialSide = crearBotonSidebar("Historial Workouts", COLOR_BOTON_SIDEBAR, Color.BLACK);
        btnHistorialSide.setBounds(20, 255, 190, 42);
        btnHistorialSide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usu == null) {
                    return;
                }
                List<Historial> historialRealizado = controlador.obtenerHistorialWorkouts(usu.getId());
                PanelHistorialWorkouts historial = new PanelHistorialWorkouts(usu, historialRealizado, todosLosWorkouts);
                historial.setVisible(true);
            }
        });
        sidebar.add(btnHistorialSide);

        // ==========================================
        // 🔹 CONTENIDO PRINCIPAL (DERECHA)
        // ==========================================

        // Título principal
        JLabel lblTitulo = new JLabel("WORKOUTS");
        lblTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        lblTitulo.setForeground(new Color(0, 0, 0));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setBounds(260, 30, 400, 50);
        contentPane.add(lblTitulo);

        // Selector de Nivel
        JPanel panelNivel = new JPanel();
        panelNivel.setBackground(new Color(255, 255, 255, 230));
        panelNivel.setBounds(252, 91, 263, 36);
        contentPane.add(panelNivel);

        JLabel lblNivel = new JLabel("Seleccione Nivel:");
        lblNivel.setFont(FUENTE_CABECERA);
        panelNivel.add(lblNivel);

        nivel = new JComboBox<>();
        nivel.setFont(FUENTE_TABLA);
        panelNivel.add(nivel);

        rellenarNiveles();
        // Se abre mostrando TODOS los niveles (los bloqueados salen marcados con 🔒).
        // Si prefieres que se abra en el nivel del usuario, usa: nivel.setSelectedItem(nivelActual);
        nivel.setSelectedIndex(0);

        nivel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object seleccionado = nivel.getSelectedItem();
                if (seleccionado == null) {
                    return;
                }
                int nivelSeleccionado = (seleccionado instanceof Integer) ? (Integer) seleccionado : -1;
                modeloWorkouts.setRowCount(0);
                modeloEjers.setRowCount(0);
                workoutsVisibles = controlador.mostrarWorkoutsPorNivel(
                        nivelSeleccionado, modeloWorkouts, todosLosWorkouts, nivelActual);
            }
        });

        // Tabla Workouts
        modeloWorkouts = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Nivel", "Nombre", "Descripción", "Video URL" });
        tablaWorkouts = new JTable(modeloWorkouts);
        // Celdas a centrar en Workouts: Columna 0 (Nivel), Columna 1 (Nombre) y Columna 3 (Video)
        estilizarTabla(tablaWorkouts, new int[] { 0, 1, 3 });

        // Ajustar los anchos de las columnas para Workouts
        TableColumnModel columnModelWorkouts = tablaWorkouts.getColumnModel();
        columnModelWorkouts.getColumn(0).setPreferredWidth(60);   // Nivel (muy estrecho)
        columnModelWorkouts.getColumn(0).setMaxWidth(80);
        columnModelWorkouts.getColumn(1).setPreferredWidth(180);  // Nombre
        columnModelWorkouts.getColumn(2).setPreferredWidth(370);  // Descripción (espacio amplio)
        columnModelWorkouts.getColumn(3).setPreferredWidth(180);  // Video URL

        scrollWorkouts = new JScrollPane(tablaWorkouts);
        scrollWorkouts.setBorder(new CompoundBorder());
        scrollWorkouts.getViewport().setBackground(Color.WHITE);
        scrollWorkouts.setBounds(260, 145, 825, 175);
        contentPane.add(scrollWorkouts);

        workoutsVisibles = controlador.mostrarWorkoutsPorNivel(
                nivelSeleccionado(), modeloWorkouts, todosLosWorkouts, nivelActual);
        if (workoutsVisibles == null) {
            workoutsVisibles = new ArrayList<>();
        }

        tablaWorkouts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = tablaWorkouts.getSelectedRow();
                int columna = tablaWorkouts.getSelectedColumn();

                if (fila >= 0 && fila < workoutsVisibles.size()) {
                    Workouts seleccionado = workoutsVisibles.get(fila);

                    // 1. Cargar los ejercicios en la tabla inferior (también para los bloqueados)
                    modeloEjers.setRowCount(0);
                    controlador.mostrarEjerciciosPorWorkout(seleccionado, modeloEjers);

                    // 2. Si se hace DOBLE CLIC (o se hace clic en la columna de Video URL - índice 3)
                    if (evt.getClickCount() == 2 || columna == 3) {
                        String videoUrl = seleccionado.getVideoUrl();

                        // IMPRIMIR EN CONSOLA PARA VER QUÉ LLEGA REALMENTE
                        System.out.println("----------------------------------------");
                        System.out.println("Workout pulsado: " + seleccionado.getNombre());
                        System.out.println("Campo videoUrl en objeto: '" + videoUrl + "'");
                        System.out.println("----------------------------------------");

                        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                            try {
                                // Comprobar si el sistema admite abrir el navegador
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                    // Asegurarse de que empiece por http:// o https://
                                    if (!videoUrl.startsWith("http://") && !videoUrl.startsWith("https://")) {
                                        videoUrl = "https://" + videoUrl;
                                    }
                                    Desktop.getDesktop().browse(new URI(videoUrl));
                                } else {
                                    JOptionPane.showMessageDialog(PanelWorkouts.this,
                                            "No se puede abrir el navegador en este sistema.",
                                            "Error", JOptionPane.WARNING_MESSAGE);
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelWorkouts.this,
                                        "Error al abrir la URL: " + ex.getMessage(),
                                        "Error de enlace", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(PanelWorkouts.this,
                                    "Este workout no tiene un enlace de video registrado.",
                                    "Sin video", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        // Botón Comenzar Entrenamiento
        JButton btnComenzarEjercicio = new JButton("Comenzar Entrenamiento");
        btnComenzarEjercicio.setBackground(new Color(232, 188, 108));
        btnComenzarEjercicio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComenzarEjercicio.setBounds(549, 638, 253, 44);
        btnComenzarEjercicio.setBorder(null);
        btnComenzarEjercicio.setFocusPainted(false);
        btnComenzarEjercicio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComenzarEjercicio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int filaWorkout = tablaWorkouts.getSelectedRow();

                if (filaWorkout < 0 || filaWorkout >= workoutsVisibles.size()) {
                    JOptionPane.showMessageDialog(PanelWorkouts.this,
                            "Selecciona primero un workout.");
                    return;
                }

                Workouts workoutSeleccionado = workoutsVisibles.get(filaWorkout);

                // 🔒 Bloqueo por nivel
                if (workoutSeleccionado.getNivel() > nivelActual) {
                    String nombreNivel = workoutSeleccionado.getNivel() < NOMBRES_NIVEL.length
                            ? NOMBRES_NIVEL[workoutSeleccionado.getNivel()] : "superior";
                    JOptionPane.showMessageDialog(PanelWorkouts.this,
                            "Este workout es de nivel " + nombreNivel
                            + ".\nCompleta los workouts de tu nivel actual para desbloquearlo.",
                            "Workout bloqueado", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (workoutSeleccionado.getEjer() == null || workoutSeleccionado.getEjer().isEmpty()) {
                    JOptionPane.showMessageDialog(PanelWorkouts.this,
                            "Este workout no tiene ejercicios.");
                    return;
                }

                PanelEjercicios panelEj = new PanelEjercicios(usu, workoutSeleccionado);
                panelEj.setVisible(true);
                dispose();
            }
        });
        contentPane.add(btnComenzarEjercicio);

        // Tabla Ejercicios
        modeloEjers = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Nombre", "Descripción", "Duración" });
        tablaEjers = new JTable(modeloEjers);
        estilizarTabla(tablaEjers, new int[] { 0, 1, 2 });

        // Anchos de columnas
        TableColumnModel columnModelEjers = tablaEjers.getColumnModel();
        columnModelEjers.getColumn(0).setPreferredWidth(180); // Nombre
        columnModelEjers.getColumn(1).setPreferredWidth(490); // Descripción
        columnModelEjers.getColumn(2).setPreferredWidth(120); // Duración

        scrollEjers = new JScrollPane(tablaEjers);
        scrollEjers.setBorder(new CompoundBorder());
        scrollEjers.getViewport().setBackground(Color.WHITE);
        scrollEjers.setBounds(260, 385, 825, 198);
        contentPane.add(scrollEjers);
    }

    // ------------------ MÉTODOS AUXILIARES ------------------

    private void rellenarNiveles() {
        nivel.addItem("Todos los niveles");
        TreeSet<Integer> niveles = new TreeSet<>();
        for (Workouts w : todosLosWorkouts) {
            niveles.add(w.getNivel());
        }
        for (Integer n : niveles) {
            nivel.addItem(n);
        }
    }

    private int nivelSeleccionado() {
        Object sel = nivel.getSelectedItem();
        return (sel instanceof Integer) ? (Integer) sel : -1;
    }

    /**
     * Aplica el estilo base a la tabla y permite centrar específicamente las columnas indicadas en el array.
     */
    private void estilizarTabla(final JTable tabla, final int[] columnasCentradas) {
        tabla.setDefaultEditor(Object.class, null);
        tabla.setFont(FUENTE_TABLA);
        tabla.setRowHeight(28);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(SystemColor.info);
        tabla.setSelectionForeground(SystemColor.desktop);
        tabla.setFillsViewportHeight(true);

        // Renderizador que controla la alineación por columna y alterna colores de fila
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);

                // Comprobar si esta columna debe ir centrada
                boolean centrar = false;
                for (int colCentrada : columnasCentradas) {
                    if (column == colCentrada) {
                        centrar = true;
                        break;
                    }
                }

                if (centrar) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_FILA_ALT);
                    c.setForeground(Color.BLACK);

                    // Gris si el workout está bloqueado (solo en la tabla de workouts)
                    if (t == tablaWorkouts && row < workoutsVisibles.size()
                            && workoutsVisibles.get(row).getNivel() > nivelActual) {
                        c.setForeground(Color.GRAY);
                    }
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JTableHeader cabecera = tabla.getTableHeader();
        cabecera.setFont(FUENTE_CABECERA);
        cabecera.setBackground(COLOR_OSCURO);
        cabecera.setForeground(Color.WHITE);
        cabecera.setReorderingAllowed(false);
        cabecera.setPreferredSize(new java.awt.Dimension(cabecera.getWidth(), 32));
    }

    /** Crea botones de navegación para la barra lateral */
    private JButton crearBotonSidebar(String texto, Color fondo, Color textoColor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(fondo);
        btn.setForeground(textoColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 210), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

}