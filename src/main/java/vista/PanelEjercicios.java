package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.google.cloud.firestore.DocumentSnapshot;

import controlador.Controlador;
import modelo.Ejercicios;
import modelo.HiloCronometro;
import modelo.HiloRegresivo;
import modelo.Series;
import modelo.Workouts;

public class PanelEjercicios extends JFrame {

	private static final long serialVersionUID = 1L;

	// Estados de una tarjeta de serie
	private static final int PENDIENTE = 0;
	private static final int ACTIVA = 1;
	private static final int COMPLETADA = 2;

	private JPanel contentPane;

	// Cabecera
	private JLabel lblCronoPrincipal;   // (2) cronómetro del workout
	private JButton btnIniciar;         // (3) Iniciar / Pausar / Reanudar
	private JButton btnSiguiente;       // (4)
	private JButton btnSalir;           // (5)

	// Zona del ejercicio
	private JLabel lblNombreEjercicio;  // (6)
	private JLabel lblDescEjercicio;
	private JLabel lblCronoEjercicio;   // (7) cronómetro del ejercicio
	private JLabel lblAviso;            // (9) "Empieza en..." / "Descanso..."
	private JPanel panelSeries;         // (8) tarjetas de las series
	private List<JPanel> tarjetas = new ArrayList<>();
	private List<JLabel> tiemposSerie = new ArrayList<>();

	// Hilos (solo reciben un JLabel; la secuencia la gobierna este panel)
	private HiloCronometro hiloCronoPrincipal;
	private HiloCronometro hiloEjercicio;
	private HiloRegresivo hiloActual;

	// Datos
	private DocumentSnapshot usu;
	private Workouts workoutSeleccionado;
	private List<Ejercicios> ejercicios;
	private int indiceEjercicio = 0;
	private int contSeriesActual = 0;
	private int ejerciciosCompletados = 0;
	private boolean workoutIniciado = false;
	private boolean finalizado = false;
	private ArrayList<Integer> tiemposPorEjercicio = new ArrayList<>();

	private Controlador controlador = new Controlador();

	public PanelEjercicios(DocumentSnapshot usu, Workouts workoutSeleccionado) {
		this.usu = usu;
		this.workoutSeleccionado = workoutSeleccionado;
		this.ejercicios = workoutSeleccionado.getEjer() != null
				? workoutSeleccionado.getEjer() : new ArrayList<Ejercicios>();
		initialize();
	}

	private void initialize() {
		setTitle("Elogym - Ejercicio");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1100, 700);
		setLocationRelativeTo(null);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ================= CABECERA OSCURA =================
		JPanel cabecera = new JPanel();
		cabecera.setBackground(new Color(240, 240, 240));
		cabecera.setBounds(20, 15, 1040, 150);
		cabecera.setLayout(null);
		contentPane.add(cabecera);

		// Logo
		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setBounds(15, 15, 120, 120);
		URL urlLogo = PanelEjercicios.class.getResource("/img/EloGymLogo.png");
		if (urlLogo != null) {
			ImageIcon icono = new ImageIcon(urlLogo);
			lblLogo.setIcon(new ImageIcon(icono.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH)));
		}
		cabecera.add(lblLogo);

		// (1) Título y descripción del workout
		JLabel lblWorkout = new JLabel(workoutSeleccionado.getNombre());
		lblWorkout.setForeground(new Color(0, 0, 0));
		lblWorkout.setFont(new Font("Segoe UI", Font.BOLD, 32));
		lblWorkout.setBounds(145, 26, 540, 45);
		cabecera.add(lblWorkout);

		String descWorkout = workoutSeleccionado.getDescripcion() != null
				? workoutSeleccionado.getDescripcion() : "";
		JLabel lblDescWorkout = new JLabel("<html>" + descWorkout + "</html>");
		lblDescWorkout.setForeground(new Color(0, 0, 0));
		lblDescWorkout.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblDescWorkout.setVerticalAlignment(SwingConstants.TOP);
		lblDescWorkout.setBounds(145, 82, 540, 39);
		cabecera.add(lblDescWorkout);

		// (2) Cronómetro del workout
		lblCronoPrincipal = new JLabel("00:00");
		lblCronoPrincipal.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCronoPrincipal.setForeground(new Color(0, 0, 0));
		lblCronoPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 34));
		lblCronoPrincipal.setBounds(800, 12, 220, 45);
		cabecera.add(lblCronoPrincipal);

		// (3) Iniciar / Pausar / Reanudar
		btnIniciar = new JButton("Iniciar");
		btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnIniciar.setBackground(new Color(40, 167, 69));
		btnIniciar.setForeground(new Color(255, 255, 255));
		btnIniciar.setBorderPainted(false);
		btnIniciar.setFocusPainted(false);
		btnIniciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnIniciar.setBounds(720, 70, 150, 32);
		btnIniciar.addActionListener(e -> manejarBotonIniciar());
		cabecera.add(btnIniciar);

		// (4) Siguiente
		btnSiguiente = new JButton("Siguiente");
		btnSiguiente.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnSiguiente.setBackground(new Color(232, 188, 108));
		btnSiguiente.setForeground(new Color(255, 255, 255));
		btnSiguiente.setBorderPainted(false);
		btnSiguiente.setFocusPainted(false);
		btnSiguiente.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnSiguiente.setBounds(720, 110, 150, 32);
		btnSiguiente.setEnabled(false);
		btnSiguiente.addActionListener(e -> pasarAlSiguiente());
		cabecera.add(btnSiguiente);

		// (5) Salir
		btnSalir = new JButton("Salir");
		btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnSalir.setBackground(new Color(235, 20, 20));
		btnSalir.setForeground(new Color(255, 255, 255));
		btnSalir.setBorderPainted(false);
		btnSalir.setFocusPainted(false);
		btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnSalir.setBounds(890, 70, 130, 72);
		btnSalir.addActionListener(e -> mostrarResumen(true));
		cabecera.add(btnSalir);

		// ================= ZONA DEL EJERCICIO =================
		JPanel panelEjercicio = new JPanel();
		panelEjercicio.setBackground(new Color(255, 255, 255));
		panelEjercicio.setBounds(20, 180, 1040, 440);
		panelEjercicio.setLayout(null);
		contentPane.add(panelEjercicio);

		// (6) Nombre y descripción del ejercicio
		lblNombreEjercicio = new JLabel("");
		lblNombreEjercicio.setForeground(new Color(0, 0, 0));
		lblNombreEjercicio.setFont(new Font("Segoe UI", Font.BOLD, 34));
		lblNombreEjercicio.setBounds(25, 15, 700, 45);
		panelEjercicio.add(lblNombreEjercicio);

		lblDescEjercicio = new JLabel("");
		lblDescEjercicio.setForeground(new Color(0, 0, 0));
		lblDescEjercicio.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblDescEjercicio.setBounds(25, 65, 700, 30);
		panelEjercicio.add(lblDescEjercicio);

		// (7) Cronómetro del ejercicio
		lblCronoEjercicio = new JLabel("00:00");
		lblCronoEjercicio.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCronoEjercicio.setForeground(new Color(0, 0, 0));
		lblCronoEjercicio.setFont(new Font("Segoe UI", Font.BOLD, 34));
		lblCronoEjercicio.setBounds(780, 15, 235, 45);
		panelEjercicio.add(lblCronoEjercicio);

		// (9) Zona de avisos (recuadro discontinuo)
		lblAviso = new JLabel("");
		lblAviso.setHorizontalAlignment(SwingConstants.CENTER);
		lblAviso.setForeground(new Color(70, 70, 70));
		lblAviso.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblAviso.setBorder(BorderFactory.createDashedBorder(new Color(70, 70, 70), 2, 5, 3, false));
		lblAviso.setBounds(760, 70, 255, 40);
		panelEjercicio.add(lblAviso);

		// (8) Tarjetas de las series, con desplazamiento horizontal si no caben
		panelSeries = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		panelSeries.setOpaque(false);

		JScrollPane scrollSeries = new JScrollPane(panelSeries,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollSeries.setOpaque(false);
		scrollSeries.getViewport().setOpaque(false);
		scrollSeries.setBorder(null);
		scrollSeries.setBounds(15, 125, 1010, 300);
		panelEjercicio.add(scrollSeries);

		// Mostrar el primer ejercicio
		if (!ejercicios.isEmpty()) {
			mostrarEjercicio(indiceEjercicio);
		} else {
			lblNombreEjercicio.setText("No hay ejercicios en este workout");
			btnIniciar.setEnabled(false);
		}
	}

	private String formato(int segundos) {
		return String.format("%02d:%02d", segundos / 60, segundos % 60);
	}

	// ------------------ TARJETAS DE SERIES ------------------

	private void mostrarEjercicio(int idx) {
		if (idx < 0 || idx >= ejercicios.size()) {
			return;
		}
		Ejercicios ej = ejercicios.get(idx);
		lblNombreEjercicio.setText(ej.getNombre());
		lblDescEjercicio.setText(ej.getDescripcion() != null ? ej.getDescripcion() : "");
		contSeriesActual = 0;

		panelSeries.removeAll();
		tarjetas.clear();
		tiemposSerie.clear();

		if (ej.getSerie() != null) {
			int numero = 1;
			for (Series s : ej.getSerie()) {
				panelSeries.add(crearTarjeta(s, numero));
				numero++;
			}
		}
		panelSeries.revalidate();
		panelSeries.repaint();
	}

	private JPanel crearTarjeta(Series s, int numero) {
		JPanel tarjeta = new JPanel(null);
		tarjeta.setPreferredSize(new Dimension(200, 270));
		tarjeta.setBackground(new Color(255, 255, 255));
		tarjeta.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215), 1, true));

		JLabel foto = new JLabel("", SwingConstants.CENTER);
		foto.setForeground(new Color(100, 100, 100));
		foto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		foto.setBounds(20, 15, 160, 160);
		tarjeta.add(foto);
		cargarImagen(foto, s.getImagenUrl());

		JLabel nombre = new JLabel("Serie " + numero);
		nombre.setForeground(new Color(0, 0, 0));
		nombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
		nombre.setBounds(20, 190, 160, 25);
		tarjeta.add(nombre);

		JLabel tiempo = new JLabel(formato(s.getDuracion()));
		tiempo.setForeground(new Color(0, 0, 0));
		tiempo.setFont(new Font("Segoe UI", Font.BOLD, 18));
		tiempo.setBounds(20, 225, 160, 30);
		tarjeta.add(tiempo);

		tarjetas.add(tarjeta);
		tiemposSerie.add(tiempo);
		return tarjeta;
	}

	// Carga la imagen en segundo plano para no bloquear la interfaz
	private void cargarImagen(final JLabel destino, final String valor) {
		System.out.println("Intentando cargar la imagen: [" + valor + "]");
		if (valor == null || valor.trim().isEmpty()) {
			destino.setText("Sin imagen");
			return;
		}
		final String ruta = valor.trim();

		// Si es una URL web
		if (ruta.startsWith("http")) {
			new SwingWorker<ImageIcon, Void>() {
				private String motivo = "";

				@Override
				protected ImageIcon doInBackground() throws Exception {
					java.net.HttpURLConnection con =
							(java.net.HttpURLConnection) new URL(ruta.replace(" ", "%20")).openConnection();
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					con.setInstanceFollowRedirects(true);
					con.setConnectTimeout(5000);
					con.setReadTimeout(8000);
					int codigo = con.getResponseCode();
					System.out.println("[IMG] HTTP " + codigo + " (" + con.getContentType() + ") -> " + ruta);
					if (codigo != 200) { motivo = "HTTP " + codigo; return null; }
					try (java.io.InputStream in = con.getInputStream()) {
						BufferedImage img = ImageIO.read(in);
						if (img == null) { motivo = "Formato no soportado"; return null; }
						return escalar(img, 160, 160);
					}
				}

				@Override
				protected void done() {
					try {
						ImageIcon icono = get();
						if (icono != null) {
							destino.setIcon(icono);
						} else {
							System.out.println("[IMG] FALLO: " + motivo + " -> " + ruta);
							destino.setText(motivo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						destino.setText("Error");
					}
				}
			}.execute();
			return;
		}

		// Si es una imagen local de recursos (/img/...)
		String recurso = ruta;
		if (recurso.startsWith("img/")) recurso = recurso.substring(4);
		if (!recurso.startsWith("/")) recurso = "/img/" + recurso;
		URL url = PanelEjercicios.class.getResource(recurso);

		if (url == null) {
			destino.setText("No encontrada");
			return;
		}

		try {
			BufferedImage img = ImageIO.read(url);
			if (img != null) {
				destino.setIcon(escalar(img, 160, 160));
			} else {
				destino.setText("Error imagen");
			}
		} catch (Exception e) {
			e.printStackTrace();
			destino.setText("Error imagen");
		}
	}

	private ImageIcon escalar(BufferedImage img, int maxAncho, int maxAlto) {
		// Protección contra imagen nula
		if (img == null) {
			return null;
		}
		double f = Math.min((double) maxAncho / img.getWidth(), (double) maxAlto / img.getHeight());
		int w = Math.max(1, (int) (img.getWidth() * f));
		int h = Math.max(1, (int) (img.getHeight() * f));
		return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	// Cambia el borde de la tarjeta según su estado
	private void marcarTarjeta(int idx, int estado) {
		if (idx < 0 || idx >= tarjetas.size()) {
			return;
		}
		if (estado == ACTIVA) {
			tarjetas.get(idx).setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 3, true));
		} else if (estado == COMPLETADA) {
			tarjetas.get(idx).setBorder(BorderFactory.createLineBorder(new Color(40, 167, 69), 3, true));
		} else {
			tarjetas.get(idx).setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215), 1, true));
		}
	}

	// ------------------ SECUENCIA DE RELOJES ------------------
	// Los hilos solo pintan en el JLabel que reciben. Este panel encadena
	// inicio -> serie -> descanso mediante callbacks y gestiona botones y contadores.

	private void manejarBotonIniciar() {
		String texto = btnIniciar.getText();

		if ("Iniciar".equals(texto)) {
			// (2) Cronómetro del workout: solo la primera vez, nunca se reinicia
			if (!workoutIniciado) {
				workoutIniciado = true;
				hiloCronoPrincipal = new HiloCronometro(lblCronoPrincipal, "");
				hiloCronoPrincipal.start();
			}
			// (7) Cronómetro del ejercicio: arranca con el primer "Iniciar" del ejercicio
			if (hiloEjercicio == null) {
				hiloEjercicio = new HiloCronometro(lblCronoEjercicio, "");
				hiloEjercicio.start();
			}
			btnIniciar.setText("Pausar");
			btnIniciar.setBackground(new Color(255, 165, 0));
			btnIniciar.setForeground(new Color(0, 0, 0));
			empezarCuentaInicio();

		} else if ("Pausar".equals(texto)) {
			btnIniciar.setText("Reanudar");
			btnIniciar.setBackground(new Color(70, 130, 180));
			btnIniciar.setForeground(new Color(255, 255, 255));
			alternarPausa();

		} else if ("Reanudar".equals(texto)) {
			btnIniciar.setText("Pausar");
			btnIniciar.setBackground(new Color(255, 165, 0));
			btnIniciar.setForeground(new Color(0, 0, 0));
			alternarPausa();
			// prueba commit
		}
	}

	private void alternarPausa() {
		if (hiloCronoPrincipal != null) hiloCronoPrincipal.cambiarEstado();
		if (hiloEjercicio != null) hiloEjercicio.cambiarEstado();
		if (hiloActual != null) hiloActual.cambiarEstado();
	}

	// (9) Cuenta de inicio: 5 s antes de cada serie
	private void empezarCuentaInicio() {
		marcarTarjeta(contSeriesActual, ACTIVA);
		hiloActual = new HiloRegresivo(lblAviso, "Empieza en: ", 5, () -> empezarSerie());
		hiloActual.start();
	}

	// (8) Cuenta de la serie, en la tarjeta de la serie actual
	private void empezarSerie() {
		lblAviso.setText("");
		Ejercicios ej = ejercicios.get(indiceEjercicio);
		int duracion = ej.getSerie().get(contSeriesActual).getDuracion();
		JLabel lblTiempo = tiemposSerie.get(contSeriesActual);
		hiloActual = new HiloRegresivo(lblTiempo, "", duracion, () -> empezarDescanso());
		hiloActual.start();
	}

	// (9) Cuenta del descanso: automática al terminar la serie
	private void empezarDescanso() {
		marcarTarjeta(contSeriesActual, COMPLETADA);
		Ejercicios ej = ejercicios.get(indiceEjercicio);
		hiloActual = new HiloRegresivo(lblAviso, "Descanso: ", ej.getTiempoDescanso(), () -> terminarDescanso());
		hiloActual.start();
	}

	private void terminarDescanso() {
		lblAviso.setText("");
		Ejercicios ej = ejercicios.get(indiceEjercicio);
		contSeriesActual++;

		if (contSeriesActual < ej.getSerie().size()) {
			// Quedan series: se espera a que el usuario pulse "Iniciar"
			btnIniciar.setText("Iniciar");
			btnIniciar.setBackground(new Color(40, 167, 69));
			btnIniciar.setForeground(new Color(255, 255, 255));
		} else {
			// Ejercicio completado
			ejerciciosCompletados++;
			if (hiloEjercicio != null) {
				tiemposPorEjercicio.add(hiloEjercicio.getTotalSegundos());
			}
			if (indiceEjercicio + 1 < ejercicios.size()) {
				btnIniciar.setEnabled(false);
				btnSiguiente.setEnabled(true);
			} else {
				mostrarResumen(false); // último ejercicio: fin del workout
			}
		}
	}

	private void pasarAlSiguiente() {
		if (hiloEjercicio != null) hiloEjercicio.terminar();
		indiceEjercicio++;
		mostrarEjercicio(indiceEjercicio);

		// (7) vuelve a 00:00 y arranca de nuevo
		lblCronoEjercicio.setText("00:00");
		hiloEjercicio = new HiloCronometro(lblCronoEjercicio, "");
		hiloEjercicio.start();

		lblAviso.setText("");
		btnSiguiente.setEnabled(false);
		btnIniciar.setEnabled(true);
		btnIniciar.setText("Iniciar");
		btnIniciar.setBackground(new Color(40, 167, 69));
		btnIniciar.setForeground(new Color(255, 255, 255));
	}

	// ------------------ RESUMEN FINAL ------------------

	private void mostrarResumen(boolean esSalida) {
		if (finalizado) {
			return;
		}
		finalizado = true;

		// Parar todo; al cancelar hiloActual su callback ya no se ejecuta
		if (hiloActual != null) hiloActual.terminar();
		if (hiloEjercicio != null) hiloEjercicio.terminar();
		if (hiloCronoPrincipal != null) hiloCronoPrincipal.terminar();

		// Si no se inició o no se completó ningún ejercicio: no se guarda nada
		if (!workoutIniciado || ejerciciosCompletados == 0) {
			PanelWorkouts pw = new PanelWorkouts(usu, null);
			pw.setVisible(true);
			dispose();
			return;
		}

		int segundos = hiloCronoPrincipal != null ? hiloCronoPrincipal.getTotalSegundos() : 0;
		String tiempoTotal = formato(segundos);

		int total = ejercicios.size();
		int porcentaje = total == 0 ? 0 : (ejerciciosCompletados * 100 / total);

		int previsto = 0;
		for (Ejercicios e : ejercicios) {
			previsto += e.getDuracionTotal();
		}

		String mensaje;
		if (porcentaje == 100) mensaje = "¡Enhorabuena! Has completado el workout 💪";
		else if (porcentaje >= 75) mensaje = "¡Muy bien! Estás cerca del objetivo.";
		else if (porcentaje >= 40) mensaje = "Buen trabajo, sigue así.";
		else mensaje = "¡A por ello! Cada minuto cuenta.";

		// Registro en el histórico del usuario y posible subida de nivel
		boolean subioNivel = false;
		if (usu != null) {
			int nivelAntes = controlador.obtenerNivelUsuario(usu);

			boolean guardado = controlador.guardarHistorial(
					usu.getId(), workoutSeleccionado, segundos, previsto, porcentaje);

			if (guardado) {
				DocumentSnapshot usuNuevo = controlador.subirNivelSiCorresponde(
						usu.getId(), workoutSeleccionado.getNivel(), porcentaje);
				if (usuNuevo != null) {
					usu = usuNuevo; // a partir de aquí se usa el usuario actualizado
					subioNivel = controlador.obtenerNivelUsuario(usu) > nivelAntes;
				}
			}
		}

		if (subioNivel) {
			mensaje += "<br/><br/>🎉 ¡Has subido de nivel! Ya puedes hacer los workouts del siguiente nivel.";
		}

		String resumen = String.format(
				"<html><body style='width:300px'>Tiempo total: %s<br/>Ejercicios completados: %d/%d (%d%%)<br/><br/>%s</body></html>",
				tiempoTotal, ejerciciosCompletados, total, porcentaje, mensaje);

		JOptionPane.showOptionDialog(this, resumen, "Resumen workout", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, new String[] { "Confirmar" }, "Confirmar");

		// Vuelta a la pantalla de workouts (con el usuario ya actualizado)
		PanelWorkouts pw = new PanelWorkouts(usu, null);
		pw.setVisible(true);
		dispose();
	}
}