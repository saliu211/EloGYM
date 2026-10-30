package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.google.cloud.Timestamp; // CORREGIDO: es el Timestamp de Google, no el de java.sql
import com.google.cloud.firestore.Firestore;

import conexion.Conexion;
import controlador.Controlador;

public class PanelRegistro extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNombre;
	private JTextField txtEmail;
	private JTextField txtApellido;
	private JPasswordField passwordField;
	private JTextField txtFechaNacimiento;

	public PanelRegistro() {

		setTitle("EloGym - Registro de Usuario");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 620);
		setLocationRelativeTo(null);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(245, 247, 250));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 434, 581);
		panel.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
		contentPane.add(panel);
		panel.setLayout(null);

		// ================ LOGO ================
		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setBounds(158, 23, 100, 100);

		java.net.URL urlLogo = PanelRegistro.class.getResource("/img/EloGymLogo.png");
		ImageIcon icon = null;

		// Si no lo encuentra dentro del classpath, lo busca como archivo normal
		if (urlLogo != null) {
			icon = new ImageIcon(urlLogo);
		} else {
			icon = new ImageIcon("img/EloGymLogo.png");
		}

		if (icon.getImage() != null) {
			Image imgEscalada = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			lblLogo.setIcon(new ImageIcon(imgEscalada));
		}
		panel.add(lblLogo);

		// ================ NOMBRE ================
		JLabel lblNombre = new JLabel("Nombre completo:");
		lblNombre.setForeground(new Color(70, 70, 70));
		lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblNombre.setBounds(59, 134, 300, 18);
		panel.add(lblNombre);

		txtNombre = new JTextField();
		txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtNombre.setBounds(59, 154, 300, 32);
		txtNombre.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		panel.add(txtNombre);

		// ================ APELLIDO ================
		JLabel lblApellido = new JLabel("Apellido:");
		lblApellido.setForeground(new Color(70, 70, 70));
		lblApellido.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblApellido.setBounds(59, 197, 300, 18);
		panel.add(lblApellido);

		txtApellido = new JTextField();
		txtApellido.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtApellido.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		txtApellido.setBounds(59, 220, 300, 32);
		panel.add(txtApellido);

		// ================ EMAIL ================
		JLabel lblEmail = new JLabel("Correo electrónico:");
		lblEmail.setForeground(new Color(70, 70, 70));
		lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblEmail.setBounds(59, 263, 300, 18);
		panel.add(lblEmail);

		txtEmail = new JTextField();
		txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtEmail.setBounds(59, 283, 300, 32);
		txtEmail.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		panel.add(txtEmail);

		// ================ CONTRASEÑA ================
		JLabel lblPassword = new JLabel("Contraseña:");
		lblPassword.setForeground(new Color(70, 70, 70));
		lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblPassword.setBounds(59, 326, 300, 18);
		panel.add(lblPassword);

		passwordField = new JPasswordField();
		passwordField.setBounds(59, 346, 300, 31);
		passwordField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		panel.add(passwordField);

		// ================ FECHA DE NACIMIENTO ================
		JLabel lblFechaNacimiento = new JLabel("Fecha de nacimiento (dd/MM/yyyy):");
		lblFechaNacimiento.setForeground(new Color(70, 70, 70));
		lblFechaNacimiento.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblFechaNacimiento.setBounds(59, 388, 300, 18);
		panel.add(lblFechaNacimiento);

		txtFechaNacimiento = new JTextField();
		txtFechaNacimiento.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtFechaNacimiento.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		txtFechaNacimiento.setBounds(59, 408, 300, 32); // CORREGIDO: alineado con el resto de campos
		panel.add(txtFechaNacimiento);

		// ================ BOTON CREAR CUENTA ================
		JButton btnRegistrar = new JButton("CREAR CUENTA");
		btnRegistrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String nombre = txtNombre.getText().trim();
				String apellido = txtApellido.getText().trim();
				String email = txtEmail.getText().trim();
				String contraseña = new String(passwordField.getPassword());
				String fechaTexto = txtFechaNacimiento.getText().trim();
				Date fechaNacmnt = null;

				// Esta variable decide si se llega a registrar o no.
				// Empieza en true y cualquier error la pone en false.
				boolean datosValidos = true;

				// 1. Campos vacios
				if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || contraseña.isEmpty()
						|| fechaTexto.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Campos obligatorios vacíos, por favor complete los campos",
							"Complete todos los campos", JOptionPane.WARNING_MESSAGE);
					datosValidos = false;
				}

				// 2. Formato del correo (solo se comprueba si lo anterior fue bien,
				// asi no salen varios mensajes seguidos)
				if (datosValidos && !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
					JOptionPane.showMessageDialog(null, "Correo inválido.");
					datosValidos = false;
				}

				// 3. Longitud minima de la contraseña
				if (datosValidos && contraseña.length() < 4) {
					JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 4 caracteres.");
					datosValidos = false;
				}

				// 4. Fecha: formato correcto
				if (datosValidos) {
					try {
						SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
						formato.setLenient(false); // no permite fechas imposibles como 31/02/2000
						fechaNacmnt = formato.parse(fechaTexto);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Fecha inválida. Usa el formato dd/MM/yyyy");
						datosValidos = false;
					}
				}

				// 5. Fecha: no puede ser futura
				if (datosValidos && fechaNacmnt.after(new Date())) {
					JOptionPane.showMessageDialog(null, "La fecha de nacimiento no puede ser futura.");
					datosValidos = false;
				}

				// 6. Correo duplicado.
				// CORREGIDO: el mensaje se muestra solo aqui (antes salia dos veces)
				if (datosValidos && Controlador.verificarGmail(email)) {
					JOptionPane.showMessageDialog(null, "Este correo ya está registrado.");
					datosValidos = false;
				}

				// 7. Si todo es correcto, registramos en Firebase
				if (datosValidos) {
					try {
						// Conexion con Firebase
						Firestore db = Conexion.conectar();

						// Un formato para el id del usuario, que se suma segun se crea
						int totalUsuarios = db.collection("usuarios").get().get().size();
						String id = "usuario" + (totalUsuarios + 1);

						int nivelActl = 0;

						// Pasamos la fecha al formato que entiende Firestore
						Timestamp fechaTimestamp = Timestamp.of(fechaNacmnt);

						// CORREGIDO: registrarUsuario NO es estatico, hay que crear el objeto
						// CORREGIDO: los parametros van en el orden que espera el metodo
						Controlador controlador = new Controlador();
						boolean registrado = controlador.registrarUsuario(id, nombre, apellido, email, contraseña,
								nivelActl, fechaTimestamp);

						if (registrado) {
							JOptionPane.showMessageDialog(null, "Usuario registrado correctamente.");
							PanelLogin principal = new PanelLogin();
							principal.setVisible(true);
							dispose();
						} else {
							JOptionPane.showMessageDialog(null, "Error al registrar el usuario.");
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
						ex.printStackTrace();
					}
				}

			}
		});

		btnRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRegistrar.setBackground(new Color(232, 188, 108));
		btnRegistrar.setForeground(new Color(0, 0, 0));
		btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnRegistrar.setFocusPainted(false);
		btnRegistrar.setBorderPainted(false);
		btnRegistrar.setBounds(59, 463, 300, 40);
		panel.add(btnRegistrar);

		// ================ BOTON IR AL LOGIN ================
		JButton btnIrLogin = new JButton("¿Ya tienes cuenta? Inicia sesión");
		btnIrLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PanelLogin reg = new PanelLogin();
				reg.setVisible(true);
				dispose();
			}
		});
		panel.add(btnIrLogin);

		btnIrLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnIrLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnIrLogin.setForeground(new Color(30, 144, 255));
		btnIrLogin.setContentAreaFilled(false);
		btnIrLogin.setBorderPainted(false);
		btnIrLogin.setFocusPainted(false);
		btnIrLogin.setBounds(59, 513, 300, 25);
		panel.add(btnIrLogin);

	}
}