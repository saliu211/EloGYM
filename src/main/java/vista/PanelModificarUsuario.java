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
import javax.swing.JOptionPane;
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

public class PanelModificarUsuario extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNmbr;
    private JTextField txtAplld;
    private JTextField txtFechaNacmnt;
    private JTextField txtEmail;
    private JPasswordField passwordField;

    Controlador controlador = new Controlador();

    public PanelModificarUsuario(DocumentSnapshot usu, List<Workouts> todosLosWorkouts) {
        setTitle("Elogym - Modificar Perfil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 680, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🔹 Panel Principal Claro
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

     // 🔹 Extraer datos del usuario
        String nombre = "";
        String apellido = "";
        String email = "";
        String contraseña = "";
        String fechaTexto = "";

        if (usu != null) {
            String valor = usu.getString("nombre");
            nombre = (valor != null) ? valor : "";

            valor = usu.getString("apellido");
            if (valor == null) {
                valor = usu.getString("apellidos");
            }
            apellido = (valor != null) ? valor : "";

            valor = usu.getString("email");
            email = (valor != null) ? valor : "";

            valor = usu.getString("contraseña");
            contraseña = (valor != null) ? valor : "";

            Object fchRecibida = usu.get("fechaNacimiento");
            if (fchRecibida == null) fchRecibida = usu.get("fecha_nacimiento");
            if (fchRecibida == null) fchRecibida = usu.get("fechaNac");

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
        }

        // 🔹 Tarjeta Central Gris Suave
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(new Color(234, 234, 234));
        cardPanel.setBorder(new LineBorder(new Color(210, 210, 210), 1, true));
        cardPanel.setBounds(40, 30, 585, 540);
        cardPanel.setLayout(null);
        contentPane.add(cardPanel);

        // 🔹 LOGO ELOGYM
        JLabel lblLogo = new JLabel("");
        lblLogo.setBounds(238, 11, 103, 101);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        java.net.URL urlIcono = PanelModificarUsuario.class.getResource("/img/EloGymLogo.png");
        ImageIcon icon = (urlIcono != null) ? new ImageIcon(urlIcono) : new ImageIcon("img/EloGymLogo.png");

        if (icon.getImage() != null) {
            Image imgEscalada = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(imgEscalada));
        }
        cardPanel.add(lblLogo);

        // 🔹 TÍTULO
        JLabel lblTitulo = new JLabel("Modificar Perfil");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.BLACK);
        lblTitulo.setBounds(30, 124, 525, 30);
        cardPanel.add(lblTitulo);

        // 🔹 Estilos
        Font fontLabels = new Font("Segoe UI", Font.BOLD, 11);
        Font fontInputs = new Font("Segoe UI", Font.PLAIN, 13);
        Color colorBgInput = Color.WHITE;
        Color colorBgDisabled = new Color(200, 200, 200);
        Color colorTxtInput = Color.BLACK;
        
        CompoundBorder borderInput = new CompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1, true),
                new EmptyBorder(0, 10, 0, 10)
        );

        // --- FILA 1: NOMBRE Y APELLIDO ---
        JLabel lblNombre = new JLabel("NOMBRE");
        lblNombre.setForeground(Color.BLACK);
        lblNombre.setFont(fontLabels);
        lblNombre.setBounds(44, 165, 230, 18);
        cardPanel.add(lblNombre);

        txtNmbr = new JTextField(nombre);
        txtNmbr.setFont(fontInputs);
        txtNmbr.setBackground(colorBgInput);
        txtNmbr.setForeground(colorTxtInput);
        txtNmbr.setBorder(new CompoundBorder(new LineBorder(new Color(180, 180, 180), 1, true), new EmptyBorder(0, 10, 0, 10)));
        txtNmbr.setBounds(44, 185, 230, 34);
        cardPanel.add(txtNmbr);

        JLabel lblApellido = new JLabel("APELLIDO");
        lblApellido.setForeground(Color.BLACK);
        lblApellido.setFont(fontLabels);
        lblApellido.setBounds(309, 165, 230, 18);
        cardPanel.add(lblApellido);

        txtAplld = new JTextField(apellido);
        txtAplld.setFont(fontInputs);
        txtAplld.setBackground(colorBgInput);
        txtAplld.setForeground(colorTxtInput);
        txtAplld.setBorder(borderInput);
        txtAplld.setBounds(309, 185, 230, 34);
        cardPanel.add(txtAplld);

        // --- FILA 2: FECHA Y EMAIL ---
        JLabel lblFecha = new JLabel("FECHA NACIMIENTO (dd/mm/yyyy)");
        lblFecha.setForeground(Color.BLACK);
        lblFecha.setFont(fontLabels);
        lblFecha.setBounds(44, 235, 230, 18);
        cardPanel.add(lblFecha);

        txtFechaNacmnt = new JTextField(fechaTexto);
        txtFechaNacmnt.setFont(fontInputs);
        txtFechaNacmnt.setBackground(colorBgInput);
        txtFechaNacmnt.setForeground(colorTxtInput);
        txtFechaNacmnt.setBorder(borderInput);
        txtFechaNacmnt.setBounds(44, 255, 230, 34);
        cardPanel.add(txtFechaNacmnt);

        JLabel lblEmail = new JLabel("EMAIL (No modificable)");
        lblEmail.setForeground(Color.BLACK);
        lblEmail.setFont(fontLabels);
        lblEmail.setBounds(309, 235, 230, 18);
        cardPanel.add(lblEmail);

        txtEmail = new JTextField(email);
        txtEmail.setFont(fontInputs);
        txtEmail.setBackground(colorBgDisabled);
        txtEmail.setForeground(Color.DARK_GRAY);
        txtEmail.setBorder(borderInput);
        txtEmail.setEditable(false);
        txtEmail.setBounds(309, 255, 230, 34);
        cardPanel.add(txtEmail);

        // --- FILA 3: CONTRASEÑA ---
        JLabel lblContr = new JLabel("CONTRASEÑA");
        lblContr.setForeground(Color.BLACK);
        lblContr.setFont(fontLabels);
        lblContr.setBounds(44, 305, 495, 18);
        cardPanel.add(lblContr);

        passwordField = new JPasswordField(contraseña);
        passwordField.setFont(fontInputs);
        passwordField.setBackground(colorBgInput);
        passwordField.setForeground(colorTxtInput);
        passwordField.setBorder(borderInput);
        passwordField.setBounds(44, 325, 495, 34);
        cardPanel.add(passwordField);

        // --- FILA 4: BOTONES ---
        JButton btnGuardar = new JButton("GUARDAR CAMBIOS");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(249, 205, 125));
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setBounds(70, 440, 210, 42);
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nvNmbr = txtNmbr.getText().trim();
                String nvAplld = txtAplld.getText().trim();
                String nvCntrsñ = new String(passwordField.getPassword()).trim();
                String nvFecha = txtFechaNacmnt.getText().trim();

                if (nvNmbr.isEmpty() || nvAplld.isEmpty() || nvCntrsñ.isEmpty() || nvFecha.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Timestamp nvFch = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date fecha = sdf.parse(nvFecha);
                    nvFch = Timestamp.of(fecha);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Fecha inválida. Usa el formato dd/MM/yyyy", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (usu != null) {
                    DocumentSnapshot nvUsu = controlador.nvsDatos(usu.getId(), nvNmbr, nvAplld, nvFch, nvCntrsñ);
                    if (nvUsu != null) {
                        JOptionPane.showMessageDialog(null, "¡Datos actualizados correctamente!");
                        PanelUsuario panelUsu = new PanelUsuario(nvUsu, todosLosWorkouts);
                        panelUsu.setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar los datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        cardPanel.add(btnGuardar);

        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setBounds(305, 440, 210, 42);
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PanelUsuario atras = new PanelUsuario(usu, todosLosWorkouts);
                atras.setVisible(true);
                dispose();
            }
        });
        cardPanel.add(btnCancelar);
    }
}