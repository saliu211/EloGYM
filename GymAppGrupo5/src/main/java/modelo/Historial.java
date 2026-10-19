package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Historial implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nombreWorkout;
	private int nivelWorkout;
	private int tiempoTotal;
	private int tiempoPrevisto;
	private LocalDate fecha;
	private int porcentajeCompletado;

	public Historial() {

	}

	public Historial(String nombreWorkout, int nivelWorkout, int tiempoTotal, int tiempoPrevisto, LocalDate fecha, int porcentajeCompletado) {
		super();
		this.nombreWorkout = nombreWorkout;
		this.nivelWorkout = nivelWorkout;
		this.tiempoTotal = tiempoTotal;
		this.tiempoPrevisto = tiempoPrevisto;
		this.fecha = fecha;
		this.porcentajeCompletado = porcentajeCompletado;
	}
	
	private String nivelTexto (int nivel) {
		switch (nivel) {
		case 0: return "Principiante";
		case 1: return "Intermedio";
		case 2: return "Avanzado";
		default: return "Desconocido";
		}
	}

	public String getNombreWorkout() {
		return nombreWorkout;
	}

	public void setNombreWorkout(String nombreWorkout) {
		this.nombreWorkout = nombreWorkout;
	}

	public int getNivelWorkout() {
		return nivelWorkout;
	}

	public void setNivelWorkout(int nivelWorkout) {
		this.nivelWorkout = nivelWorkout;
	}

	public int getTiempoTotal() {
		return tiempoTotal;
	}

	public void setTiempoTotal(int tiempoTotal) {
		this.tiempoTotal = tiempoTotal;
	}

	public int getTiempoPrevisto() {
		return tiempoPrevisto;
	}

	public void setTiempoPrevisto(int tiempoPrevisto) {
		this.tiempoPrevisto = tiempoPrevisto;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public int getPorcentajeCompletado() {
		return porcentajeCompletado;
	}

	public void setPorcentajeCompletado(int porcentajeCompletado) {
		this.porcentajeCompletado = porcentajeCompletado;
	}
	
	

}
