package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import conexion.Conexion;
import controlador.Controlador;
import modelo.Historial;
import modelo.Usuario;
import modelo.Workouts;

import javax.swing.border.EmptyBorder;
import java.awt.SystemColor;

public class PanelLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEmail;
	private JPasswordField txtCntr;
	private JButton btnLogin;
	private JButton btnIrRegistro;
	Controlador controlador = new Controlador();
	
	
	
	
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PanelLogin frame = new PanelLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public PanelLogin() {
		setTitle("EloGym - Iniciar Sesión");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 420, 530);
		setLocationRelativeTo(null);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(245, 247, 250)); 
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(10, 20, 384, 445);
		panel.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
		contentPane.add(panel);
		panel.setLayout(null);


		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setBounds(140, 23, 100, 100);

		java.net.URL urlLogo = PanelLogin.class.getResource("/img/EloGymLogo.png");
		ImageIcon icon = null;

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

		JLabel lblUsu = new JLabel("Correo electrónico / Usuario:");
		lblUsu.setForeground(new Color(70, 70, 70));
		lblUsu.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblUsu.setBounds(64, 134, 250, 20);
		panel.add(lblUsu);

		txtEmail = new JTextField("Ingrese su usuario");
		txtEmail.setForeground(Color.GRAY);
		txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtEmail.setBounds(64, 159, 250, 35);
		txtEmail.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		
		txtEmail.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtEmail.getText().equals("Ingrese su usuario")) {
					txtEmail.setText("");
					txtEmail.setForeground(Color.BLACK);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtEmail.getText().isEmpty()) {
					txtEmail.setText("Ingrese su usuario");
					txtEmail.setForeground(Color.GRAY);
				}
			}
		});
		panel.add(txtEmail);

		JLabel lblCntr = new JLabel("Contraseña:");
		lblCntr.setForeground(new Color(70, 70, 70));
		lblCntr.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblCntr.setBounds(64, 209, 250, 20);
		panel.add(lblCntr);

		txtCntr = new JPasswordField();
		txtCntr.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtCntr.setBounds(64, 234, 250, 35);
		txtCntr.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		panel.add(txtCntr);

		JButton btnLogin = new JButton("INICIAR SESIÓN");
		btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnLogin.setBackground(new Color(232, 188, 108)); 
		btnLogin.setForeground(new Color(0, 0, 0));
		btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnLogin.setFocusPainted(false);
		btnLogin.setBorderPainted(false);
		btnLogin.setBounds(64, 294, 250, 40);
		
		ActionListener accionLogin = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String usuarioTexto = txtEmail.getText();
				String password = new String(txtCntr.getPassword());

				if (usuarioTexto.isEmpty() || usuarioTexto.equals("Ingrese su usuario") || password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Por favor, rellene todos los campos.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				try {
					Firestore db = Conexion.conectar();
					boolean usuarioValido = controlador.verificarUsuario(usuarioTexto,password);
					
					if(!usuarioValido) {
						JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.", "Intentelo de nuevo", JOptionPane.WARNING_MESSAGE);
						return;
					}
					List <QueryDocumentSnapshot> resultados = db.collection("usuarios")
							.whereEqualTo("email", usuarioTexto)
							.whereEqualTo("contraseña", password)
							.get()
							.get()
							.getDocuments();
					
					if(resultados.isEmpty()) {
						JOptionPane.showMessageDialog(null, "El usuario no existe en la base de datos.","Intentelo de nuevo", JOptionPane.WARNING_MESSAGE);
						return;
					}
					DocumentSnapshot docUsuario = resultados.get(0);
					
					Usuario usuario = new Usuario (
							docUsuario.getString("nombre"),
							docUsuario.getString("apellidos"),
							docUsuario.getString("email"),
							docUsuario.getString("contraseña"),
							docUsuario.contains("nivel")? docUsuario.getLong("nivel").intValue() : 0,
							docUsuario.getDate("fecha_nacimiento")
							);
							List<Workouts> workoutsDisponibles = controlador.cargarWorkoutsDesdeFirestore(db);
							
							JOptionPane.showMessageDialog(null,"Accediendo a la aplicacion...","Entrando en la aplicacion",JOptionPane.WARNING_MESSAGE);
							PanelWorkouts siguiente = new PanelWorkouts(docUsuario,workoutsDisponibles);
							
							ArrayList<Historial> historialList = new ArrayList <> (controlador.obtenerHistorialWorkouts(docUsuario.getId()));
							boolean backupValido = controlador.guardarBackupUsuario(usuarioTexto, workoutsDisponibles,historialList);
							
							if(backupValido)
								System.out.println("Backup generado para: " + usuario.getNombre());
							else 
								System.out.println("Error generando backup para usuario.");
							
							siguiente.setVisible(true);
							dispose();
				
				
				} catch (InterruptedException | ExecutionException | IOException ex) {
					JOptionPane.showMessageDialog(null,"Error al conectar con FireStore", "Error de conexion",JOptionPane.WARNING_MESSAGE);
				}
				
			}
		};
		
		
		
		
		btnLogin.addActionListener(accionLogin);
		txtCntr.addActionListener(accionLogin); 
		panel.add(btnLogin);


		JButton btnIrRegistro = new JButton("¿No tienes cuenta? Regístrate");
		btnIrRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnIrRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnIrRegistro.setForeground(new Color(30, 144, 255));
		btnIrRegistro.setContentAreaFilled(false);
		btnIrRegistro.setBorderPainted(false);
		btnIrRegistro.setFocusPainted(false);
		btnIrRegistro.setBounds(64, 349, 250, 25);
		
		btnIrRegistro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PanelRegistro reg = new PanelRegistro();
				reg.setVisible(true);
				dispose(); 
			}
		});
		panel.add(btnIrRegistro);
	}


}