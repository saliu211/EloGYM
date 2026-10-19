package modelo;

import java.io.Serializable;

public class Series implements Serializable{

	private static final long serialVersionUID = 1L;
	private String nombre;
	private String imagenUrl;
	private int numReps;
	private int duracion;
	
	
	public Series() {
		super();
	}


	public Series(String nombre, String imagenUrl, int numReps, int duracion) {
		super();
		this.nombre = nombre;
		this.imagenUrl = imagenUrl;
		this.numReps = numReps;
		this.duracion = duracion;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getImagenUrl() {
		return imagenUrl;
	}


	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}


	public int getNumReps() {
		return numReps;
	}


	public void setNumReps(int numReps) {
		this.numReps = numReps;
	}


	public int getDuracion() {
		return duracion;
	}


	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}


	@Override
	public String toString() {
		return "Series [nombre=" + nombre + ", imagenUrl=" + imagenUrl + ", numReps=" + numReps + ", duracion="
				+ duracion + "]";
	}
	
	
	
	

}
