package vista;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.google.cloud.firestore.DocumentSnapshot;
import modelo.Workouts;
import modelo.Ejercicios;
import modelo.Historial;
import modelo.Series;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelHistorialWorkouts extends JFrame {

    private static final long serialVersionUID = 1L;

    // Colores y fuentes de la interfaz idénticos a PanelWorkouts
    private static final Color COLOR_PRIMARIO = new Color(30, 144, 255);
    private static final Color COLOR_OSCURO = new Color(25, 25, 25);
    private static final Color COLOR_FILA_ALT = new Color(240, 245, 255);
    private static final Font FUENTE_TABLA = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_CABECERA = new Font("Segoe UI", Font.BOLD, 13);

    private JPanel contentPane;
    private JTable tableWrkts;

    public PanelHistorialWorkouts(DocumentSnapshot usu, List<Historial> historialRealizado, List<Workouts> workoutsDisponibles) {
        setTitle("Elogym - Historial de Workouts");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 981, 561);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Historial Workouts");
        lblTitulo.setFont(new Font("Verdana", Font.BOLD, 35));
        lblTitulo.setBounds(267, 30, 384, 62);
        contentPane.add(lblTitulo);

        JButton btnAtras = new JButton("Volver");
        btnAtras.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAtras.setBackground(new Color(232, 188, 108));
        btnAtras.setForeground(Color.BLACK);
        btnAtras.setBorder(null);
        btnAtras.setFocusPainted(false);
        btnAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtras.setBounds(410, 360, 170, 40);
        btnAtras.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PanelWorkouts pw = new PanelWorkouts(usu, workoutsDisponibles);
                pw.setVisible(true);
                dispose();
            }
        });

        contentPane.add(btnAtras);

        DefaultTableModel modeloHstrlWorkouts = new DefaultTableModel(
            new Object[][] {},
            new String[] { "Nombre Workout", "Nivel", "Tiempo Total", "Tiempo Previsto", "Fecha", "% Ejercicios Completados" }
        );

        tableWrkts = new JTable(modeloHstrlWorkouts);

        // Todas las columnas centradas
        estilizarTabla(tableWrkts, new int[] { 0, 1, 2, 3, 4, 5 });

        // Ajustar proporciones de columnas
        TableColumnModel cm = tableWrkts.getColumnModel();
        cm.getColumn(0).setPreferredWidth(240); // Nombre
        cm.getColumn(1).setPreferredWidth(100); // Nivel
        cm.getColumn(2).setPreferredWidth(130); // Tiempo Total
        cm.getColumn(3).setPreferredWidth(130); // Tiempo Previsto
        cm.getColumn(4).setPreferredWidth(120); // Fecha
        cm.getColumn(5).setPreferredWidth(160); // % Completado

        JScrollPane scrollWorkouts = new JScrollPane(tableWrkts);
        scrollWorkouts.setBorder(new CompoundBorder());
        scrollWorkouts.getViewport().setBackground(Color.WHITE);
        scrollWorkouts.setBounds(27, 124, 907, 207);
        contentPane.add(scrollWorkouts);

        // Ordenar historial por fecha descendente
        ordenarHistorialPorFechaDesc(historialRealizado);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Historial h : historialRealizado) {
            if (h.getPorcentajeCompletado() <= 0) {
                continue;
            }
            String nombre = h.getNombreWorkout();

            // Forzar la traducción según el entero del nivel
            String nivelTexto;
            switch (h.getNivelWorkout()) {
                case 0:  nivelTexto = "Principiante"; break;
                case 1:  nivelTexto = "Intermedio"; break;
                case 2:  nivelTexto = "Avanzado"; break;
                default: nivelTexto = h.getNivelTexto(); break;
            }

            int segsTotales = h.getTiempoTotal();

            // Si el tiempo previsto no se guardó en el Historial, lo calculamos buscando en la lista
            int segsPrevistos = h.getTiempoPrevisto();
            if (segsPrevistos <= 0 && workoutsDisponibles != null) {
                Workouts workout = buscarWorkoutPorId(workoutsDisponibles, h.getWorkoutId());
                if (workout != null) {
                    segsPrevistos = calcularTiempoPrevisto(workout);
                }
            }

            String fecha = h.getFecha().format(formatter);
            int porcentaje = h.getPorcentajeCompletado();

            modeloHstrlWorkouts.addRow(new Object[] {
                    nombre,
                    nivelTexto,
                    formatearSegundos(segsTotales),
                    formatearSegundos(segsPrevistos),
                    fecha,
                    porcentaje + "%"
            });
        }
    }

    /**
     * Formatea segundos al estándar MM:SS (ej. 322 segs -> 05:22).
     */
    private String formatearSegundos(int totalSegundos) {
        int minutos = totalSegundos / 60;
        int segundos = totalSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    private void ordenarHistorialPorFechaDesc(List<Historial> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                if (lista.get(i).getFecha().compareTo(lista.get(j).getFecha()) < 0) {
                    Historial temp = lista.get(i);
                    lista.set(i, lista.get(j));
                    lista.set(j, temp);
                }
            }
        }
    }

    private Workouts buscarWorkoutPorId(List<Workouts> lista, String id) {
        if (lista == null || id == null) return null;
        for (Workouts w : lista) {
            if (id.equals(w.getId())) {
                return w;
            }
        }
        return null;
    }

    private int calcularTiempoPrevisto(Workouts workout) {
        int total = 0;
        if (workout.getEjer() != null) {
            for (Ejercicios ejer : workout.getEjer()) {
                int descanso = ejer.getTiempoDescanso();
                if (ejer.getSerie() != null) {
                    for (Series serie : ejer.getSerie()) {
                        total += serie.getDuracion() + descanso;
                    }
                }
            }
        }
        return total;
    }

    private void estilizarTabla(final JTable tabla, final int[] columnasCentradas) {
        tabla.setDefaultEditor(Object.class, null);
        tabla.setFont(FUENTE_TABLA);
        tabla.setRowHeight(28);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(COLOR_PRIMARIO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);

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

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) cabecera.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }
}