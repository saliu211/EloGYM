package conexion;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public class Conexion {
	
	 public static Firestore conectar() throws IOException {
	        FileInputStream srvcAccnt = new FileInputStream("usuarios.json");

	        FirestoreOptions opciones = FirestoreOptions.getDefaultInstance().toBuilder()
	                .setProjectId("elogym-grupo5")
	                .setCredentials(GoogleCredentials.fromStream(srvcAccnt))
	                .build();

	        return opciones.getService();
	    }


}
