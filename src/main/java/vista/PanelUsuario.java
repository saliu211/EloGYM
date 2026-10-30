package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;

import controlador.Controlador;
import modelo.Workouts;

public class PanelUsuario extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNmbr;
    private JTextField txtAplld;
    private JTextField txtFechaNacmnt;
    private JTextField txtEmail;
    private JPasswordField passwordField;

    Controlador controlador = new Controlador();

    public PanelUsuario(DocumentSnapshot nvUsu, List<Workouts> todosLosWorkouts) {
        setTitle("Elogym - Perfil de Usuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 680, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🔹 Panel Principal con fondo oscuro sólido (coherente con ModificarUsuario)
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255)); // Azul/Gris noche oscuro
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // 🔹 Extraer y procesar datos del usuario
        if (nvUsu != null) {
            controlador.recogerDatosUsuario(nvUsu);
        }
        
        String nombre = nvUsu != null ? nvUsu.getString("nombre") : "";
        String apellido = "";
        if (nvUsu != null) {
            String valor = nvUsu.getString("apellido");
            if (valor == null) {
                valor = nvUsu.getString("apellidos");
            }
            apellido = (valor != null) ? valor : "";
        }
      
        String email = nvUsu != null ? nvUsu.getString("email") : "";
        String contraseña = nvUsu != null ? nvUsu.getString("contraseña") : "";

        String fechaTexto = "";
        if (nvUsu != null) {
            Object fchRecibida = nvUsu.get("fechaNacimiento");
            if (fchRecibida == null) fchRecibida = nvUsu.get("fecha_nacimiento");
            if (fchRecibida == null) fchRecibida = nvUsu.get("fechaNac");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (fchRecibida instanceof Timestamp) {
                fechaTexto = sdf.format(((Timestamp) fchRecibida).toDate());
            } else if (fchRecibida instanceof Date) {
                fechaTexto = sdf.format((Date) fchRecibida);
            } else if (fchRecibida instanceof Long) {
                fechaTexto = sdf.format(new Date((Long) fchRecibida));
            } else if (fchRecibida instanceof String) {
                fechaTexto = (String) fchRecibida;
            }
            System.out.println("fechaNacimiento -> " + fchRecibida
                    + " (" + (fchRecibida != null ? fchRecibida.getClass().getName() : "null") + ")");
        }
        // 🔹 Tarjeta Central (Contenedor principal)
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(new Color(234, 234, 234));
        cardPanel.setBorder(null);
        cardPanel.setBounds(40, 30, 585, 540);
        cardPanel.setLayout(null);
        contentPane.add(cardPanel);

        // 🔹 LOGO ELOGYM (Arriba a la derecha)
        JLabel lblLogo = new JLabel("");
        lblLogo.setBounds(239, 12, 108, 98);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        java.net.URL urlIcono = PanelUsuario.class.getResource("/img/EloGymLogo.png");
        ImageIcon icon = (urlIcono != null) ? new ImageIcon(urlIcono) : new ImageIcon("img/EloGymLogo.png");

        if (icon.getImage() != null) {
            Image imgEscalada = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(imgEscalada));
        }
        cardPanel.add(lblLogo);

        // 🔹 TÍTULO CON SALUDO
        JLabel lblTitulo = new JLabel("¡Hola, " + (!nombre.isEmpty() ? nombre : "Usuario") + "!");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 0, 0));
        lblTitulo.setBounds(31, 121, 525, 30);
        cardPanel.add(lblTitulo);

        // 🔹 Estilos de fuentes, colores y bordes
        Font fontLabels = new Font("Segoe UI", Font.BOLD, 11);
        Font fontInputs = new Font("Segoe UI", Font.PLAIN, 13);
        Color colorLabel = new Color(170, 180, 195);
        Color colorBgInput = new Color(25, 30, 38); // Fondo de lectura (Read-Only)
        Color colorTxtInput = new Color(220, 225, 235);
        
        CompoundBorder borderInput = new CompoundBorder(
                new LineBorder(new Color(55, 65, 80), 1, true),
                new EmptyBorder(0, 10, 0, 10)
        );

        // --- FILA 1: NOMBRE Y APELLIDO (2 Columnas) ---
        JLabel lblNombre = new JLabel("NOMBRE");
        lblNombre.setForeground(new Color(0, 0, 0));
        lblNombre.setFont(fontLabels);
        lblNombre.setBounds(44, 162, 230, 18);
        cardPanel.add(lblNombre);

        txtNmbr = new JTextField(nombre);
        txtNmbr.setFont(fontInputs);
        txtNmbr.setBackground(new Color(255, 255, 255));
        txtNmbr.setForeground(new Color(0, 0, 0));
        txtNmbr.setBorder(new CompoundBorder());
        txtNmbr.setEditable(false);
        txtNmbr.setBounds(44, 182, 230, 34);
        cardPanel.add(txtNmbr);

        JLabel lblApellido = new JLabel("APELLIDO");
        lblApellido.setForeground(new Color(0, 0, 0));
        lblApellido.setFont(fontLabels);
        lblApellido.setBounds(309, 162, 230, 18);
        cardPanel.add(lblApellido);

        txtAplld = new JTextField(apellido);
        txtAplld.setFont(fontInputs);
        txtAplld.setBackground(new Color(255, 255, 255));
        txtAplld.setForeground(new Color(0, 0, 0));
        txtAplld.setBorder(new CompoundBorder());
        txtAplld.setEditable(false);
        txtAplld.setBounds(309, 182, 230, 34);
        cardPanel.add(txtAplld);

        // --- FILA 2: FECHA Y EMAIL (2 Columnas) ---
        JLabel lblFecha = new JLabel("FECHA NACIMIENTO (dd/mm/yyyy)");
        lblFecha.setForeground(new Color(0, 0, 0));
        lblFecha.setFont(fontLabels);
        lblFecha.setBounds(44, 232, 230, 18);
        cardPanel.add(lblFecha);

        txtFechaNacmnt = new JTextField(fechaTexto);
        txtFechaNacmnt.setFont(fontInputs);
        txtFechaNacmnt.setBackground(new Color(255, 255, 255));
        txtFechaNacmnt.setForeground(new Color(0, 0, 0));
        txtFechaNacmnt.setBorder(new CompoundBorder());
        txtFechaNacmnt.setEditable(false);
        txtFechaNacmnt.setBounds(44, 252, 230, 34);
        cardPanel.add(txtFechaNacmnt);

        JLabel lblEmail = new JLabel("EMAIL / CORREO");
        lblEmail.setForeground(new Color(0, 0, 0));
        lblEmail.setFont(fontLabels);
        lblEmail.setBounds(309, 232, 230, 18);
        cardPanel.add(lblEmail);

        txtEmail = new JTextField(email);
        txtEmail.setFont(fontInputs);
        txtEmail.setBackground(new Color(255, 255, 255));
        txtEmail.setForeground(new Color(0, 0, 0));
        txtEmail.setBorder(new CompoundBorder());
        txtEmail.setEditable(false);
        txtEmail.setBounds(309, 252, 230, 34);
        cardPanel.add(txtEmail);

        // --- FILA 3: CONTRASEÑA (Ancho completo) ---
        JLabel lblContr = new JLabel("CONTRASEÑA");
        lblContr.setForeground(new Color(0, 0, 0));
        lblContr.setFont(fontLabels);
        lblContr.setBounds(44, 302, 495, 18);
        cardPanel.add(lblContr);

        passwordField = new JPasswordField(contraseña);
        passwordField.setFont(fontInputs);
        passwordField.setBackground(new Color(255, 255, 255));
        passwordField.setForeground(new Color(0, 0, 0));
        passwordField.setBorder(new CompoundBorder());
        passwordField.setEditable(false);
        passwordField.setBounds(44, 322, 495, 34);
        cardPanel.add(passwordField);

        // --- FILA 4: BOTONES ---
        // Botón Modificar Datos (Azul primario)
        JButton btnModificar = new JButton("MODIFICAR DATOS");
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModificar.setBackground(new Color(249, 205, 125));
        btnModificar.setForeground(new Color(0, 0, 0));
        btnModificar.setBorderPainted(false);
        btnModificar.setFocusPainted(false);
        btnModificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModificar.setBounds(70, 440, 210, 42);
        btnModificar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PanelModificarUsuario modificarUsuario = new PanelModificarUsuario(nvUsu, todosLosWorkouts);
                modificarUsuario.setVisible(true);
                dispose();
            }
        });
        cardPanel.add(btnModificar);

        // Botón Volver (Rojo primario)
        JButton btnSalir = new JButton("VOLVER");
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalir.setBackground(new Color(180, 20, 35));
        btnSalir.setForeground(new Color(255, 255, 255));
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setBounds(305, 440, 210, 42);
        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PanelWorkouts atras = new PanelWorkouts(nvUsu, todosLosWorkouts);
                atras.setVisible(true);
                dispose();
            }
        });
        cardPanel.add(btnSalir);
    }
}