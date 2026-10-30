package conexion;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

/**
 * Clase encargada de establecer y gestionar la conexión con Google Cloud Firestore.
 * Implementa el patrón Singleton para reutilizar la misma instancia de la base de datos.
 */
public class Conexion {
	
    // Variable estática para almacenar la instancia única de Firestore en memoria
    private static Firestore db;

    /**
     * Establece o devuelve la conexión activa a Firestore.
     * Es un método sincronizado (synchronized) para prevenir problemas de hilos concurrentes (Thread-Safe).
     * 
     * @return Instancia activa de Firestore.
     * @throws IOException Si ocurre un error al leer el archivo de claves usuarios.json.
     */
    public static synchronized Firestore conectar() throws IOException {
        if (db == null) {
            // Lee las credenciales de servicio en formato JSON desde la raíz del proyecto
            FileInputStream srvcAccnt = new FileInputStream("usuarios.json");

            // Configura las opciones de Firebase apuntando al ID del proyecto
            FirestoreOptions opciones = FirestoreOptions.getDefaultInstance().toBuilder()
                    .setProjectId("elogym-grupo5")
                    .setCredentials(GoogleCredentials.fromStream(srvcAccnt))
                    .build();

            // Obtiene la instancia del servicio de Firestore
            db = opciones.getService();
        }
        return db; // Retorna la instancia única creada
    }
}