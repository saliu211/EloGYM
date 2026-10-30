package modelo;

import java.io.Serializable;
import java.util.List;

public class Workouts implements Serializable{


	private static final long serialVersionUID = 1L;
	private String id;
	private String nombre;
	private String descripcion;
	private int nivel;
	private int numEjercicios;
	private String videoUrl; 
	List <Ejercicios> ejer;
	
	
	public Workouts() {
	}


	public Workouts(String id, String nombre, String descripcion, int nivel, int numEjercicios, String videoUrl) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.nivel = nivel;
		this.numEjercicios = numEjercicios;
		this.videoUrl = videoUrl;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public int getNivel() {
		return nivel;
	}


	public void setNivel(int nivel) {
		this.nivel = nivel;
	}


	public int getNumEjercicios() {
		return numEjercicios;
	}


	public void setNumEjercicios(int numEjercicios) {
		this.numEjercicios = numEjercicios;
	}


	public String getVideoUrl() {
		return videoUrl;
	}


	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}


	public List<Ejercicios> getEjer() {
		return ejer;
	}


	public void setEjer(List<Ejercicios> ejer) {
		this.ejer = ejer;
	}


	@Override
	public String toString() {
		return "Workouts [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", nivel=" + nivel
				+ ", numEjercicios=" + numEjercicios + ", videoUrl=" + videoUrl + ", ejer=" + ejer + "]";
	}


	
	
	
	
	
	
	
	

}
