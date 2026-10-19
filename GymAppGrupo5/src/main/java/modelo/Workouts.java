package modelo;

import java.io.Serializable;
import java.util.List;

public class Workouts implements Serializable{


	private static final long serialVersionUID = 1L;
	private String nombre;
	private String descripcion;
	private int nivel;
	private String videoUrl; 
	List <Ejercicios> ejer;
	
	
	public Workouts() {
		super();
	}


	public Workouts(String nombre, String descripcion, int nivel, String videoUrl) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.nivel = nivel;
		this.videoUrl = videoUrl;
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
		return "Workouts [nombre=" + nombre + ", descripcion=" + descripcion + ", nivel=" + nivel + ", videoUrl="
				+ videoUrl + ", ejer=" + ejer + "]";
	}
	
	
	
	
	
	
	
	

}
