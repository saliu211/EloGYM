package modelo;

import java.io.Serializable;
import java.util.List;

public class Ejercicios implements Serializable{


	private static final long serialVersionUID = 1L;
	private String nombre;
	private String descripcion;
	private int tiempoDescanso;
	List <Series> serie;
	
	public Ejercicios() {
		super();
	}

	public Ejercicios(String nombre, String descripcion, int tiempoDescanso, List<Series> serie) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.tiempoDescanso = tiempoDescanso;
		this.serie = serie;
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

	public int getTiempoDescanso() {
		return tiempoDescanso;
	}

	public void setTiempoDescanso(int tiempoDescanso) {
		this.tiempoDescanso = tiempoDescanso;
	}

	public List<Series> getSerie() {
		return serie;
	}

	public void setSerie(List<Series> serie) {
		this.serie = serie;
	}
	
	//Calcular la duracion de cada serie, mas los descansos entre series, para obtener la duracion de cada ejercicio.
	public int getDuracionTotal() {
	    int total = 0;
	    if (serie != null && !serie.isEmpty()) {
	        for (Series s : serie) {
	            total += s.getDuracion();
	        }
	        total += serie.size() * tiempoDescanso;
	    }
	    return total;
	}

	@Override
	public String toString() {
		return "Ejercicios [nombre=" + nombre + ", descripcion=" + descripcion + ", tiempoDescanso=" + tiempoDescanso
				+ ", serie=" + serie + "]";
	}
	
	
	
	
	

}
