package controlador;

import java.util.List;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import conexion.Conexion;

public class Controlador {
	
	/* ================ METODO PARA VERIFICACION DE USUARIO DEL LOGIN ================ */
	/* Utilizamos email y contraseña para la verficacion. */
	
	public boolean verificarUsuario (String email, String contraseña) {
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
		} catch (Exception e ) {
			usuarioValido = false;
		}
		return usuarioValido;
	}

}
