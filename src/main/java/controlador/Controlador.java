package controlador;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import conexion.Conexion;
import modelo.Ejercicios;
import modelo.Historial;
import modelo.Series;
import modelo.Usuario;
import modelo.Workouts;

public class Controlador {

    private static final int NIVEL_MAXIMO = 2;

    /* ================ VERIFICACION DE USUARIO LOGIN ================ */
    public boolean verificarUsuario(String email, String contraseña) {
        boolean usuarioValido = false;
        try {
            Firestore db = Conexion.conectar();
            ApiFuture<QuerySnapshot> future = db.collection("usuarios")
                    .whereEqualTo("email", email)
                    .whereEqualTo("contraseña", contraseña)
                    .get();

            List<QueryDocumentSnapshot> cliente = future.get().getDocuments();

            if (cliente != null && !cliente.isEmpty()) {
                usuarioValido = true;
            }
        } catch (Exception e) {
            usuarioValido = false;
            e.printStackTrace();
        }
        return usuarioValido;
    }

    /* ================ REGISTRO DE USUARIO ================ */
    // IMPORTANTE: al llamar a este método desde la vista de registro, pasar nivelActual = 0
    public boolean registrarUsuario(String id, String nombreUsu, String apellidoUsu, String email, String contraseña,
            int nivelActual, Timestamp fechaNacTimestamp) {
        boolean registrado = false;
        try {
            Firestore db = Conexion.conectar();
            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombreUsu);
            datos.put("apellido", apellidoUsu);
            datos.put("email", email);
            datos.put("contraseña", contraseña);
            datos.put("nivel", nivelActual);
            datos.put("fechaNacimiento", fechaNacTimestamp);

            QuerySnapshot snapshot = db.collection("usuarios").whereEqualTo("email", email).get().get();

            if (!snapshot.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ya existe un usuario con ese email", "Inténtelo con otro usuario",
                        JOptionPane.WARNING_MESSAGE);
                registrado = false;
            } else {
                ApiFuture<WriteResult> usuario = db.collection("usuarios").document(id).set(datos);
                usuario.get();
                registrado = true;
            }
        } catch (Exception e) {
            registrado = false;
            e.printStackTrace();
        }
        return registrado;
    }

    /* ================ MODIFICAR DATOS DEL USUARIO (nvsDatos) ================ */
    public DocumentSnapshot nvsDatos(String id, String nvNmbr, String nvAplld, Timestamp nvFch, String nvCntrsñ) {
        try {
            Firestore db = Conexion.conectar();

            // Preparar mapa con los campos a actualizar
            Map<String, Object> actualizaciones = new HashMap<>();
            actualizaciones.put("nombre", nvNmbr);
            actualizaciones.put("apellido", nvAplld);
            actualizaciones.put("fechaNacimiento", nvFch);
            actualizaciones.put("contraseña", nvCntrsñ);

            // Actualizar documento en Firestore de forma sincrónica
            db.collection("usuarios").document(id).update(actualizaciones).get();

            // Retornar la nueva instantánea del documento actualizado
            return db.collection("usuarios").document(id).get().get();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ================ NIVEL DEL USUARIO ================ */

    /** Devuelve el nivel del usuario (0 si no tiene el campo). */
    public int obtenerNivelUsuario(DocumentSnapshot usu) {
        if (usu == null) {
            return 0;
        }
        Long n = usu.getLong("nivel");
        return n == null ? 0 : n.intValue();
    }

    /** ¿Puede el usuario hacer este workout? */
    public boolean puedeRealizarWorkout(DocumentSnapshot usu, Workouts w) {
        return obtenerNivelUsuario(usu) >= w.getNivel();
    }

    /**
     * Sube de nivel si el usuario completó un workout de SU nivel actual.
     * Devuelve el DocumentSnapshot actualizado del usuario (o null si hubo error).
     */
    public DocumentSnapshot subirNivelSiCorresponde(String idUsuario, int nivelWorkout, int porcentaje) {
        try {
            Firestore db = Conexion.conectar();
            DocumentSnapshot usuActual = db.collection("usuarios").document(idUsuario).get().get();
            int nivelUsuario = obtenerNivelUsuario(usuActual);

            if (nivelWorkout == nivelUsuario && porcentaje >= 100 && nivelUsuario < NIVEL_MAXIMO) {
                db.collection("usuarios").document(idUsuario).update("nivel", nivelUsuario + 1).get();
            }
            return db.collection("usuarios").document(idUsuario).get().get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ================ CARGAR WORKOUTS DESDE FIRESTORE ================ */
    public List<Workouts> cargarWorkoutsDesdeFirestore(Firestore db) {
        List<Workouts> workoutsDisponibles = new ArrayList<>();
        try {
            List<QueryDocumentSnapshot> docsWorkouts = db.collection("workouts").get().get().getDocuments();
            for (QueryDocumentSnapshot doc : docsWorkouts) {

                int numEjercicios = parseFirestoreInt(doc, "numEjercicios");
                int nivel = parseFirestoreInt(doc, "nivel");

                Workouts work = new Workouts(doc.getId(), doc.getString("nombre"), doc.getString("descripcion"), nivel,
                        numEjercicios, doc.getString("videoUrl"));

                List<Ejercicios> ejercicios = new ArrayList<>();
                List<QueryDocumentSnapshot> docsEjercicios = db.collection("workouts").document(work.getId())
                        .collection("ejercicios").get().get().getDocuments();

                for (QueryDocumentSnapshot ejers : docsEjercicios) {
                    List<Series> series = new ArrayList<>();
                    List<QueryDocumentSnapshot> docsSeries = ejers.getReference().collection("series").get().get()
                            .getDocuments();

                    for (QueryDocumentSnapshot seri : docsSeries) {
                        Series serie = new Series(seri.getString("nombre"), seri.getString("imagenUrl"),
                                parseFirestoreInt(seri, "numReps"), parseFirestoreInt(seri, "duracion"));
                        series.add(serie);
                    }

                    Ejercicios ejercicio = new Ejercicios(ejers.getString("nombre"), ejers.getString("descripcion"),
                            parseFirestoreInt(ejers, "tiempoDescanso"), series);
                    ejercicios.add(ejercicio);
                }

                work.setEjer(ejercicios);
                workoutsDisponibles.add(work);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workoutsDisponibles;
    }

    // Auxiliar para parseo seguro de enteros
    private int parseFirestoreInt(DocumentSnapshot doc, String campo) {
        int resultado = 0;
        Object val = doc.get(campo);

        if (val instanceof Number) {
            resultado = ((Number) val).intValue();
        } else if (val instanceof String) {
            try {
                resultado = Integer.parseInt((String) val);
            } catch (NumberFormatException e) {
                resultado = 0;
            }
        }
        return resultado;
    }

    /* ================ HISTORIAL DE WORKOUTS ================ */
    public List<Historial> obtenerHistorialWorkouts(String id) {
        List<Historial> historialList = new ArrayList<>();
        try {
            Firestore db = Conexion.conectar();
            List<QueryDocumentSnapshot> documentos = db.collection("usuarios")
                    .document(id)
                    .collection("historial")
                    .get()
                    .get()
                    .getDocuments();

            for (QueryDocumentSnapshot doc : documentos) {
                java.util.Date fechaDate = doc.getDate("fecha");
                LocalDate fecha = LocalDate.now();
                if (fechaDate != null) {
                    fecha = fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }

                Historial hist = new Historial(doc.getString("id"), doc.getString("nombre"),
                        parseFirestoreInt(doc, "nivelWorkout"), doc.getString("nivelTexto"),
                        parseFirestoreInt(doc, "tiempoTotal"), parseFirestoreInt(doc, "tiempoPrevisto"), fecha,
                        parseFirestoreInt(doc, "porcentajeCompletado"));
                historialList.add(hist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historialList;
    }

    /* ================ BACKUP DE USUARIO ================ */
    public boolean guardarBackupUsuario(Usuario usuario, List<Workouts> workoutsDisponibles,
            ArrayList<Historial> historialList) {
        boolean exito = false;
        try {
            Files.createDirectories(Paths.get("backups"));

            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("backups/usuarios.dat"))) {
                output.writeObject(usuario);
            }

            escribirWorkoutsEnArchivo(workoutsDisponibles);

            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("backups/historial.dat"))) {
                for (Historial h : historialList) {
                    output.writeObject(h);
                }
            }

            exito = true;
            System.out.println("Backup generado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al generar backup:");
            e.printStackTrace();
        }
        return exito;
    }

    /* ================ GUARDAR WORKOUT EN EL HISTORIAL ================ */
    public boolean guardarHistorial(String idUsuario, Workouts w, int tiempoTotal, int tiempoPrevisto, int porcentaje) {
        boolean guardado = false;
        try {
            Firestore db = Conexion.conectar();

            // 1. Obtener los documentos actuales de la subcolección para calcular el siguiente número
            List<QueryDocumentSnapshot> documentos = db.collection("usuarios")
                    .document(idUsuario)
                    .collection("historial")
                    .get().get().getDocuments();

            int siguienteNum = documentos.size() + 1;
            String idDocumentoHistorial = "historial" + siguienteNum;

            // Traducir el número de nivel al texto
            String nivelTextoStr;
            switch (w.getNivel()) {
                case 0:  nivelTextoStr = "Principiante"; break;
                case 1:  nivelTextoStr = "Intermedio"; break;
                case 2:  nivelTextoStr = "Avanzado"; break;
                default: nivelTextoStr = "Principiante"; break;
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("id", w.getId());
            datos.put("nombre", w.getNombre());
            datos.put("nivelWorkout", w.getNivel());
            datos.put("nivelTexto", nivelTextoStr);
            datos.put("tiempoTotal", tiempoTotal);
            datos.put("tiempoPrevisto", tiempoPrevisto);
            datos.put("fecha", Timestamp.now());
            datos.put("porcentajeCompletado", porcentaje);

            db.collection("usuarios").document(idUsuario).collection("historial").document(idDocumentoHistorial)
                    .set(datos)
                    .get();
            guardado = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guardado;
    }

    public int borrarHistorialVacio(String idUsuario) {
        int borrados = 0;
        try {
            Firestore db = Conexion.conectar();
            List<QueryDocumentSnapshot> docs = db.collection("usuarios")
                    .document(idUsuario)
                    .collection("historial")
                    .get().get().getDocuments();

            for (QueryDocumentSnapshot doc : docs) {
                Long pct = doc.getLong("porcentajeCompletado");
                if (pct == null || pct == 0) {
                    doc.getReference().delete().get();
                    borrados++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return borrados;
    }

    public static void escribirWorkoutsEnArchivo(List<Workouts> workouts) throws IOException {
        String ruta = "backups/workouts.dat";
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(ruta))) {
            for (Workouts workout : workouts) {
                output.writeObject(workout);
            }
            System.out.println("Workouts guardados en: " + ruta);
        }
    }

    /* ================ VERIFICAR CORREO ================ */
    public static boolean verificarGmail(String email) {
        boolean existe = false;
        try {
            Firestore db = Conexion.conectar();
            existe = !db.collection("usuarios").whereEqualTo("email", email).get().get().isEmpty();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar el correo.");
            e.printStackTrace();
            existe = true;
        }
        return existe;
    }

    /* ================ EXTRAER DATOS DEL USUARIO ================ */
    public ArrayList<String> recogerDatosUsuario(DocumentSnapshot nvUsu) {
        ArrayList<String> datos = new ArrayList<>();

        if (nvUsu == null || !nvUsu.exists()) {
            return datos;
        }

        String nombre = nvUsu.getString("nombre");
        String apellido = nvUsu.getString("apellido");
        if (apellido == null) {
            apellido = nvUsu.getString("apellidos");
        }
        String email = nvUsu.getString("email");
        String contraseña = nvUsu.getString("contraseña");

        String fechaTexto = "";
        Object fchRecibida = nvUsu.get("fechaNacimiento");
        if (fchRecibida instanceof Timestamp) {
            Timestamp ts = (Timestamp) fchRecibida;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fechaTexto = sdf.format(ts.toDate());
        } else if (fchRecibida instanceof String) {
            fechaTexto = (String) fchRecibida;
        }

        String nivelTexto = "";
        Long nivel = nvUsu.getLong("nivel");
        if (nivel != null) {
            nivelTexto = String.valueOf(nivel);
        }

        datos.add("Nombre: " + nombre);
        datos.add("Apellido: " + apellido);
        datos.add("Email: " + email);
        datos.add("Contraseña: " + contraseña);
        datos.add("Fecha de nacimiento: " + fechaTexto);
        datos.add("Nivel: " + nivelTexto);

        return datos;
    }

    /* ================ TABLA DE WORKOUTS ================ */

    /** Versión antigua (sin marcar bloqueados). Se mantiene por compatibilidad. */
    public List<Workouts> mostrarWorkoutsPorNivel(int nivel, DefaultTableModel modelo, List<Workouts> todos) {
        return mostrarWorkoutsPorNivel(nivel, modelo, todos, Integer.MAX_VALUE);
    }

    /** Muestra los workouts del nivel elegido (-1 = todos) y marca con 🔒 los que el usuario aún no puede hacer. */
    public List<Workouts> mostrarWorkoutsPorNivel(int nivel, DefaultTableModel modelo, List<Workouts> todos,
            int nivelUsuario) {
        List<Workouts> filtrados = new ArrayList<>();
        modelo.setRowCount(0);
        for (Workouts w : todos) {
            if (nivel == -1 || w.getNivel() == nivel) { // -1 = "Todos"
                filtrados.add(w);
                String textoVideo = (w.getVideoUrl() != null && !w.getVideoUrl().trim().isEmpty())
                        ? "Video "
                        : "Sin Video";
                String nombre = w.getNivel() > nivelUsuario ? "🔒 " + w.getNombre() : w.getNombre();
                modelo.addRow(new Object[] {
                    w.getNivel(),
                    nombre,
                    w.getDescripcion(),
                    textoVideo
                });
            }
        }
        return filtrados;
    }

    public void mostrarEjerciciosPorWorkout(Workouts workoutSeleccionado, DefaultTableModel modeloEjers) {
        modeloEjers.setRowCount(0);

        if (workoutSeleccionado != null && workoutSeleccionado.getEjer() != null) {
            for (Ejercicios ej : workoutSeleccionado.getEjer()) {
                int t = ej.getDuracionTotal();
                modeloEjers.addRow(new Object[] {
                    ej.getNombre(),
                    ej.getDescripcion(),
                    String.format("%d:%02d", t / 60, t % 60)
                });
            }
        }
    }

}