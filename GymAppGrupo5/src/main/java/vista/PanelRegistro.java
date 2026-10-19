package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelRegistro extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNombre;
	private JTextField txtEmail;
	private JPasswordField txtPassword;
	private JPasswordField txtConfirmPassword;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PanelRegistro frame = new PanelRegistro();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PanelRegistro() {
		
		setTitle("EloGym - Registro de Usuario");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 620);
		setLocationRelativeTo(null);
		setResizable(false);

		// Fondo principal (Gris claro)
		contentPane = new JPanel();
		contentPane.setBackground(new Color(245, 247, 250));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Tarjeta blanca contenedora
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(10, 20, 414, 535);
		panel.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setBounds(147, 11, 100, 100);

		java.net.URL urlLogo = PanelRegistro.class.getResource("/img/EloGymLogo.png");
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

		JLabel lblNombre = new JLabel("Nombre completo:");
		lblNombre.setForeground(new Color(70, 70, 70));
		lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblNombre.setBounds(54, 114, 300, 18);
		panel.add(lblNombre);

		txtNombre = new JTextField();
		txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtNombre.setBounds(54, 134, 300, 32);
		txtNombre.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		panel.add(txtNombre);

		JLabel lblEmail = new JLabel("Correo electrónico:");
		lblEmail.setForeground(new Color(70, 70, 70));
		lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblEmail.setBounds(54, 174, 300, 18);
		panel.add(lblEmail);

		txtEmail = new JTextField();
		txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtEmail.setBounds(54, 194, 300, 32);
		txtEmail.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		panel.add(txtEmail);

		JLabel lblPassword = new JLabel("Contraseña:");
		lblPassword.setForeground(new Color(70, 70, 70));
		lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblPassword.setBounds(54, 234, 300, 18);
		panel.add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtPassword.setBounds(54, 254, 300, 32);
		txtPassword.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		panel.add(txtPassword);

		JLabel lblConfirm = new JLabel("Confirmar contraseña:");
		lblConfirm.setForeground(new Color(70, 70, 70));
		lblConfirm.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblConfirm.setBounds(54, 294, 300, 18);
		panel.add(lblConfirm);

		txtConfirmPassword = new JPasswordField();
		txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtConfirmPassword.setBounds(54, 314, 300, 32);
		txtConfirmPassword.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		panel.add(txtConfirmPassword);


		JButton btnRegistrar = new JButton("CREAR CUENTA");
		btnRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRegistrar.setBackground(new Color(232, 188, 108));
		btnRegistrar.setForeground(new Color(0, 0, 0));
		btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnRegistrar.setFocusPainted(false);
		btnRegistrar.setBorderPainted(false);
		btnRegistrar.setBounds(54, 369, 300, 40);
		panel.add(btnRegistrar);


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
		btnIrLogin.setBounds(54, 419, 300, 25);
		panel.add(btnIrLogin);
	}
}